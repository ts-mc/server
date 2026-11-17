#!/usr/bin/env python3
import argparse,hashlib,json,shutil,zipfile
from pathlib import Path
from nbt import load,dump,plain
p=argparse.ArgumentParser();p.add_argument('--source',default='WeihnachtsMC-Village');p.add_argument('--target',default='tsxmas_christmas_2026');p.add_argument('--seasonal-pack',default='/tmp/tsxmas-christmas-pack.zip');a=p.parse_args()
source=Path(a.source);target=Path(a.target)
if not target.name.startswith('tsxmas_christmas_') or target.exists():raise SystemExit('Refusing existing or non-event target')
if any(p.is_symlink() for p in source.rglob('*')):raise SystemExit('Symlinks require review')
base=Path('.backups/tsxmas-map-audit');audit=json.loads((base/'source-audit.json').read_text())
if audit['errors'] or audit['custom_blocks'] or audit['command_blocks']:raise SystemExit('Map audit requires review')
level=load((source/'level.dat').read_bytes());data=level[2].get('Data',(10,level[2]))[1]
if data['DataVersion'][1]!=5023:raise SystemExit('Unexpected map DataVersion')
target.mkdir()
worldgen_files=['world_gen_settings.dat','game_rules.dat','world_clocks.dat','weather.dat','world_border.dat']
for folder in ['region','entities','poi']:
 src=source/'dimensions/minecraft/overworld'/folder
 if src.exists():shutil.copytree(src,target/'dimensions/minecraft/overworld'/folder)
for name in worldgen_files:
 src=source/'dimensions/minecraft/overworld/data/minecraft'/name
 dst=target/'dimensions/minecraft/overworld/data/minecraft'/name;dst.parent.mkdir(parents=True,exist_ok=True);shutil.copy2(src,dst)
data['LevelName']=(8,target.name)
data['DataPacks']=(10,{'Enabled':(9,(8,['vanilla','file/tsxmas-map-assets','paper'])),'Disabled':(9,(8,[]))})
(target/'level.dat').write_bytes(dump(level))
assets=base/'registry-pack';(assets/'data/weihnachtsmc').mkdir(parents=True,exist_ok=True)
for folder in ['painting_variant','jukebox_song']:
 shutil.copytree(source/'datapacks/weihnachtsmc/data/weihnachtsmc'/folder,assets/'data/weihnachtsmc'/folder,dirs_exist_ok=True)
(assets/'pack.mcmeta').write_text(json.dumps({'pack':{'description':'TheySix Christmas map artwork and music registries; no gameplay or worldgen overrides','pack_format':121,'min_format':121,'max_format':121}},indent=2)+'\n')
shutil.copytree(assets,target/'datapacks/tsxmas-map-assets')
season=Path(a.seasonal_pack);merged=base/'TheySix-Christmas-26.3-merged.zip';conflicts=[]
if season.exists():
 if hashlib.sha1(season.read_bytes()).hexdigest()!='848a7a8e8d2ef149bec10b336b77185cb15936a2':raise SystemExit('Seasonal pack hash mismatch')
 with zipfile.ZipFile(source/'resources.zip') as original,zipfile.ZipFile(season) as christmas,zipfile.ZipFile(merged,'w',zipfile.ZIP_DEFLATED) as out:
  entries={n:original.read(n) for n in original.namelist() if not n.endswith('/')}
  entries.pop('assets/minecraft/sounds.json',None)
  for n in christmas.namelist():
   if n.endswith('/') or n in ['pack.mcmeta','pack.png']:continue
   if n in entries:conflicts.append(n)
   entries[n]=christmas.read(n)
  entries['pack.mcmeta']=json.dumps({'pack':{'description':'TheySix Christmas: WeihnachtsMC venue assets + A Very Minecraft Christmas by ninaxenchanted','pack_format':97,'min_format':97,'max_format':97}},indent=2).encode()
  for n,content in sorted(entries.items()):out.writestr(n,content)
shutil.copy2(source/'resources.zip',target/'resources.zip')
for relative,expected in audit['files'].items():
 if hashlib.sha256((source/relative).read_bytes()).hexdigest()!=expected:raise SystemExit('Source map changed during preparation')
for f in (target/'dimensions/minecraft/overworld').rglob('*.mca'):
 if hashlib.sha256(f.read_bytes()).hexdigest()!=audit['files'][f.relative_to(target).as_posix()]:raise SystemExit('Terrain/entity/POI copy mismatch')
report={'source':source.name,'target':target.name,'data_version':5023,'minecraft':'26.3','spawn':audit['level']['spawn'],'copied_chunks':audit['chunks'],'copied_mca_files':len(list(target.rglob('*.mca'))),'source_unchanged':True,'global_worldgen_overrides_included':False,'original_advancements_included':False,'original_plugin_data_included':False,'java_started':False,'live_uploaded':False,'runtime_load_tested':False,'pack_texture_conflicts':conflicts,'pack_conflict_policy':'Seasonal textures take precedence; map namespaces retained; original background-music mute omitted','merged_pack_sha1':hashlib.sha1(merged.read_bytes()).hexdigest() if merged.exists() else None}
(base/'prepared.json').write_text(json.dumps(report,indent=2)+'\n')
print(json.dumps({k:v for k,v in report.items() if k!='pack_texture_conflicts'},indent=2))
