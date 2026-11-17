#!/usr/bin/env python3

import collections,hashlib,json,zipfile
from pathlib import Path,PurePosixPath
REPO=Path(__file__).resolve().parents[3]
BASE=REPO/'.backups/tsxmas-textures';BASE.mkdir(exist_ok=True)
source=REPO/'WeihnachtsMC-Village/resources.zip'
live=REPO/'.remote-sync/plugins/BetterModel/build.zip'
entries={};conflicts=[]
for path in [source,live]:
 with zipfile.ZipFile(path) as archive:
  if len(set(archive.namelist()))!=len(archive.namelist()):raise SystemExit('Duplicate ZIP entries require review')
  for info in archive.infolist():
   name=info.filename;p=PurePosixPath(name)
   if info.is_dir():continue
   if p.is_absolute() or '..' in p.parts or '\\' in name or info.file_size>64*1024*1024:raise SystemExit('Unsafe ZIP entry')
   if name in ['pack.mcmeta','pack.png']:continue
   if path==source and name=='assets/minecraft/sounds.json':continue
   value=archive.read(name)
   if name in entries and entries[name]!=value:conflicts.append(name)
   entries[name]=value
if conflicts:raise SystemExit('Conflicting LIVE/map assets need review: '+str(conflicts[:20]))
entries['pack.mcmeta']=json.dumps({'pack':{'pack_format':97,'min_format':97,'max_format':97,'description':'TheySix Events: WeihnachtsMC venue + current BetterModel assets (Java 26.3)'}},indent=2).encode()
with zipfile.ZipFile(source) as z:entries['pack.png']=z.read('pack.png')
entries['THEYSIX-CREDITS.txt']=('TheySix Events Christmas venue asset pack\n'
 'Map and included artwork/model resources: WeihnachtsMC Christmas Village\n'
 'https://www.planetminecraft.com/project/weihnachtsmc-christmas-village-with-santa-s-workshop/\n'
 'Map page music credit: Kevin MacLeod (incompetech.com), CC BY 4.0\n'
 'https://creativecommons.org/licenses/by/4.0/\n'
 'Map page painting credit: pixel versions of public-domain artworks\n'
 'Live generated model assets: BetterModel\nhttps://github.com/toxicity188/BetterModel\n'
 'Existing authors retain their rights. This file does not relicense their assets.\n'
 'A Very Minecraft Christmas is sent separately from its official Modrinth CDN;\n'
 'its assets are not redistributed inside this ZIP.\n').encode()
json_count=0;missing=[]
def ref(value,folder,suffix):
 if not isinstance(value,str) or value.startswith('#') or ':' not in value:return
 namespace,path=value.split(':',1)
 if namespace not in ['weihnachtsmc','bettermodel']:return
 target=f'assets/{namespace}/{folder}/{path}{suffix}'
 if target not in entries:missing.append(target)
def scan_models(value):
 if isinstance(value,dict):
  if value.get('type')=='minecraft:model':ref(value.get('model'),'models','.json')
  for sub in value.values():scan_models(sub)
 elif isinstance(value,list):
  for sub in value:scan_models(sub)
for name,value in entries.items():
 if not name.endswith('.json'):continue
 data=json.loads(value);json_count+=1
 if '/models/' in name and isinstance(data,dict):
  ref(data.get('parent'),'models','.json')
  for texture in data.get('textures',{}).values():ref(texture,'textures','.png')
 if '/items/' in name:scan_models(data)
if missing:raise SystemExit('Missing custom asset references: '+str(sorted(set(missing))[:30]))
target=BASE/'TheySix-Christmas-Venue-26.3-v1.zip'
with zipfile.ZipFile(target,'w',zipfile.ZIP_DEFLATED) as out:
 for name,value in sorted(entries.items()):
  info=zipfile.ZipInfo(name,date_time=(2026,11,15,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;out.writestr(info,value)
report={'artifact':str(target.relative_to(REPO)),'sha1':hashlib.sha1(target.read_bytes()).hexdigest(),'sha256':hashlib.sha256(target.read_bytes()).hexdigest(),'bytes':target.stat().st_size,'entries':len(entries),'parsed_json':json_count,'missing_custom_asset_references':len(missing),'asset_conflicts':conflicts,'namespaces':dict(collections.Counter(n.split('/')[1] for n in entries if n.startswith('assets/'))),'sources':{str(p.relative_to(REPO)):hashlib.sha256(p.read_bytes()).hexdigest() for p in [source,live]},'seasonal_pack_embedded':False,'java_started':False}
(BASE/'venue-pack.json').write_text(json.dumps(report,indent=2)+'\n');print(json.dumps(report,indent=2))
