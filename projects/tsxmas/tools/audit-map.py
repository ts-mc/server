#!/usr/bin/env python3
import argparse
import hashlib
import io
import json
from pathlib import Path, PurePosixPath
import stat
import zipfile

parser = argparse.ArgumentParser()
parser.add_argument('archive', type=Path)
args = parser.parse_args()
report = {'sha256': hashlib.sha256(args.archive.read_bytes()).hexdigest(), 'datapack_files': [], 'embedded_resource_packs': [], 'world_metadata': [], 'issues': []}
with zipfile.ZipFile(args.archive) as archive:
    total = 0
    for info in archive.infolist():
        name = info.filename
        path = PurePosixPath(name)
        total += info.file_size
        if path.is_absolute() or '..' in path.parts or '\\' in name or ':' in name or stat.S_ISLNK(info.external_attr >> 16):
            report['issues'].append('Unsafe archive path: ' + name)
        if path.suffix.lower() in ('.jar', '.exe', '.sh', '.bat', '.dll', '.so'):
            report['issues'].append('Executable content: ' + name)
        if info.file_size > 512 * 1024 * 1024 or info.file_size / max(1, info.compress_size) > 1000:
            report['issues'].append('Review expansion size: ' + name)
        if 'datapacks' in path.parts:
            report['datapack_files'].append(name)
        if path.name in ('level.dat', 'level.dat_old'):
            report['world_metadata'].append(name)
        if path.name == 'resources.zip':
            if info.file_size > 64 * 1024 * 1024:
                report['issues'].append('Embedded pack too large for inline inspection: ' + name)
                continue
            with zipfile.ZipFile(io.BytesIO(archive.read(info))) as pack:
                metadata = json.loads(pack.read('pack.mcmeta')) if 'pack.mcmeta' in pack.namelist() else None
                report['embedded_resource_packs'].append({'path': name, 'metadata': metadata, 'entries': len(pack.infolist()), 'namespaces': sorted({PurePosixPath(n).parts[1] for n in pack.namelist() if n.startswith('assets/') and len(PurePosixPath(n).parts) > 2})})
    report['uncompressed_bytes'] = total
    if total > 16 * 1024 * 1024 * 1024:
        report['issues'].append('Archive exceeds 16 GiB expanded; review offline storage requirements')
report['manual_checks'] = ['Read level.dat DataVersion with a trusted NBT viewer', 'Inspect every datapack including nested ZIPs and load/tick functions', 'Review resource licensing and asset collisions', 'Test a copy with the exact Paper version before import']
print(json.dumps(report, indent=2))
raise SystemExit(1 if report['issues'] else 0)
