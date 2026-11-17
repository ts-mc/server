#!/usr/bin/env python3
import argparse,hashlib,importlib.util,json,re,time
from pathlib import Path
REPO=Path(__file__).resolve().parents[3]
spec=importlib.util.spec_from_file_location('tsxmas_sftp',REPO/'projects/paper26-upgrade/sftp-deploy.py');sftp=importlib.util.module_from_spec(spec);spec.loader.exec_module(sftp)
BASE=REPO/'.backups/tsxmas-map-audit';Q=sftp.quote

def digest(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def clean_names(text):return {line.strip().rstrip('/') for line in text.splitlines() if line.strip()}
def stage():
 prepared=json.loads((BASE/'prepared.json').read_text());world=REPO/prepared['target']
 if prepared['runtime_load_tested']:raise RuntimeError('Unexpected runtime state')
 properties=(REPO/'.backups/paper26-live-deploy/remote-before/server.properties').read_text()
 primary=re.search(r'(?m)^level-name=(.+)$',properties).group(1).strip()
 if not re.fullmatch(r'[A-Za-z0-9_-]+',primary):raise RuntimeError('Review primary world path')
 root=clean_names(sftp.batch(['cls -1']))
 if world.name in root:raise RuntimeError('Event world already exists on LIVE; refusing overwrite')
 packs=clean_names(sftp.batch(['cls -1 '+Q(primary+'/datapacks')]))
 if any(n.rsplit('/',1)[-1]=='tsxmas-map-assets' for n in packs):raise RuntimeError('Map asset pack already exists on LIVE; refusing overwrite')
 deployment='tsxmas-map-'+time.strftime('%Y%m%d-%H%M%S');remote='.deploy-staging/'+deployment
 files=[]
 for source in world.rglob('*'):
  if source.is_file():files.append({'source':str(source),'relative':'world/'+source.relative_to(world).as_posix(),'sha256':digest(source)})
 registry=BASE/'registry-pack'
 for source in registry.rglob('*'):
  if source.is_file():files.append({'source':str(source),'relative':'registry/'+source.relative_to(registry).as_posix(),'sha256':digest(source)})
 sftp.batch(['get '+Q('plugins/TSXMAS/locations.yml')+' -o '+Q(BASE/'live-locations-before.yml')])
 existing=(BASE/'live-locations-before.yml').read_bytes();text=existing.decode('utf-8')
 match=re.search(r'(?m)^world:\s*[\'\"]?([a-zA-Z0-9_-]+)',text)
 if not match or match.group(1)!=world.name:raise RuntimeError('LIVE TSXMAS world differs; preserve and review its configuration')
 proposed=text
 old='boundary:\n  configured: false\n  min-x: 0\n  min-y: -64\n  min-z: 0\n  max-x: 0\n  max-y: 320\n  max-z: 0'
 local=(REPO/'plugins/TSXMAS/locations.yml').read_text();block=re.search(r'(?ms)^boundary:\n.*?(?=^points:)',local).group(0).rstrip('\n')
 newline='\r\n' if '\r\n' in text else '\n'
 proposed=proposed.replace(old.replace('\n',newline),block.replace('\n',newline))
 hub=re.search(r'(?m)^  hub:.*$',local).group(0)
 proposed=proposed.replace('  hub: {configured: false}',hub)
 proposed=proposed.replace('# Coordinates intentionally unset. Never fabricate map coordinates or import over an existing world.','# Hub and bounds come from the supplied map; other points require an in-game survey.')
 config=BASE/'live-locations-proposed.yml';config.write_bytes(proposed.encode('utf-8'))
 if config.read_bytes()!=existing:files.append({'source':str(config),'relative':'locations.yml','sha256':digest(config)})
 parents=sorted({remote+'/'+str(Path(f['relative']).parent).replace('\\','/') for f in files},key=lambda x:(x.count('/'),x))
 commands=['mkdir -p '+Q(p) for p in parents]
 commands+=['put '+Q(f['source'])+' -o '+Q(remote+'/'+f['relative']) for f in files]
 sftp.batch(commands)
 verify=BASE/'staged-readback'/deployment;commands=[]
 for f in files:
  target=verify/f['relative'];target.parent.mkdir(parents=True,exist_ok=True);commands.append('get '+Q(remote+'/'+f['relative'])+' -o '+Q(target))
 sftp.batch(commands)
 for f in files:
  if digest(verify/f['relative'])!=f['sha256']:raise RuntimeError('Map staging readback mismatch')
 result={'id':deployment,'remote_stage':remote,'world':world.name,'primary_world':primary,'files':files,'locations_before_sha256':hashlib.sha256(existing).hexdigest(),'staged_sha256_verified':True,'active':False,'java_started':False,'runtime_load_tested':False}
 (BASE/'sftp-map-stage.json').write_text(json.dumps(result,indent=2)+'\n')
 print('Staged and SHA-256 verified '+str(len(files))+' map/asset/config files. Nothing imported; no Java started.')

def activate():
 plan=json.loads((BASE/'sftp-map-stage.json').read_text())
 if plan['active'] or not plan['staged_sha256_verified']:raise RuntimeError('Invalid staging state')
 sftp.inspect();state=json.loads((sftp.BASE/'inspection.json').read_text())
 if not state['log_indicates_stopped']:raise RuntimeError('LIVE is running; activation refused')
 root=clean_names(sftp.batch(['cls -1']))
 if plan['world'] in root:raise RuntimeError('Event target appeared on LIVE; refusing overwrite')
 target=plan['primary_world']+'/datapacks/tsxmas-map-assets'
 packs=clean_names(sftp.batch(['cls -1 '+Q(plan['primary_world']+'/datapacks')]))
 if any(n.rsplit('/',1)[-1]=='tsxmas-map-assets' for n in packs):raise RuntimeError('Asset pack target appeared on LIVE; refusing overwrite')
 config=BASE/'locations-preactivate.yml';sftp.batch(['get '+Q('plugins/TSXMAS/locations.yml')+' -o '+Q(config)])
 if digest(config)!=plan['locations_before_sha256']:raise RuntimeError('LIVE locations changed; preserve and review')
 backup='.deploy-backups/'+plan['id'];commands=['mkdir -p '+Q(backup)]
 commands+=['mv '+Q(plan['remote_stage']+'/world')+' '+Q(plan['world']),'mv '+Q(plan['remote_stage']+'/registry')+' '+Q(target)]
 if any(f['relative']=='locations.yml' for f in plan['files']):
  commands+=['mv '+Q('plugins/TSXMAS/locations.yml')+' '+Q(backup+'/locations.yml'),'mv '+Q(plan['remote_stage']+'/locations.yml')+' '+Q('plugins/TSXMAS/locations.yml')]
 sftp.batch(commands)
 plan.update(active=True,backup=backup,world_imported=False,java_started=False)
 (BASE/'sftp-map-stage.json').write_text(json.dumps(plan,indent=2)+'\n')
 print('Placed the separate world and namespaced registry pack. Existing terrain, player data and registrations unchanged; console import remains pending.')

def verify():
 plan=json.loads((BASE/'sftp-map-stage.json').read_text())
 if not plan['active']:raise RuntimeError('Map placement has not completed')
 destination=BASE/'active-readback'/plan['id'];commands=[]
 for f in plan['files']:
  relative=f['relative']
  if relative.startswith('world/'):remote=plan['world']+'/'+relative[len('world/'):]
  elif relative.startswith('registry/'):remote=plan['primary_world']+'/datapacks/tsxmas-map-assets/'+relative[len('registry/'):]
  elif relative=='locations.yml':remote='plugins/TSXMAS/locations.yml'
  else:raise RuntimeError('Unexpected manifest entry')
  target=destination/relative;target.parent.mkdir(parents=True,exist_ok=True)
  commands.append('get '+Q(remote)+' -o '+Q(target))
 sftp.batch(commands)
 for f in plan['files']:
  if digest(destination/f['relative'])!=f['sha256']:raise RuntimeError('Active readback mismatch: '+f['relative'])
 plan['active_sha256_verified']=True
 (BASE/'sftp-map-stage.json').write_text(json.dumps(plan,indent=2)+'\n')
 print('Active placement SHA-256 verified: '+str(len(plan['files']))+' files. No Java started or world import executed.')

def inspect_import():
 pass
 import yaml
 plan=json.loads((BASE/'sftp-map-stage.json').read_text())
 destination=BASE/'import-inspection';destination.mkdir(parents=True,exist_ok=True)
 dimension=plan['primary_world']+'/dimensions/minecraft/'+plan['world']
 listings={}
 root_names=clean_names(sftp.batch(['cls -1']))
 for remote in [dimension,dimension+'/region',dimension+'/entities',dimension+'/poi']:
  listings[remote]=sftp.batch(['cls -1 '+Q(remote)]).splitlines()
 commands=['get '+Q('plugins/Multiverse-Core/worlds.yml')+' -o '+Q(destination/'multiverse-worlds.yml'),
           'get '+Q('logs/latest.log')+' -o '+Q(destination/'latest.log')]
 chunks=[]
 for f in plan['files']:
  if f['relative'].startswith('world/dimensions/minecraft/overworld/') and f['relative'].endswith('.mca'):
   relative=f['relative'][len('world/dimensions/minecraft/overworld/'):]
   target=destination/relative;target.parent.mkdir(parents=True,exist_ok=True)
   commands.append('get '+Q(dimension+'/'+relative)+' -o '+Q(target))
   chunks.append((relative,target,f['sha256']))
 sftp.batch(commands)
 worlds=yaml.safe_load((destination/'multiverse-worlds.yml').read_text())
 entries=worlds.get('worlds',worlds)
 registration_key=next((key for key,value in entries.items()
                       if key in [plan['world'],'minecraft:'+plan['world']]
                       or isinstance(value,dict) and value.get('read-only',{}).get('legacy-world-name')==plan['world']),None)
 record=entries.get(registration_key)
 if not isinstance(record,dict):raise RuntimeError('Event world is absent from Multiverse registration')
 log=(destination/'latest.log').read_text(errors='replace')
 summary={'world':plan['world'],'storage':dimension,'registered':True,'registration_key':registration_key,
          'legacy_folder_exists':plan['world'] in root_names,
          'multiverse_properties':{k:record[k] for k in ['environment','gamemode','game-mode','gameMode','difficulty','autoload','auto-load','autoLoad','alias'] if k in record},
          'mca_files_read':len(chunks),'mca_hashes_unchanged':sum(digest(p)==h for _,p,h in chunks),
          'mca_hashes_changed':[rel for rel,p,h in chunks if digest(p)!=h],
          'datapack_load_observed':'Found new data pack file/tsxmas-map-assets, loading it automatically' in log,
          'world_import_observed':"World '"+plan['world']+"' imported!" in log,
          'java_started_by_tool':False,'remote_files_changed_by_tool':False}
 (destination/'summary.json').write_text(json.dumps(summary,indent=2)+'\n')
 (destination/'directory-listings.json').write_text(json.dumps(listings,indent=2)+'\n')
 print(json.dumps(summary,indent=2))

if __name__=='__main__':
 p=argparse.ArgumentParser();p.add_argument('operation',choices=['stage','activate','verify','inspect-import']);a=p.parse_args()
 try:{'stage':stage,'activate':activate,'verify':verify,'inspect-import':inspect_import}[a.operation]()
 except Exception as e:raise SystemExit(type(e).__name__+': '+str(e))
