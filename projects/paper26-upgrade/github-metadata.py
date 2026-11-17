#!/usr/bin/env python3
import concurrent.futures,json,urllib.request
from pathlib import Path
repos=['Efnilite/WorldServer','libraryaddict/LibsDisguises','dmulloy2/ProtocolLib','EssentialsX/Essentials','BenCodez/VotifierPlus','BitworksMC/HeadDB','Ajneb97/EasyCommandBlocker','HelpChat/MyCommand','GamingKaetzchen/Ban-Webhook','Crazy-Crew/BlockParticles','jpenilla/MiniMOTD','LuckPerms/LuckPerms']
def get(repo):
 try:
  req=urllib.request.Request('https://api.github.com/repos/'+repo+'/releases?per_page=5',headers={'User-Agent':'TheySix-Server-Upgrade/1.0'})
  with urllib.request.urlopen(req,timeout=30) as r:x=json.load(r)
  Path('.backups/paper26-upgrade/metadata/'+repo.replace('/','_')+'.json').write_text(json.dumps(x,indent=2)+'\n')
  return {'repo':repo,'releases':[{'tag':v['tag_name'],'pre':v['prerelease'],'body':v.get('body','')[:3000],'files':[{'name':f['name'],'url':f['browser_download_url'],'digest':f.get('digest')} for f in v['assets']]} for v in x[:2]]}
 except Exception as e:return {'repo':repo,'error':str(e)}
with concurrent.futures.ThreadPoolExecutor(max_workers=4) as pool:
 for x in pool.map(get,repos): print(json.dumps(x),flush=True)
