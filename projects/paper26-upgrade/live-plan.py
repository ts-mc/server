#!/usr/bin/env python3
import json,hashlib,sys,time,re
from pathlib import Path
from prepare import descriptor
import importlib.util
spec=importlib.util.spec_from_file_location('apply_local',str(Path(__file__).with_name('apply-local.py')));module=importlib.util.module_from_spec(spec);spec.loader.exec_module(module)
auto_version=module.auto_version

def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
repo=Path(__file__).resolve().parents[2];base=repo/'.backups/paper26-live-deploy';before=base/'remote-before'
inspection=json.loads((base/'inspection.json').read_text())
expected={Path(n).name for n in inspection['plugin_jars']}
files=list((before/'plugins').glob('*.jar'))
if {p.name for p in files}!=expected:raise SystemExit('LIVE comparison JAR download is incomplete')
current={}
for p in files:
 name,version=descriptor(p.read_bytes())
 current.setdefault(name,[]).append((p,version))
 if len(current[name])!=1:raise SystemExit('Duplicate LIVE plugin identity: '+name)
candidate=repo/'.backups/paper26-upgrade/candidate/plugins'
sources={descriptor(p.read_bytes())[0]:p for p in candidate.glob('*.jar')}
sources['TSXMAS']=repo/'projects/tsxmas/target/TSXMAS.v1.0.0.jar'
unknown=set(current)-set(sources)
if unknown:raise SystemExit('Unexpected LIVE plugins require review: '+', '.join(sorted(unknown)))
changes=[];retained=[]
for name,source in sorted(sources.items()):
 old=current.get(name,[]);new_sha=sha(source)
 if old and sha(old[0][0])==new_sha and old[0][0].name==source.name:
  retained.append(name);continue
 if name in ['BlueArcade3','Geyser-Spigot','TSHide'] and old and sha(old[0][0])!=new_sha:raise SystemExit('Custom/pinned LIVE artifact differs from tested copy: '+name)
 if name=='PlayerLogs' and old:
  import zipfile
  def main(p):
   with zipfile.ZipFile(p) as z:return re.search(r'(?m)^main:\s*(.+)$',z.read('plugin.yml').decode()).group(1).strip()
  if main(old[0][0])!=main(source):raise SystemExit('LIVE PlayerLogs implementation differs from the selected project')
 changes.append({'plugin':name,'source':str(source),'target':'plugins/'+source.name,'sha256':new_sha,'old':[{'path':'plugins/'+p.name,'sha256':sha(p),'version':v} for p,v in old]})
paper=Path('/tmp/tsxmas-paper-26.3.jar')
if not(before/'server.jar').exists():raise SystemExit('LIVE server.jar comparison is missing')
if sha(paper)!=sha(before/'server.jar'):changes.append({'plugin':'Paper','source':str(paper),'target':'server.jar','sha256':sha(paper),'old':[{'path':'server.jar','sha256':sha(before/'server.jar')}]})
config=before/'plugins/TSStaff/config.yml'
if config.exists():
 data=config.read_bytes();patched=auto_version(data.decode('utf-8')).encode('utf-8')
 if data!=patched:
  path=base/'patched/TSStaff-config.yml';path.parent.mkdir(exist_ok=True);path.write_bytes(patched)
  changes.append({'plugin':'TSStaff version filter','source':str(path),'target':'plugins/TSStaff/config.yml','sha256':sha(path),'old':[{'path':'plugins/TSStaff/config.yml','sha256':sha(config)}]})
plan={'id':'paper26-'+time.strftime('%Y%m%d-%H%M%S'),'changes':changes,'retained':retained,'original_plugin_names':sorted(inspection['plugin_jars']),'worlds_or_databases_included':False}
(base/'plan.json').write_text(json.dumps(plan,indent=2)+'\n')
print(json.dumps({'changes':len(changes),'retained':len(retained),'plugins_updated':[c['plugin'] for c in changes],'worlds_or_databases_included':False},indent=2))
