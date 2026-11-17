#!/usr/bin/env python3
import collections,hashlib,json,struct,sys,zlib,gzip
from pathlib import Path
from nbt import load,plain
root=Path(sys.argv[1]);report={'files':{},'chunks':0,'data_versions':collections.Counter(),'block_entities':collections.Counter(),'entities':collections.Counter(),'custom_blocks':set(),'custom_paintings':set(),'command_blocks':[],'signs':[],'chunk_bounds':None,'errors':[]}
for p in root.rglob('*'):
 if p.is_symlink():raise SystemExit('Symlink in map source')
 if p.is_file():report['files'][p.relative_to(root).as_posix()]=hashlib.sha256(p.read_bytes()).hexdigest()
kind,_,v=load((root/'level.dat').read_bytes());level=plain((kind,v));level=level.get('Data',level)
report['level']={k:level[k] for k in ['DataVersion','Version','LevelName','spawn','DataPacks'] if k in level}
coords=[]
for path in sorted((root/'dimensions/minecraft/overworld').rglob('*.mca')):
 raw=path.read_bytes()
 if len(raw)<8192:report['errors'].append('Truncated region '+path.name);continue
 for i in range(1024):
  offset=int.from_bytes(raw[4*i:4*i+3],'big')*4096;sectors=raw[4*i+3]
  if not offset:continue
  try:
   size=int.from_bytes(raw[offset:offset+4],'big');kind=raw[offset+4]
   if kind&128:raise ValueError('External chunk storage needs separate review')
   if size<1 or size+4>sectors*4096 or offset+4+size>len(raw):raise ValueError('Invalid chunk length')
   payload=raw[offset+5:offset+4+size]
   if kind==2:
    obj=zlib.decompressobj();payload=obj.decompress(payload,64*1024*1024)
    if obj.unconsumed_tail:raise ValueError('Chunk expansion exceeds limit')
   elif kind==1:payload=gzip.decompress(payload)
   elif kind!=3:raise ValueError('Unsupported chunk compression '+str(kind))
   typ,_,value=load(payload);x=plain((typ,value));report['data_versions'][x.get('DataVersion','unknown')]+=1
   if path.parent.name=='region':
    report['chunks']+=1;coords.append((x['xPos'],x['zPos']))
    for section in x.get('sections',[]):
     for b in section.get('block_states',{}).get('palette',[]):
      name=b.get('Name','') if isinstance(b,dict) else b.split('[')[0]
      if name and not name.startswith('minecraft:'):report['custom_blocks'].add(name)
    for b in x.get('block_entities',[]):
     report['block_entities'][b.get('id','unknown')]+=1
     if 'command_block' in b.get('id',''):report['command_blocks'].append({k:b.get(k) for k in ['id','x','y','z']})
     if 'sign' in b.get('id',''):
      text=[]
      for side in ['front_text','back_text']:
       for message in b.get(side,{}).get('messages',[]):
        if isinstance(message,str):
         try:message=json.loads(message)
         except ValueError:pass
        if isinstance(message,dict):message=message.get('text','')
        if isinstance(message,str) and message.strip():text.append(message[:120])
      if text:report['signs'].append({'pos':[b.get(k) for k in ['x','y','z']],'text':text})
   for e in x.get('Entities',x.get('entities',[])):
    report['entities'][e.get('id','unknown')]+=1
    if e.get('id')=='minecraft:painting' and isinstance(e.get('variant'),str) and not e['variant'].startswith('minecraft:'):report['custom_paintings'].add(e['variant'])
  except Exception as error:report['errors'].append(str(path.relative_to(root))+':'+str(i)+':'+type(error).__name__+':'+str(error))
if coords:report['chunk_bounds']={'min-x':min(x for x,z in coords)*16,'max-x':max(x for x,z in coords)*16+15,'min-z':min(z for x,z in coords)*16,'max-z':max(z for x,z in coords)*16+15}
for key in ['custom_blocks','custom_paintings']:report[key]=sorted(report[key])
for key in ['data_versions','block_entities','entities']:report[key]=dict(report[key])
out=Path('.backups/tsxmas-map-audit');out.mkdir(parents=True,exist_ok=True)
(out/'source-audit.json').write_text(json.dumps(report,indent=2)+'\n')
print(json.dumps({k:v for k,v in report.items() if k not in ['files','signs','level','errors']},indent=2));print('Audit errors:',len(report['errors']))
print('Signs found:',len(report['signs']))
