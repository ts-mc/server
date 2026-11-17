#!/usr/bin/env python3
import argparse,hashlib,importlib.util,json
from pathlib import Path
REPO=Path(__file__).resolve().parents[3];BASE=REPO/'.backups/tsxmas-leaderboards';BASE.mkdir(exist_ok=True)
def module(name,file):
 spec=importlib.util.spec_from_file_location(name,Path(__file__).with_name(file));m=importlib.util.module_from_spec(spec);spec.loader.exec_module(m);return m
transport=module('tsxmas_leaderboard_transport','christmas-update.py');patch=module('tsxmas_leaderboard_patch','leaderboard-config.py')
transport.BASE=BASE;transport.VERSION='1.1.0';transport.DEPLOYMENT_PREFIX='tsxmas-leaderboards-';transport.CANDIDATE_PLAN='config-plan.json'
transport.LOCAL_PATCHER=Path(__file__).with_name('leaderboard-config.py');transport.LOCAL_TEMPLATES=[];transport.REPORT_NAME='leaderboards-1.1.0-deployment.json'
transport.CONFIGS=['plugins/TSXMAS/'+name+'.yml' for name in ['config','messages','locations','events','rewards','database']]
def prepare():
 changes=[]
 for file in ['config','messages','locations']:
  name='plugins/TSXMAS/'+file+'.yml';source=BASE/'before'/name;old=source.read_bytes();new,paths=patch.edits(name,old.decode('utf-8'))
  if not paths:continue
  target=BASE/'candidate'/name;target.parent.mkdir(parents=True,exist_ok=True);target.write_bytes(new.encode('utf-8'))
  changes.append({'target':name,'source':str(target),'before_sha256':hashlib.sha256(old).hexdigest(),'sha256':transport.digest(target),'changed_yaml_paths':paths})
 (BASE/'config-plan.json').write_text(json.dumps({'changes':changes,'palette':['B3000C','E40010','D8D8D8','1FD537','00B32C']},indent=2)+'\n')
 print('Prepared additive leaderboard defaults in '+str(len(changes))+' configs; existing prefixes, points, event states and pack policy preserved.')
if __name__=='__main__':
 p=argparse.ArgumentParser();p.add_argument('operation',choices=['inspect','prepare','plan','stage','activate']);a=p.parse_args()
 try:
  if a.operation=='prepare':prepare()
  else:{'inspect':transport.inspect,'plan':transport.plan_update,'stage':transport.stage,'activate':transport.activate}[a.operation]()
 except Exception as e:raise SystemExit(type(e).__name__+': '+str(e))
