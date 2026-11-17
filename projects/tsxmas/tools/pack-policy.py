#!/usr/bin/env python3

import argparse,hashlib,importlib.util,json,re,time,urllib.parse,urllib.request
from pathlib import Path
import yaml
REPO=Path(__file__).resolve().parents[3]
BASE=REPO/'.backups/tsxmas-textures';BASE.mkdir(exist_ok=True)
spec=importlib.util.spec_from_file_location('tsxmas_sftp',REPO/'projects/paper26-upgrade/sftp-deploy.py')
sftp=importlib.util.module_from_spec(spec);spec.loader.exec_module(sftp);Q=sftp.quote
def digest(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def set_property(raw,key,value):
 pattern=rb'(?m)^'+re.escape(key.encode())+rb'=([^\r\n]*)'
 replacement=key.encode()+b'='+value.encode('ascii')
 if re.search(pattern,raw):return re.sub(pattern,lambda _:replacement,raw)
 newline=b'\r\n' if b'\r\n' in raw else b'\n'
 return raw+(b'' if raw.endswith(b'\n') else newline)+replacement+newline
def prepare(url):
 parsed=urllib.parse.urlsplit(url)
 valid_path=(parsed.hostname=='download.mc-packs.net' and re.fullmatch(r'/pack/[0-9a-f]{40}\.zip',parsed.path)) or (parsed.hostname=='files.catbox.moe' and re.fullmatch(r'/[a-zA-Z0-9]{6}\.zip',parsed.path))
 if parsed.scheme!='https' or parsed.port not in [None,443] or parsed.query or parsed.fragment or parsed.username or parsed.password or not valid_path:raise RuntimeError('Use the direct HTTPS ZIP URL from an approved pack host')
 pack=json.loads((BASE/'venue-pack.json').read_text());expected=pack['sha1']
 if parsed.hostname=='download.mc-packs.net' and Path(parsed.path).stem!=expected:raise RuntimeError('MCPacks URL hash differs from the prepared artifact')
 download=BASE/'hosted-readback.zip'
 with urllib.request.urlopen(urllib.request.Request(url,headers={'User-Agent':'TheySix-TSXMAS/1.0'}),timeout=45) as response:
  if response.headers.get('Content-Length') and int(response.headers['Content-Length'])>30*1024*1024:raise RuntimeError('Unexpected hosted pack size')
  data=response.read(30*1024*1024+1)
 if len(data)>30*1024*1024 or hashlib.sha1(data).hexdigest()!=expected or hashlib.sha256(data).hexdigest()!=pack['sha256']:raise RuntimeError('Hosted pack did not match the prepared ZIP')
 download.write_bytes(data)
 snapshot=REPO/'.remote-sync';target=BASE/'required-pack-candidate';target.mkdir(exist_ok=True)
 raw=(snapshot/'server.properties').read_bytes()
 prompt=json.dumps({'text':'TheySix Events needs this pack for Christmas village models and textures.','color':'aqua'},separators=(',',':'))
 for key,value in {'resource-pack':url,'resource-pack-sha1':expected,'resource-pack-id':'8c4ad7b3-c182-4c6b-9f07-42c6ef4a0c66','resource-pack-prompt':prompt,'require-resource-pack':'true'}.items():raw=set_property(raw,key,value)
 (target/'server.properties').write_bytes(raw)
 config_path=snapshot/'plugins/TSXMAS/config.yml';text=config_path.read_text();before=yaml.safe_load(text)
 start=text.index('resource-pack:');end=text.index('\nintegrations:',start);section=text[start:end]
 replacements={r'(?m)^  enabled: false$':'  enabled: true',r'(?m)^  mode: OPT_IN$':'  mode: SEASONAL',r'(?m)^  required: false$':'  required: true',r'(?m)^  prompt: .*$':"  prompt: 'TheySix Events requires these Christmas seasonal textures.'"}
 for pattern,replacement in replacements.items():section=re.sub(pattern,replacement,section)
 text=text[:start]+section+text[end:];after=yaml.safe_load(text)
 if not after['resource-pack']['enabled'] or not after['resource-pack']['required'] or after['resource-pack']['mode']!='SEASONAL':raise RuntimeError('Required seasonal policy could not be set safely')
 for key in before:
  if key!='resource-pack' and before[key]!=after[key]:raise RuntimeError('Unrelated TSXMAS settings changed')
 for key in ['url','sha1','id','minecraft-version']:
  if before['resource-pack'][key]!=after['resource-pack'][key]:raise RuntimeError('Official seasonal pack identity changed unexpectedly')
 (target/'tsxmas-config.yml').write_text(text)
 deployment='tsxmas-pack-'+time.strftime('%Y%m%d-%H%M%S')
 changes=[{'remote':'server.properties','source':str(target/'server.properties'),'before_sha256':digest(snapshot/'server.properties'),'sha256':digest(target/'server.properties')},{'remote':'plugins/TSXMAS/config.yml','source':str(target/'tsxmas-config.yml'),'before_sha256':digest(config_path),'sha256':digest(target/'tsxmas-config.yml')}]
 plan={'id':deployment,'venue_url':url,'venue_sha1':expected,'hosted_sha256_verified':True,'changes':changes,'staged':False,'active':False,'events_enabled':after['enabled'],'java_started':False}
 (BASE/'pack-policy.json').write_text(json.dumps(plan,indent=2)+'\n');print('Verified hosted ZIP and prepared required Java venue + official seasonal pack configuration. Event activation remains unchanged.')
def stage():
 plan=json.loads((BASE/'pack-policy.json').read_text());remote='.deploy-staging/'+plan['id'];commands=[]
 for c in plan['changes']:
  if digest(Path(c['source']))!=c['sha256']:raise RuntimeError('Candidate changed')
  commands+=['mkdir -p '+Q(remote+'/'+str(Path(c['remote']).parent)),'put '+Q(c['source'])+' -o '+Q(remote+'/'+c['remote'])]
 sftp.batch(commands);plan['staged']=True;(BASE/'pack-policy.json').write_text(json.dumps(plan,indent=2)+'\n');print('Staged two inactive pack configuration files.')
def activate():
 plan=json.loads((BASE/'pack-policy.json').read_text())
 if not plan['staged'] or plan['active'] or not plan['hosted_sha256_verified']:raise RuntimeError('Invalid deployment state')
 check=BASE/'pack-preactivate';check.mkdir(exist_ok=True)
 sftp.batch(['get '+Q('logs/latest.log')+' -o '+Q(check/'latest.log')])
 log=(check/'latest.log').read_text(errors='replace');markers=[line for line in log.splitlines() if any(x in line for x in ['[bootstrap] Running Java','Starting minecraft server version','For help, type "help"','Stopping server','Stopping the server','All dimensions are saved'])]
 if not markers or not any(x in markers[-1] for x in ['Stopping server','Stopping the server','All dimensions are saved']):raise RuntimeError('LIVE appears running; stop it cleanly before activation')
 commands=[]
 for index,c in enumerate(plan['changes']):commands+=['get '+Q(c['remote'])+' -o '+Q(check/str(index)),'get '+Q('.deploy-staging/'+plan['id']+'/'+c['remote'])+' -o '+Q(check/('staged-'+str(index)))]
 sftp.batch(commands)
 for index,c in enumerate(plan['changes']):
  if digest(check/str(index))!=c['before_sha256']:raise RuntimeError('LIVE config changed; preserve and review before applying')
  if digest(check/('staged-'+str(index)))!=c['sha256']:raise RuntimeError('Staged configuration readback mismatch')
 local=REPO/'plugins/TSXMAS/config.yml'
 if digest(local)!=plan['changes'][1]['before_sha256']:raise RuntimeError('LOCAL TSXMAS config differs; review before applying')
 backup='.deploy-backups/'+plan['id'];commands=[]
 for c in plan['changes']:
  commands+=['mkdir -p '+Q(backup+'/'+str(Path(c['remote']).parent)),'mv '+Q(c['remote'])+' '+Q(backup+'/'+c['remote']),'mv '+Q('.deploy-staging/'+plan['id']+'/'+c['remote'])+' '+Q(c['remote'])]
 sftp.batch(commands)
 commands=['get '+Q(c['remote'])+' -o '+Q(check/('active-'+str(i))) for i,c in enumerate(plan['changes'])];sftp.batch(commands)
 if any(digest(check/('active-'+str(i)))!=c['sha256'] for i,c in enumerate(plan['changes'])):raise RuntimeError('Active configuration readback mismatch')
 local.write_bytes(Path(plan['changes'][1]['source']).read_bytes())
 local_properties=REPO/'server.properties'
 if local_properties.exists():
  current=local_properties.read_bytes();(check/'local-server-properties-before').write_bytes(current)
  generated=Path(plan['changes'][0]['source']).read_bytes()
  for key in ['resource-pack','resource-pack-sha1','resource-pack-id','resource-pack-prompt','require-resource-pack']:
   value=re.search(rb'(?m)^'+re.escape(key.encode())+rb'=([^\r\n]*)',generated).group(1).decode('ascii')
   current=set_property(current,key,value)
  local_properties.write_bytes(current)
 plan.update(active=True,backup=backup,active_sha256_verified=True)
 (BASE/'pack-policy.json').write_text(json.dumps(plan,indent=2)+'\n')
 public={k:v for k,v in plan.items() if k!='changes'};public['changed_files']=[c['remote'] for c in plan['changes']]
 (REPO/'projects/tsxmas/docs/pack-deployment.json').write_text(json.dumps(public,indent=2)+'\n')
 print('Activated and SHA-256 verified two required-pack configs; backups retained. Java was not started. Restart and client acceptance remain pending.')
if __name__=='__main__':
 p=argparse.ArgumentParser();p.add_argument('operation',choices=['prepare','stage','activate']);p.add_argument('--url');a=p.parse_args()
 try:
  if a.operation=='prepare':prepare(a.url or '')
  else:{'stage':stage,'activate':activate}[a.operation]()
 except Exception as e:raise SystemExit(type(e).__name__+': '+str(e))
