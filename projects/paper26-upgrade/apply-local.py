#!/usr/bin/env python3
import argparse,hashlib,json,os,shutil,time
from pathlib import Path
from prepare import descriptor,EXCLUDED
REPO=Path(__file__).resolve().parents[2]
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def auto_version(data):
 lines=data.splitlines(keepends=True);inside=False
 for i,line in enumerate(lines):
  if line and not line[0].isspace() and not line.lstrip().startswith('#'):inside=line.startswith('updates:')
  if inside and line.lstrip().startswith('minecraft-version:'):
   prefix=line[:len(line)-len(line.lstrip())];ending='\r\n' if line.endswith('\r\n') else '\n' if line.endswith('\n') else ''
   lines[i]=prefix+"minecraft-version: 'auto'"+ending
 return ''.join(lines)
if __name__=='__main__':
 p=argparse.ArgumentParser();p.add_argument('--apply',action='store_true');a=p.parse_args()
 desired=REPO/'.backups/paper26-upgrade/candidate/plugins'
 sources={descriptor(f.read_bytes())[0]:f for f in desired.glob('*.jar')}
 xmas=REPO/'projects/tsxmas/target/TSXMAS.v1.0.0.jar';sources['TSXMAS']=xmas
 current={}
 for f in (REPO/'plugins').glob('*.jar'):
  name,_=descriptor(f.read_bytes());current.setdefault(name,[]).append(f)
 changes=[]
 for name,source in sources.items():
  target=REPO/'plugins'/source.name
  old=current.get(name,[])
  if len(old)==1 and old[0]==target and sha(old[0])==sha(source):continue
  changes.append((name,source,target,old))
 retired=[f for name,paths in current.items() if name in EXCLUDED for f in paths]
 print(json.dumps({'jar_updates_or_additions':len(changes),'retired_jars_to_archive':[f.name for f in retired],'paper_target':'26.3-251','worlds_untouched':True}))
 if not a.apply:raise SystemExit(0)
 result=json.loads(Path('/tmp/theysix-upgrade-26.3-live-set/result.json').read_text())
 if not(result['started'] and result['exit_code']==0 and not result['errors'] and result['status_ping']['ok']):raise SystemExit('Final LIVE-set compatibility test is not successful')
 for proc in Path('/proc').iterdir():
  if not proc.name.isdigit():continue
  try:
   if (proc/'cwd').resolve()==REPO and (proc/'exe').resolve().name=='java':raise SystemExit('A local server process is running; stop it first')
  except OSError:pass
 backup=REPO/'.backups'/('paper26-local-'+time.strftime('%Y%m%d-%H%M%S'));backup.mkdir()
 for name,source,target,old in changes:
  for f in old:
   dest=backup/f.relative_to(REPO);dest.parent.mkdir(parents=True,exist_ok=True);f.rename(dest)
  temp=backup/'new-files'/target.relative_to(REPO);temp.parent.mkdir(parents=True,exist_ok=True);shutil.copy2(source,temp);os.replace(temp,target)
 for f in retired:
  dest=backup/f.relative_to(REPO);dest.parent.mkdir(parents=True,exist_ok=True);f.rename(dest)
 paper=Path('/tmp/tsxmas-paper-26.3.jar');target=REPO/'server.jar'
 if sha(target)!=sha(paper):
  target.rename(backup/'server.jar');temp=backup/'paper-new';shutil.copy2(paper,temp);os.replace(temp,target)
 for filename in ['config','messages','events','rewards','locations','database']:
  source=REPO/'projects/tsxmas/src/main/resources'/(filename+'.yml');target=REPO/'plugins/TSXMAS'/(filename+'.yml')
  if not target.exists():target.parent.mkdir(exist_ok=True);shutil.copy2(source,target)
 config=REPO/'plugins/TSStaff/config.yml'
 if config.exists():
  old=config.read_bytes();text=old.decode('utf-8');new=auto_version(text).encode('utf-8')
  if new!=old:
   dest=backup/'plugins/TSStaff/config.yml';dest.parent.mkdir(parents=True,exist_ok=True);dest.write_bytes(old);config.write_bytes(new)
 for _,source,target,_ in changes:
  if sha(source)!=sha(target):raise SystemExit('Local readback mismatch')
 (backup/'applied.json').write_text(json.dumps({'plugins':[name for name,_,_,_ in changes],'retired':[f.name for f in retired]},indent=2)+'\n')
 print('Applied locally. Reversible JAR/config backup:',backup.relative_to(REPO))
