#!/usr/bin/env python3
import argparse,json,os,re,subprocess,sys,time,hashlib
from pathlib import Path
REPO=Path(__file__).resolve().parents[2]
BASE=REPO/'.backups/paper26-live-deploy';BASE.mkdir(parents=True,exist_ok=True)
def quote(s):return '"'+str(s).replace('\\','\\\\').replace('"','\\"')+'"'
def connection():
 c=json.loads((REPO/'.vscode/sftp.json').read_text())
 secret=Path(os.environ.get('TS_SERVER_SFTP_CREDENTIAL',str(Path.home()/'.config/ts-server/sftp-password'))).expanduser()
 if not secret.is_file() or secret.stat().st_uid!=os.getuid() or secret.stat().st_mode & 0o777 != 0o600:raise RuntimeError('Protected SFTP credential is unavailable')
 lines=secret.read_text().splitlines()
 if len(lines)!=1 or not lines[0]:raise RuntimeError('Protected SFTP credential must contain one non-empty line')
 password=lines[0]
 return c,password

def batch(commands):
 c,password=connection()
 pre=['set xfer:clobber yes','set cmd:fail-exit yes','set net:max-retries 1','set net:timeout 25','set sftp:auto-confirm no',f'open -u {quote(c["username"]+","+password)} -p {int(c["port"])} {quote("sftp://"+c["host"])}','cd '+quote(c.get('remotePath','/'))]
 result=subprocess.run(['lftp','-f','/dev/stdin'],input='\n'.join(pre+commands+['bye'])+'\n',capture_output=True,text=True)
 if result.returncode:
  message=result.stderr+result.stdout
  kind='connection/transfer failure'
  if 'Host key verification failed' in message:kind='SSH host-key verification failed'
  elif 'Login failed' in message or 'Permission denied' in message:kind='SFTP authentication/permission failure'
  elif 'No such file' in message:kind='Remote file not found'
  elif 'resolve' in message:kind='SFTP hostname resolution failed'
  elif 'exists' in message:kind='Local comparison file already exists'
  for secret in [password,str(c.get('password','')),c['host'],c['username']]:
   if secret:message=message.replace(secret,'[REDACTED]')
  message=re.sub(r'\b(?:\d{1,3}\.){3}\d{1,3}\b','[REDACTED-IP]',message)
  raise RuntimeError(kind+': '+message[-1000:].strip())
 return result.stdout

def inspect():
 root=batch(['cls -1'])
 (BASE/'remote-root-list.txt').write_text(root)
 plugins=batch(['cls -1 plugins'])
 (BASE/'remote-plugin-list.txt').write_text(plugins)
 batch(['get '+quote('logs/latest.log')+' -o '+quote(BASE/'latest-before.log')])
 lines=(BASE/'latest-before.log').read_text(errors='replace').splitlines()
 markers=[]
 for i,line in enumerate(lines):
  if any(s in line for s in ['Starting minecraft server version','Starting org.bukkit.craftbukkit.Main','[bootstrap] Running Java','Stopping the server','Stopping server','All dimensions are saved','For help, type "help"']):markers.append((i,line))
 stopped=bool(markers and any(s in markers[-1][1] for s in ['All dimensions are saved','Stopping server','Stopping the server']))
 jars=[line.strip() for line in plugins.splitlines() if line.strip().endswith('.jar')]
 summary={'root_jar_names':[line for line in root.splitlines() if line.endswith('.jar')],'plugin_jars':jars,'log_indicates_stopped':stopped,'last_lifecycle_marker':re.sub(r'\[[^]]*\]', '',markers[-1][1]).strip() if markers else None}
 (BASE/'inspection.json').write_text(json.dumps(summary,indent=2)+'\n')
 print(json.dumps(summary,indent=2))

def download_before():
 inspect()
 x=json.loads((BASE/'inspection.json').read_text())
 dest=BASE/'remote-before';(dest/'plugins').mkdir(parents=True,exist_ok=True)
 commands=[]
 for name in x['plugin_jars']:
  if not re.fullmatch(r'plugins/[^/]+\.jar',name):raise RuntimeError('Unexpected plugin path')
  commands.append('get '+quote(name)+' -o '+quote(dest/name))
 for name in ['server.jar','server.properties','plugins/TSStaff/config.yml']:
  target=dest/name;target.parent.mkdir(parents=True,exist_ok=True)
  commands.append('get '+quote(name)+' -o '+quote(target))
 batch(commands)
 print('Downloaded existing JARs and targeted settings into the ignored comparison/rollback directory.')

