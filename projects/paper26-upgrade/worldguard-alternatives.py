import json,urllib.request,hashlib
from pathlib import Path
x=json.load(open('.backups/paper26-upgrade/metadata/worldguard.json'))
root=Path('.backups/paper26-upgrade/alternatives');root.mkdir(exist_ok=True)
for number in ['7.0.20','7.0.19','7.0.18']:
 v=next(v for v in x['versions'] if v['version_number']==number);f=next(f for f in v['files'] if f['primary'])
 with urllib.request.urlopen(f['url'],timeout=60) as r:data=r.read()
 assert hashlib.sha512(data).hexdigest()==f['hashes']['sha512']
 (root/f['filename']).write_bytes(data)
 print(number,f['filename'],flush=True)
