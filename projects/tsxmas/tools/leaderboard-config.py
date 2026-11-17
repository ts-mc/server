#!/usr/bin/env python3
import json,copy
from pathlib import Path
import yaml
from yaml.nodes import MappingNode,SequenceNode
REPO=Path(__file__).resolve().parents[3]
RESOURCES=REPO/'projects/tsxmas/src/main/resources'
EVENTS=['calendar','hunt','delivery','build','secret','raffle','market','grinch']
def find(text,path):
 node=yaml.compose(text)
 for key in path:
  if not isinstance(node,MappingNode):return None
  node=next((v for k,v in node.value if k.value==key),None)
  if node is None:return None
 return node
def add_mapping(text,path,missing):
 if not missing:return text
 node=find(text,path)
 if not isinstance(node,MappingNode):raise ValueError('Expected mapping: '+'.'.join(path))
 if node.flow_style:
  current=yaml.safe_load(text)
  for key in path:current=current[key]
  merged=dict(current);merged.update(missing)
  return text[:node.start_mark.index]+json.dumps(merged,ensure_ascii=False)+text[node.end_mark.index:]
 newline='\r\n' if '\r\n' in text else '\n';indent=' '*node.start_mark.column
 snippet=yaml.safe_dump(missing,sort_keys=False,allow_unicode=True,width=1000).rstrip('\n')
 snippet=newline.join(indent+line if line else '' for line in snippet.splitlines())+newline
 at=node.end_mark.index;prefix=newline if at and text[at-1]!='\n' else ''
 return text[:at]+prefix+snippet+text[at:]
def edits(name,text):
 file=Path(name).name;defaults=yaml.safe_load((RESOURCES/file).read_text());current=yaml.safe_load(text);changes=[]
 if file=='config.yml':
  if 'leaderboards' not in current:text=add_mapping(text,(),{'leaderboards':defaults['leaderboards']});changes.append('leaderboards')
  else:
   missing={k:v for k,v in defaults['leaderboards'].items() if k not in current['leaderboards']};text=add_mapping(text,('leaderboards',),missing);changes.extend('leaderboards.'+k for k in missing)
 elif file=='messages.yml':
  wanted={k:v for k,v in defaults.items() if k.startswith('leaderboard') or k in ['menu-leaderboards','menu-grinch','grinch-info']}
  missing={k:v for k,v in wanted.items() if k not in current};text=add_mapping(text,(),missing);changes.extend(missing)
  extra=[v for v in defaults['player-help'] if ('/xmas top' in v or '/xmas grinch' in v) and v not in current.get('player-help',[])]
  if extra:
   node=find(text,('player-help',))
   if node is None:text=add_mapping(text,(),{'player-help':extra})
   elif not isinstance(node,SequenceNode):raise ValueError('player-help must be a list')
   elif node.flow_style:text=text[:node.start_mark.index]+json.dumps(current['player-help']+extra,ensure_ascii=False)+text[node.end_mark.index:]
   else:
    newline='\r\n' if '\r\n' in text else '\n';snippet=''.join(' '*node.start_mark.column+'- '+json.dumps(v,ensure_ascii=False)+newline for v in extra)
    at=node.end_mark.index;prefix=newline if at and text[at-1]!='\n' else ''
    text=text[:at]+prefix+snippet+text[at:]
   changes.append('player-help')
 elif file=='locations.yml':
  for event in EVENTS:
   key='leaderboard-'+event
   if key not in current.get('holograms',{}):
    if 'holograms' not in current:text=add_mapping(text,(),{'holograms':{key:defaults['holograms'][key]}});current['holograms']={}
    else:text=add_mapping(text,('holograms',),{key:defaults['holograms'][key]})
    current.setdefault('holograms',{})[key]=copy.deepcopy(defaults['holograms'][key]);changes.append('holograms.'+key)
   elif 'leaderboard' not in current['holograms'][key]:
    text=add_mapping(text,('holograms',key),{'leaderboard':event});changes.append('holograms.'+key+'.leaderboard')
 yaml.safe_load(text);return text,changes