def load_plan():
 plan=json.loads((BASE/'plan.json').read_text())
 if not re.fullmatch(r'paper26-[0-9-]+',plan['id']):raise RuntimeError('Invalid deployment ID')
 seen=set()
 for change in plan['changes']:
  name=change['target']
  if name in seen or not (name=='server.jar' or re.fullmatch(r'plugins/[^/]+\.jar',name) or name=='plugins/TSStaff/config.yml'):raise RuntimeError('Disallowed/duplicate deployment target')
  seen.add(name)
  if hashlib.sha256(Path(change['source']).read_bytes()).hexdigest()!=change['sha256']:raise RuntimeError('Source artifact changed after planning')
 return plan

def stage():
 plan=load_plan();remote='.deploy-staging/'+plan['id']
 commands=['mkdir -p '+quote(remote+'/plugins/TSStaff')]
 for change in plan['changes']:
  commands.append('put '+quote(change['source'])+' -o '+quote(remote+'/'+change['target']))
 batch(commands)
 verify=BASE/'staged-readback'/plan['id'];commands=[]
 for change in plan['changes']:
  target=verify/change['target'];target.parent.mkdir(parents=True,exist_ok=True)
  commands.append('get '+quote(remote+'/'+change['target'])+' -o '+quote(target))
 batch(commands)
 for change in plan['changes']:
  if hashlib.sha256((verify/change['target']).read_bytes()).hexdigest()!=change['sha256']:raise RuntimeError('Staged artifact readback hash mismatch')
 (BASE/'staged-verified.json').write_text(json.dumps({'id':plan['id'],'verified_files':len(plan['changes'])},indent=2)+'\n')
 print('Uploaded and SHA-256 verified '+str(len(plan['changes']))+' inactive staged files. Active files unchanged.')

def activate():
 plan=load_plan();verified=json.loads((BASE/'staged-verified.json').read_text())
 if verified['id']!=plan['id']:raise RuntimeError('The plan has not been verified')
 test=json.loads(Path('/tmp/theysix-upgrade-26.3-live-set/result.json').read_text())
 if not(test['started'] and not test['errors'] and test['exit_code']==0 and test['status_ping']['ok']):raise RuntimeError('Final LIVE-set compatibility test failed')
 inspect()
 inspection=json.loads((BASE/'inspection.json').read_text())
 if not inspection['log_indicates_stopped']:raise RuntimeError('LIVE log does not indicate a stopped server. Activation refused.')
 if sorted(inspection['plugin_jars'])!=plan['original_plugin_names']:raise RuntimeError('LIVE plugin filenames changed since planning')
 for c in plan['changes']:
  if c['target']=='plugins/TSStaff/config.yml':
   check=BASE/'TSStaff-config-preactivate.yml';batch(['get '+quote(c['target'])+' -o '+quote(check)])
   if hashlib.sha256(check.read_bytes()).hexdigest()!=c['old'][0]['sha256']:raise RuntimeError('LIVE TSStaff config changed since planning')
 stage_root='.deploy-staging/'+plan['id'];backup='.deploy-backups/'+plan['id']
 commands=['mkdir -p '+quote(backup+'/plugins/TSStaff')]
 for c in plan['changes']:
  for old in c['old']:
   name=old['path']
   if not(name=='server.jar' or re.fullmatch(r'plugins/[^/]+\.jar',name) or name=='plugins/TSStaff/config.yml'):raise RuntimeError('Disallowed backup path')
   commands.append('mv '+quote(name)+' '+quote(backup+'/'+name))
 for c in plan['changes']:
  commands.append('mv '+quote(stage_root+'/'+c['target'])+' '+quote(c['target']))
 (BASE/'activation-started.json').write_text(json.dumps({'id':plan['id'],'backup':backup,'files':len(plan['changes'])},indent=2)+'\n')
 batch(commands)
 active=batch(['cls -1 plugins'])
 names=sorted(line.strip() for line in active.splitlines() if line.strip().endswith('.jar'))
 expected=set(plan['original_plugin_names'])
 for c in plan['changes']:
  for old in c['old']:expected.discard(old['path'])
  if c['target'].startswith('plugins/') and c['target'].endswith('.jar'):expected.add(c['target'])
 if set(names)!=expected:raise RuntimeError('Post-activation plugin filename set differs from the plan')
 (BASE/'deployed.json').write_text(json.dumps({'id':plan['id'],'backup':backup,'updated_files':len(plan['changes']),'active_plugin_count':len(names),'verified_by':'SFTP staged SHA-256 readback followed by server-side rename','server_started':False},indent=2)+'\n')
 print('Activated targeted files. Remote rollback directory: '+backup)
 print('Active plugin JARs: '+str(len(names))+'. Server remains stopped; no world conversion was started.')

if __name__=='__main__':
 p=argparse.ArgumentParser();p.add_argument('operation',choices=['inspect','download-before','stage','activate']);a=p.parse_args()
 try:
  {'inspect':inspect,'download-before':download_before,'stage':stage,'activate':activate}[a.operation]()
 except Exception as e:print(type(e).__name__+': '+str(e),file=sys.stderr);sys.exit(1)
