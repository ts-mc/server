#!/usr/bin/env python3
import concurrent.futures,hashlib,json,re,shutil,urllib.request,zipfile,io
from pathlib import Path
BASE=Path('.backups/paper26-upgrade');META=BASE/'metadata';DEST=BASE/'candidate/plugins'
MAPPING={'FastAsyncWorldEdit':'fastasyncworldedit','WorldGuard':'worldguard','packetevents':'packetevents','FancyNpcs':'fancynpcs','FancyHolograms':'fancyholograms','nightcore':'nightcore','Multiverse-Core':'multiverse-core','ViaVersion':'viaversion','ViaBackwards':'viabackwards','TAB':'tab-was-taken','SkinsRestorer':'skinsrestorer','PlaceholderAPI':'placeholderapi','DeluxeMenus':'deluxemenus','DiscordSRV':'discordsrv','ItemJoin':'itemjoin','Maintenance':'maintenance','MiniMOTD':'minimotd','AdvancedBanZ':'advancedbanz','VoidGen':'voidgen'}
EXCLUDED={'TSEvent','EssentialsSpawn','EssentialsChat','AquaSpawn','ExcellentEconomy','AllBedrockSkins'}
def descriptor(data):
 with zipfile.ZipFile(io.BytesIO(data)) as z:
  n=next((x for x in ['plugin.yml','paper-plugin.yml'] if x in z.namelist()),None)
  if not n:raise ValueError('Not a plugin JAR')
  text=z.read(n).decode()
  def field(k):
   m=re.search('^'+k+r':\s*(.+)$',text,re.M)
   return m.group(1).strip().strip('\"\'') if m else ''
  return field('name'),field('version')
def download(job):
 name,oldpath,file,release,source=job
 req=urllib.request.Request(file['url'],headers={'User-Agent':'TheySix-Server-Upgrade/1.0'})
 with urllib.request.urlopen(req,timeout=90) as r:data=r.read()
 for kind,expected in file['hashes'].items():
  if hashlib.new(kind,data).hexdigest()!=expected:raise ValueError('Hash mismatch: '+name)
 actual,version=descriptor(data)
 if actual!=name:raise ValueError('Plugin identity mismatch: '+name+' vs '+actual)
 old_hash=hashlib.sha256(oldpath.read_bytes()).hexdigest() if oldpath else None
 target=DEST/file['filename']
 if oldpath and (DEST/oldpath.name).exists() and oldpath.name!=target.name:(DEST/oldpath.name).unlink()
 target.write_bytes(data)
 return {'plugin':name,'old_file':oldpath.name if oldpath else None,'new_file':target.name,'version':version,'release':release,'source':source,'url':file['url'],'sha256':hashlib.sha256(data).hexdigest(),'same_bytes':bool(old_hash and old_hash==hashlib.sha256(data).hexdigest())}
if __name__=='__main__':
 if DEST.exists():raise SystemExit('Candidate already exists; preserve it and use a fresh directory')
 DEST.mkdir(parents=True)
 jobs=[];records=[]
 for path in sorted(Path('plugins').glob('*.jar')):
  data=path.read_bytes();name,version=descriptor(data)
  if name in EXCLUDED:continue
  shutil.copy2(path,DEST/path.name)
  slug=MAPPING.get(name)
  if slug:
   x=json.loads((META/(slug+'.json')).read_text())
   candidates=[v for v in x['versions'] if v['version_type']=='release' and '26.3' in v['game_versions'] and any(l in v['loaders'] for l in ['paper','spigot','bukkit'])]
   if candidates:
    v=candidates[0]
    f=next(f for f in v['files'] if f['filename'].endswith('.jar') and not any(t in f['filename'] for t in ['sources','javadoc']))
    jobs.append((name,path,f,v['version_number'],'https://modrinth.com/plugin/'+slug+'/version/'+v['id']));continue
  if name=='LibsDisguises':
   v=json.loads((META/'libraryaddict_LibsDisguises.json').read_text())[0];a=v['assets'][0]
   jobs.append((name,path,{'filename':a['name'],'url':a['browser_download_url'],'hashes':{'sha256':a['digest'].split(':')[1]}},v['tag_name'],v['html_url']));continue
  records.append({'plugin':name,'old_file':path.name,'new_file':path.name,'version':version,'release':'retained','sha256':hashlib.sha256(data).hexdigest(),'note':'Pinned by owner' if name=='Geyser-Spigot' else 'No reviewed compatible update selected; runtime testing required'})
 v=json.loads((META/'EssentialsX_Essentials.json').read_text())[0];a=next(a for a in v['assets'] if re.match(r'EssentialsX-[0-9].*\.jar$',a['name']))
 jobs.append(('Essentials',None,{'filename':a['name'],'url':a['browser_download_url'],'hashes':{'sha256':a['digest'].split(':')[1]}},v['tag_name'],v['html_url']))
 with concurrent.futures.ThreadPoolExecutor(max_workers=4) as pool:
  for result in pool.map(download,jobs):records.append(result);print(json.dumps(result),flush=True)
 for folder in ['modules','libraries']:
  src=Path('plugins/BlueArcade3')/folder
  if src.exists():shutil.copytree(src,DEST/'BlueArcade3'/folder)
 (BASE/'candidate/manifest.json').write_text(json.dumps(records,indent=2)+'\n')
 print('Staged',len(records),'plugins; no active files changed')
