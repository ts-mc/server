#!/usr/bin/env python3
import argparse,importlib.util,json,hashlib,sqlite3
from pathlib import Path
REPO=Path(__file__).resolve().parents[3];BASE=REPO/'.backups/tsxmas-event-repair';BASE.mkdir(exist_ok=True)
def module(name,file):
 spec=importlib.util.spec_from_file_location(name,Path(__file__).with_name(file));m=importlib.util.module_from_spec(spec);spec.loader.exec_module(m);return m
t=module('event_repair_transport','christmas-update.py');patch=module('event_repair_patch','event-repair-config.py')
t.BASE=BASE;t.VERSION='1.1.1';t.CANDIDATE_PLAN='config-plan.json';t.DEPLOYMENT_PREFIX='tsxmas-event-repair-';t.LOCAL_PATCHER=Path(__file__).with_name('event-repair-config.py');t.LOCAL_TEMPLATES=[];t.REPORT_NAME='event-repair-1.1.1-deployment.json'
t.CONFIGS=['plugins/TSXMAS/'+f+'.yml' for f in ['config','locations','events','messages']]+['plugins/WorldGuard/worlds/tsxmas_christmas_2026/regions.yml']
def prepare():
 import yaml
 positions=yaml.safe_load((BASE/'before/plugins/TSXMAS/locations.yml').read_text());changes=[]
 for name in t.CONFIGS:
  source=BASE/'before'/name;old=source.read_bytes();new,paths=patch.edits(name,old.decode(),positions)
  if new.encode()==old:continue
  target=BASE/'candidate'/name;target.parent.mkdir(parents=True,exist_ok=True);target.write_bytes(new.encode())
  changes.append(dict(target=name,source=str(target),before_sha256=hashlib.sha256(old).hexdigest(),sha256=t.digest(target),changed_yaml_paths=paths))
 (BASE/'config-plan.json').write_text(json.dumps({'changes':changes},indent=2)+'\n');print('Prepared '+str(len(changes))+' targeted configs; existing points retained.')
def state():
 dest=BASE/'state.db';t.sftp.batch(['get '+t.Q('plugins/TSXMAS/tsxmas.db')+' -o '+t.Q(dest)])
 db=sqlite3.connect('file:'+str(dest)+'?mode=ro',uri=True)
 print('Stored event states:',db.execute("SELECT key,value FROM state WHERE scope='event' AND owner='server'").fetchall());db.close()
if __name__=='__main__':
 p=argparse.ArgumentParser();p.add_argument('operation',choices=['inspect','prepare','plan','stage','activate','state']);a=p.parse_args()
 {'inspect':t.inspect,'prepare':prepare,'plan':t.plan_update,'stage':t.stage,'activate':t.activate,'state':state}[a.operation]()
