#!/usr/bin/env python3
import hashlib,json,re
from pathlib import Path
import yaml
from yaml.nodes import MappingNode,SequenceNode,ScalarNode
REPO=Path(__file__).resolve().parents[3];BASE=REPO/'.backups/tsxmas-christmas-update'
PALETTE=['B3000C','E40010','D8D8D8','1FD537','00B32C']
GRADIENT='<gradient:'+':'.join('#'+v for v in PALETTE)+'>'
def plain(text):
 text=re.sub(r'(?i)[&§]x(?:[&§][0-9a-f]){6}','',text)
 text=re.sub(r'(?i)(?:&|§)#[0-9a-f]{6}|[&§][0-9a-fk-or]','',text)
 return re.sub(r'<[^>]*>','',text)
def gradient(text,bold=False,x_style=False):
 chars=list(text);out=[]
 for i,ch in enumerate(chars):
  value=i/max(1,len(chars)-1)*(len(PALETTE)-1);left=min(int(value),len(PALETTE)-2);fraction=value-left
  a=[int(PALETTE[left][j:j+2],16) for j in (0,2,4)];b=[int(PALETTE[left+1][j:j+2],16) for j in (0,2,4)]
  color=''.join(f'{round(x+(y-x)*fraction):02X}' for x,y in zip(a,b))
  out.append(('&x'+''.join('&'+c for c in color) if x_style else '&#'+color)+('&l' if bold else '')+ch)
 return ''.join(out)+'&r'
def themed(text):
 return gradient(plain(text),bold='&l' in text or '<bold>' in text or '<b>' in text)
def nodes(text):
 result={}
 def walk(node,path=()):
  if isinstance(node,MappingNode):
   for key,value in node.value:walk(value,path+(key.value,))
  elif isinstance(node,SequenceNode):
   for i,value in enumerate(node.value):walk(value,path+(i,))
  elif isinstance(node,ScalarNode):result[path]=node
 walk(yaml.compose(text));return result
def edits(name,text):
 values=nodes(text);changes={}
 for path,node in values.items():
  value=node.value
  if name=='plugins/TAB/config.yml':
   title=path[-1]=='title' and path[:2]==('scoreboard','scoreboards')
   label=plain(value).lower()
   branded=any(word in label for word in ['theysix.tech','theysix events','ᴛʜᴇʏsɪx'])
   if (title or branded) and '%' not in value and node.tag.endswith(':str'):changes[path]=themed(value)
   elif '<gradient:#4cadd0:#b2f9ff>' in value.lower():changes[path]=re.sub(r'(?i)<gradient:#4cadd0:#b2f9ff>',GRADIENT,value)
  elif name=='plugins/TAB/groups.yml' and path[-1] in ['tabsuffix','tagsuffix']:
   changes[path]=themed(value)
  elif name=='plugins/TAB/animations.yml':
   if path[0] in ['ServerName','web','vote'] and 'texts' in path:changes[path]=themed(value)
   elif path[0]=='time' and 'texts' in path:changes[path]=value.replace('&b','&#00B32C')
   elif path[0]=='barcolors' and 'texts' in path:changes[path]=['RED','WHITE','GREEN','WHITE'][path[-1]%4]
  elif name=='plugins/BlueArcade3/language.yml' and path==('prefix',):
   changes[path]='<bold>'+GRADIENT+plain(value).rstrip().removesuffix('|').rstrip()+'</gradient></bold> <gray>|</gray>'
  elif name=='plugins/TSStaff/config.yml' and path==('prefix',):changes[path]=gradient(plain(value).rstrip(),bold=True,x_style=True)+' '
  elif name=='plugins/TSXMAS/messages.yml':
   if path==('prefix',):changes[path]='<bold>'+GRADIENT+plain(value).rstrip()+'</gradient></bold> '
   elif path==('validation-ok',):changes[path]='Venue checks passed. Use /tsxmas validate <event> to check a specific activity.'
  elif name=='plugins/LPC/config.yml' and path==('chat-format',) and 'SERVER' not in plain(value):
   changes[path]=gradient('SERVER',bold=True)+' &8| &r'+value
 changes={path:value for path,value in changes.items() if value!=values[path].value}
 for path,value in sorted(changes.items(),key=lambda item:values[item[0]].start_mark.index,reverse=True):
  node=values[path];text=text[:node.start_mark.index]+json.dumps(value,ensure_ascii=False)+text[node.end_mark.index:]
 yaml.safe_load(text)
 return text,['.'.join(map(str,path)) for path in changes]
if __name__=='__main__':
 before=BASE/'before';candidate=BASE/'candidate';candidate.mkdir(exist_ok=True);report=[]
 names=['plugins/TAB/config.yml','plugins/TAB/groups.yml','plugins/TAB/animations.yml','plugins/BlueArcade3/language.yml','plugins/TSStaff/config.yml','plugins/TSXMAS/messages.yml','plugins/LPC/config.yml']
 for name in names:
  source=before/name;text=source.read_bytes().decode('utf-8');new,paths=edits(name,text)
  if not paths:continue
  out=candidate/name;out.parent.mkdir(parents=True,exist_ok=True);out.write_bytes(new.encode('utf-8'))
  report.append({'target':name,'source':str(out),'before_sha256':hashlib.sha256(source.read_bytes()).hexdigest(),'sha256':hashlib.sha256(out.read_bytes()).hexdigest(),'changed_yaml_paths':paths})
 (BASE/'theme-plan.json').write_text(json.dumps({'palette':PALETTE,'changes':report,'rank_data_changed':False},indent=2)+'\n')
 print('Prepared '+str(len(report))+' targeted seasonal YAML files; rank data and all event locations preserved.')
