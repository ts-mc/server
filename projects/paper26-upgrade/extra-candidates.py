#!/usr/bin/env python3
import sys,json,re
from pathlib import Path
from prepare import download,BASE,META,DEST,descriptor
manifest=json.loads((BASE/'candidate/manifest.json').read_text())
byname={x['plugin']:x for x in manifest}
def existing(name):
 for p in DEST.glob('*.jar'):
  if descriptor(p.read_bytes())[0]==name:return p
 return None
def github(repo,name,tag=None,filename=None):
 releases=json.loads((META/(repo.replace('/','_')+'.json')).read_text())
 release=next(v for v in releases if tag is None or v['tag_name']==tag)
 asset=next(a for a in release['assets'] if a['name']==filename)
 hashes={'sha256':asset['digest'].split(':')[1]} if asset.get('digest') else {}
 return (name,existing(name),{'filename':asset['name'],'url':asset['browser_download_url'],'hashes':hashes},release['tag_name']+(' (development)' if release['prerelease'] else ''),release['html_url'])
jobs=[github('dmulloy2/ProtocolLib','ProtocolLib','dev-build','ProtocolLib.jar'),github('BitworksMC/HeadDB','HeadDB',None,'HeadDB-6.1.1.jar')]
x=json.loads((META/'essentials-dev.json').read_text());a=next(a for a in x['artifacts'] if re.match(r'EssentialsX-[0-9].*\.jar$',a['fileName']))
jobs.append(('Essentials',existing('Essentials'),{'filename':a['fileName'],'url':x['url']+'artifact/'+a['relativePath'],'hashes':{}},'development build '+str(x['number']),'https://essentialsx.net/downloads.html'))
x=json.loads((META/'ultracosmetics.json').read_text());v=next(v for v in x['versions'] if '26.3' in v['game_versions']);f=next(f for f in v['files'] if f['primary'])
jobs.append(('UltraCosmetics',existing('UltraCosmetics'),f,v['version_number']+' (beta)','https://modrinth.com/plugin/ultracosmetics/version/'+v['id']))
for job in jobs:
 r=download(job);r['note']='Staging candidate; review release channel and migration before deployment';r['old_file']=byname.get(r['plugin'],{}).get('old_file',r['old_file']);byname[r['plugin']]=r;(BASE/'candidate/manifest.json').write_text(json.dumps(list(byname.values()),indent=2)+'\n');print(json.dumps(r),flush=True)
x=json.loads((META/'playerlogs.json').read_text());v=x['versions'][0];f=next(f for f in v['files'] if f['primary'])
r=download(('PlayerLogs',None,f,v['version_number'],'https://modrinth.com/plugin/playerlogs/version/'+v['id']));r['note']='No declared 26.3 compatibility; missing from local active JARs; test only';r['old_file']=byname.get(r['plugin'],{}).get('old_file',r['old_file']);byname[r['plugin']]=r;(BASE/'candidate/manifest.json').write_text(json.dumps(list(byname.values()),indent=2)+'\n');print(json.dumps(r),flush=True)
(BASE/'candidate/manifest.json').write_text(json.dumps(list(byname.values()),indent=2)+'\n')
