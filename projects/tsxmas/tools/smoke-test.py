#!/usr/bin/env python3
import argparse
import hashlib
import json
from pathlib import Path
import shutil
import subprocess
import time

parser = argparse.ArgumentParser()
parser.add_argument('--java', required=True)
parser.add_argument('--paper', required=True)
parser.add_argument('--directory', required=True)
parser.add_argument('--plugin')
parser.add_argument('--existing-jars')
parser.add_argument('--timeout', type=int, default=180)
parser.add_argument('--copy-minigame-modules', action='store_true')
parser.add_argument('--config-overlay')
parser.add_argument('--status-probe', action='store_true')
parser.add_argument('--port', type=int, default=25674)
parser.add_argument('--accept-eula', action='store_true', required=True)
args = parser.parse_args()
root = Path(args.directory).resolve()
if root.exists():
    raise SystemExit('Refusing existing directory: choose a new isolated staging directory')
root.mkdir(parents=True)
(root / 'plugins').mkdir()
(root / 'eula.txt').write_text('eula=true\n')
(root / 'server.properties').write_text(f'server-ip=127.0.0.1\nserver-port={args.port}\nonline-mode=true\nlevel-name=tsxmas_test\nview-distance=2\nsimulation-distance=2\nmax-players=2\nspawn-protection=0\n')
artifacts = []
excluded = ('TSEvent', 'EssentialsSpawn', 'EssentialsChat', 'AquaSpawn', 'ExcellentEconomy', 'AllBedrockSkins')
if args.existing_jars:
    for jar in sorted(Path(args.existing_jars).glob('*.jar')):
        if jar.name.startswith(excluded):
            continue
        shutil.copy2(jar, root / 'plugins' / jar.name)
        artifacts.append({'file': jar.name, 'sha256': hashlib.sha256(jar.read_bytes()).hexdigest()})
if args.copy_minigame_modules and args.existing_jars:
    for folder in ['modules', 'libraries']:
        source = Path(args.existing_jars) / 'BlueArcade3' / folder
        if source.is_dir():
            shutil.copytree(source, root / 'plugins' / 'BlueArcade3' / folder)
if args.config_overlay:
    for source in Path(args.config_overlay).rglob('*.yml'):
        target = root / 'plugins' / source.relative_to(args.config_overlay)
        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(source, target)
if args.plugin:
    shutil.copy2(args.plugin, root / 'plugins' / Path(args.plugin).name)
commands = ['version', 'plugins', 'tsxmas version', 'tsxmas validate', 'tsxmas debug', 'tsxmas backup', 'tsxmas reload']
log = root / 'console.log'
started = False
status_ping = None
def probe_status(port):
    import socket, struct
    def varint(n):
        n &= 0xffffffff
        result = bytearray()
        while n > 127:
            result.append((n & 127) | 128); n >>= 7
        result.append(n)
        return bytes(result)
    def readint(sock):
        n = 0
        for i in range(5):
            b = sock.recv(1)
            if not b: raise EOFError()
            n |= (b[0] & 127) << (7*i)
            if b[0] < 128: return n
        raise ValueError('Invalid varint')
    try:
        with socket.create_connection(('127.0.0.1', port), timeout=8) as sock:
            host = b'localhost'
            handshake = b'\x00' + varint(-1) + varint(len(host)) + host + struct.pack('>H', port) + b'\x01'
            sock.sendall(varint(len(handshake)) + handshake + b'\x01\x00')
            readint(sock)
            if readint(sock) != 0: raise ValueError('Wrong packet')
            length = readint(sock)
            if length > 1048576: raise ValueError('Oversized status response')
            payload = b''
            while len(payload) < length:
                chunk = sock.recv(length - len(payload))
                if not chunk: raise EOFError()
                payload += chunk
            response = json.loads(payload)
            return {'ok': True, 'version': response.get('version')}
    except Exception as error:
        return {'ok': False, 'error_type': type(error).__name__}
with log.open('w') as output:
    process = subprocess.Popen([args.java, '-Dterminal.jline=false', '-Dterminal.ansi=false', '-Xms512M', '-Xmx3G', '-jar', str(Path(args.paper).resolve()), '--nogui'], cwd=root, stdin=subprocess.PIPE, stdout=output, stderr=subprocess.STDOUT, text=True)
    try:
        deadline = time.monotonic() + args.timeout
        while process.poll() is None and time.monotonic() < deadline:
            content = log.read_text(errors='replace')
            if 'For help, type "help"' in content:
                started = True
                break
            time.sleep(1)
        if started:
            if args.status_probe: status_ping = probe_status(args.port)
            for command in commands:
                process.stdin.write(command + '\n')
                process.stdin.flush()
                time.sleep(1)
        if process.poll() is None:
            process.stdin.write('stop\n')
            process.stdin.flush()
            process.wait(timeout=60)
    finally:
        if process.poll() is None:
            process.terminate()
            try:
                process.wait(timeout=15)
            except subprocess.TimeoutExpired:
                process.kill()
                process.wait()
content = log.read_text(errors='replace')
import re
errors = [line for line in content.splitlines() if re.search(r'Error occurred while (enabling|loading)|Could not load|Unsupported (API|server|Minecraft)|No compatible.*adapter|UnknownDependencyException|NoClassDefFoundError|NoSuchMethodError|UnsupportedClassVersionError', line)]
summary = {'started': started, 'exit_code': process.returncode, 'artifacts': artifacts, 'errors': errors, 'tsxmas_ready': bool(re.search(r'TSXMAS .* ready;', content)), 'production_configs_tested': False, 'players_tested': False, 'status_ping': status_ping}
(root / 'result.json').write_text(json.dumps(summary, indent=2) + '\n')
print(json.dumps({k: v for k, v in summary.items() if k != 'artifacts'}, indent=2))
raise SystemExit(0 if started and not errors and process.returncode == 0 and (not args.plugin or summary['tsxmas_ready']) and (not args.status_probe or status_ping['ok']) else 1)
