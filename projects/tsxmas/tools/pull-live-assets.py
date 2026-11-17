#!/usr/bin/env python3
import importlib.util,json,time,sys
from pathlib import Path
REPO=Path(__file__).resolve().parents[3]
spec=importlib.util.spec_from_file_location('tsxmas_sftp',REPO/'projects/paper26-upgrade/sftp-deploy.py')
sftp=importlib.util.module_from_spec(spec);spec.loader.exec_module(sftp)
Q=sftp.quote
snapshot=REPO/'.remote-sync';snapshot.mkdir(exist_ok=True)
if sys.argv[1:]==['--generated-pack']:
 target=snapshot/'plugins/BetterModel/build.zip';target.parent.mkdir(parents=True,exist_ok=True)
 sftp.batch(['get '+Q('plugins/BetterModel/build.zip')+' -o '+Q(target)])
 print('Fetched current generated BetterModel ZIP into the ignored comparison snapshot.')
 raise SystemExit(0)
if sys.argv[1:]==['--recent-logs']:
 import re
 names=sorted(n.strip() for n in sftp.batch(['cls -1 logs']).splitlines() if re.fullmatch(r'logs/\d{4}-\d{2}-\d{2}-\d+\.log\.gz',n.strip()))[-3:]
 target=snapshot/'logs';target.mkdir(exist_ok=True)
 sftp.batch(['get '+Q(name)+' -o '+Q(target/Path(name).name) for name in names])
 print('Fetched '+str(len(names))+' recent archived logs into the ignored comparison snapshot.')
 raise SystemExit(0)
if sys.argv[1:]:raise SystemExit('Usage: pull-live-assets.py [--generated-pack|--recent-logs]')
audit=REPO/'.backups/tsxmas-textures'/time.strftime('%Y%m%d-%H%M%S');audit.mkdir(parents=True)
root=sftp.batch(['cls -1']);plugins=sftp.batch(['cls -1 plugins'])
(audit/'root-list.txt').write_text(root);(audit/'plugins-list.txt').write_text(plugins)
commands=[]
for folder in ['BetterModel','TSXMAS','Multiverse-Core','WorldGuard','FancyNpcs','FancyHolograms','Geyser-Spigot']:
 target=snapshot/'plugins'/folder;target.mkdir(parents=True,exist_ok=True)
 commands.append('mirror --parallel=3 --exclude-glob "*.jar" --exclude-glob "*.db*" --exclude-glob "*.sqlite*" --exclude-glob "*.zip" --exclude-glob "*.log*" --exclude-glob "logs/" --exclude-glob "backups/" --exclude-glob "*.pem" --exclude-glob "*.key" '+Q('plugins/'+folder)+' '+Q(target))
for name in ['server.properties','logs/latest.log']:
 target=snapshot/name;target.parent.mkdir(parents=True,exist_ok=True)
 commands.append('get '+Q(name)+' -o '+Q(target))
commands.append('cls -l logs')
result=sftp.batch(commands);(audit/'transfer-list.txt').write_text(result)
(audit/'latest.log').write_bytes((snapshot/'logs/latest.log').read_bytes())
(audit/'server.properties').write_bytes((snapshot/'server.properties').read_bytes())
(REPO/'.backups/tsxmas-textures/latest-inspection.json').write_text(json.dumps({'snapshot':str(snapshot.relative_to(REPO)),'audit':str(audit.relative_to(REPO)),'remote_mutations':False},indent=2)+'\n')
print('Refreshed seven relevant plugin directories, server properties and latest log into the ignored comparison snapshot. No LIVE files changed.')
