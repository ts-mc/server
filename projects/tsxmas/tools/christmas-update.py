#!/usr/bin/env python3
import argparse,hashlib,importlib.util,json,re,time,zipfile,shutil
from pathlib import Path
REPO=Path(__file__).resolve().parents[3]
BASE=REPO/'.backups/tsxmas-christmas-update';BASE.mkdir(exist_ok=True)
spec=importlib.util.spec_from_file_location('tsxmas_sftp',REPO/'projects/paper26-upgrade/sftp-deploy.py');sftp=importlib.util.module_from_spec(spec);spec.loader.exec_module(sftp);Q=sftp.quote
CONFIGS=['plugins/TAB/config.yml','plugins/TAB/groups.yml','plugins/TAB/animations.yml','plugins/LPC/config.yml','plugins/BlueArcade3/language.yml','plugins/TSStaff/config.yml','plugins/TSXMAS/config.yml','plugins/TSXMAS/messages.yml','plugins/TSXMAS/events.yml','plugins/TSXMAS/locations.yml']
VERSION='1.0.1'
CANDIDATE_PLAN='theme-plan.json'
DEPLOYMENT_PREFIX='tsxmas-christmas-'
LOCAL_PATCHER=REPO/'projects/tsxmas/tools/christmas-theme.py'
LOCAL_TEMPLATES=[('plugins/TAB/config.yml.example','plugins/TAB/config.yml'),('plugins/TSStaff/config.yml.example','plugins/TSStaff/config.yml'),('plugins/TSStaff/src/main/resources/config.yml','plugins/TSStaff/config.yml')]
REPORT_NAME='christmas-1.0.1-deployment.json'
def digest(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def inspect():
 dest=BASE/'before';dest.mkdir(exist_ok=True);commands=[]
 for name in CONFIGS+['logs/latest.log']:
  p=dest/name;p.parent.mkdir(parents=True,exist_ok=True);commands.append('get '+Q(name)+' -o '+Q(p))
 sftp.batch(commands)
 (BASE/'plugin-list.txt').write_text(sftp.batch(['cls -1 plugins']))
 print('Read '+str(len(CONFIGS))+' targeted LIVE configuration files and latest log into a private comparison directory. LIVE unchanged.')
def plan_update():
 theme=json.loads((BASE/CANDIDATE_PLAN).read_text());changes=list(theme['changes'])
 source=REPO/('projects/tsxmas/target/TSXMAS.v'+VERSION+'.jar')
 with zipfile.ZipFile(source) as z:
  import yaml
  info=yaml.safe_load(z.read('plugin.yml'))
  if info['name']!='TSXMAS' or info['version']!=VERSION or info['main']!='com.theysix.tsxmas.TSXMASPlugin':raise RuntimeError('Unexpected plugin identity')
 plugins=sftp.batch(['cls -1 plugins']).splitlines();old=[n.strip() for n in plugins if re.fullmatch(r'plugins/TSXMAS[^/]*\.jar',n.strip())]
 if len(old)!=1 or old[0]=='plugins/TSXMAS.v'+VERSION+'.jar':raise RuntimeError('Review active TSXMAS JARs before updating')
 previous=BASE/'before'/old[0];previous.parent.mkdir(parents=True,exist_ok=True);sftp.batch(['get '+Q(old[0])+' -o '+Q(previous)])
 with zipfile.ZipFile(previous) as z:
  import yaml
  previous_info=yaml.safe_load(z.read('plugin.yml'))
  if previous_info['name']!='TSXMAS':raise RuntimeError('Old plugin identity differs')
  if tuple(map(int,previous_info['version'].split('.')))>=tuple(map(int,VERSION.split('.'))):raise RuntimeError('Refusing an equal-version update or downgrade')
 changes.append({'target':'plugins/TSXMAS.v'+VERSION+'.jar','old_target':old[0],'source':str(source),'before_sha256':digest(previous),'sha256':digest(source)})
 for c in changes:
  if digest(Path(c['source']))!=c['sha256']:raise RuntimeError('Candidate changed')
 import xml.etree.ElementTree as ET
 suites=[ET.parse(p).getroot() for p in (REPO/'projects/tsxmas/target/surefire-reports').glob('TEST-*.xml')]
 if not suites or any(int(s.get('errors','0')) or int(s.get('failures','0')) for s in suites):raise RuntimeError('Unit tests have not passed')
 plan={'id':DEPLOYMENT_PREFIX+time.strftime('%Y%m%d-%H%M%S'),'version':VERSION,'palette':theme.get('palette',[]),'tests_passed':sum(int(s.get('tests','0')) for s in suites),'changes':changes,'staged':False,'active':False,'java_server_started':False,'rank_data_changed':False,'world_data_changed':False}
 (BASE/'plan.json').write_text(json.dumps(plan,indent=2)+'\n');print('Prepared TSXMAS '+VERSION+' and '+str(len(changes)-1)+' targeted configs; '+str(plan['tests_passed'])+' unit tests passed.')
def stage():
 plan=json.loads((BASE/'plan.json').read_text());remote='.deploy-staging/'+plan['id'];commands=[]
 for c in plan['changes']:
  if digest(Path(c['source']))!=c['sha256']:raise RuntimeError('Candidate changed')
  commands+=['mkdir -p '+Q(remote+'/'+str(Path(c['target']).parent)),'put '+Q(c['source'])+' -o '+Q(remote+'/'+c['target'])]
 sftp.batch(commands)
 verify=BASE/'staged-readback';commands=[]
 for c in plan['changes']:
  target=verify/c['target'];target.parent.mkdir(parents=True,exist_ok=True);commands+=['get '+Q(remote+'/'+c['target'])+' -o '+Q(target)]
 sftp.batch(commands)
 if any(digest(verify/c['target'])!=c['sha256'] for c in plan['changes']):raise RuntimeError('Staged readback mismatch')
 plan['staged']=True;(BASE/'plan.json').write_text(json.dumps(plan,indent=2)+'\n');print('Staged and SHA-256 verified '+str(len(plan['changes']))+' inactive files.')
def activate():
 plan=json.loads((BASE/'plan.json').read_text())
 if not plan['staged'] or plan['active']:raise RuntimeError('Invalid staging state')
 check=BASE/'preactivate';check.mkdir(exist_ok=True);sftp.batch(['get '+Q('logs/latest.log')+' -o '+Q(check/'latest.log')])
 lines=(check/'latest.log').read_text(errors='replace').splitlines();markers=[l for l in lines if any(s in l for s in ['[bootstrap] Running Java','Starting minecraft server version','For help, type "help"','Stopping server','Stopping the server','All dimensions are saved'])]
 if not markers or not any(s in markers[-1] for s in ['Stopping server','Stopping the server','All dimensions are saved']):raise RuntimeError('LIVE appears running; stop cleanly before deployment')
 plugins=sftp.batch(['cls -1 plugins']).splitlines()
 if 'plugins/TSXMAS.v'+VERSION+'.jar' in plugins:raise RuntimeError('New plugin target already exists; refusing overwrite')
 commands=[]
 for i,c in enumerate(plan['changes']):commands+=['get '+Q(c.get('old_target',c['target']))+' -o '+Q(check/str(i))]
 sftp.batch(commands)
 for i,c in enumerate(plan['changes']):
  if digest(check/str(i))!=c['before_sha256']:raise RuntimeError('LIVE changed since comparison: '+c['target'])
 backup='.deploy-backups/'+plan['id'];commands=[]
 for c in plan['changes']:
  old=c.get('old_target',c['target']);commands+=['mkdir -p '+Q(backup+'/'+str(Path(old).parent)),'mv '+Q(old)+' '+Q(backup+'/'+old)]
 for c in plan['changes']:commands+=['mv '+Q('.deploy-staging/'+plan['id']+'/'+c['target'])+' '+Q(c['target'])]
 (BASE/'activation-started.json').write_text(json.dumps({'id':plan['id'],'backup':backup},indent=2)+'\n')
 sftp.batch(commands)
 readback=BASE/'active-readback';commands=[]
 for c in plan['changes']:
  target=readback/c['target'];target.parent.mkdir(parents=True,exist_ok=True);commands+=['get '+Q(c['target'])+' -o '+Q(target)]
 sftp.batch(commands)
 if any(digest(readback/c['target'])!=c['sha256'] for c in plan['changes']):raise RuntimeError('Active readback mismatch')
 names=sftp.batch(['cls -1 plugins']).splitlines()
 if [n for n in names if re.fullmatch(r'plugins/TSXMAS[^/]*\.jar',n)]!=['plugins/TSXMAS.v'+VERSION+'.jar']:raise RuntimeError('Unexpected active TSXMAS JAR set')
 spec=importlib.util.spec_from_file_location('tsxmas_config_patch',LOCAL_PATCHER);module=importlib.util.module_from_spec(spec);spec.loader.exec_module(module)
 local_backup=BASE/'local-before'
 for c in plan['changes']:
  name=c['target']
  if name.endswith('.jar'):
   for old in (REPO/'plugins').glob('TSXMAS*.jar'):
    target=local_backup/old.relative_to(REPO);target.parent.mkdir(parents=True,exist_ok=True);shutil.move(str(old),str(target))
   shutil.copy2(c['source'],REPO/name)
  else:
   local=REPO/name
   if local.exists():
    old=local.read_bytes();target=local_backup/name;target.parent.mkdir(parents=True,exist_ok=True);target.write_bytes(old)
    updated,_=module.edits(name,old.decode('utf-8'));local.write_bytes(updated.encode('utf-8'))
 for path,canonical in LOCAL_TEMPLATES:
  local=REPO/path
  if local.exists():
   original=local.read_bytes();target=local_backup/path;target.parent.mkdir(parents=True,exist_ok=True);target.write_bytes(original)
   updated,_=module.edits(canonical,original.decode('utf-8'));local.write_bytes(updated.encode('utf-8'))
 plan.update(active=True,backup=backup,active_sha256_verified=True)
 (BASE/'plan.json').write_text(json.dumps(plan,indent=2)+'\n')
 report={k:v for k,v in plan.items() if k!='changes'};report['changed_files']=[c['target'] for c in plan['changes']]
 (REPO/'projects/tsxmas/docs'/REPORT_NAME).write_text(json.dumps(report,indent=2)+'\n')
 print('Activated and SHA-256 verified '+str(len(plan['changes']))+' files. Old JAR/configs retained in '+backup+'. Server not started; world/player/permission data untouched.')
if __name__=='__main__':
 p=argparse.ArgumentParser();p.add_argument('operation',choices=['inspect','plan','stage','activate']);a=p.parse_args()
 try:{'inspect':inspect,'plan':plan_update,'stage':stage,'activate':activate}[a.operation]()
 except Exception as e:raise SystemExit(type(e).__name__+': '+str(e))
