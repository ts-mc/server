#!/usr/bin/env python3
import concurrent.futures, json, urllib.request, urllib.parse
from pathlib import Path
ROOT=Path('.backups/paper26-upgrade/metadata')
PROJECTS=['fastasyncworldedit','worldguard','packetevents','libsdisguises','fancynpcs','fancyholograms','nightcore','worldserver','multiverse-core','viaversion','viabackwards','tab-was-taken','skinsrestorer','luckperms','placeholderapi','deluxemenus','discordsrv','discordsrv-staff-chat','blockparticles','itemjoin','headdb','maintenance','minimotd','ultracosmetics','noteblockapi','votifierplus','advancedbanz','dankvotes','voidgen','essentialsx','protocollib','easycommandblocker']
def fetch(slug):
 try:
  def get(s):
   req=urllib.request.Request('https://api.modrinth.com/v2/'+s,headers={'User-Agent':'TheySix-Server-Upgrade/1.0'})
   with urllib.request.urlopen(req,timeout=30) as r:return json.load(r)
  project=get('project/'+slug)
  versions=get('project/'+slug+'/version')
  (ROOT/(slug+'.json')).write_text(json.dumps({'project':project,'versions':versions},indent=2)+'\n')
  compatible=[v for v in versions if '26.3' in v['game_versions'] and any(x in v['loaders'] for x in ['paper','spigot','bukkit','purpur'])]
  def brief(v):return {'version':v['version_number'],'type':v['version_type'],'minecraft':v['game_versions'][-5:],'loaders':v['loaders'],'files':[f['filename'] for f in v['files']]}
  return {'slug':slug,'title':project['title'],'source':project.get('source_url'),'latest':brief(versions[0]) if versions else None,'compatible':list(map(brief,compatible[:2]))}
 except Exception as e:return {'slug':slug,'error':str(e)}
if __name__=='__main__':
 ROOT.mkdir(parents=True,exist_ok=True)
 with concurrent.futures.ThreadPoolExecutor(max_workers=4) as pool:
  for result in pool.map(fetch,PROJECTS):print(json.dumps(result),flush=True)
