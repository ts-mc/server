import importlib.util,math
from pathlib import Path
import yaml
spec=importlib.util.spec_from_file_location('additive_config',Path(__file__).with_name('leaderboard-config.py'));m=importlib.util.module_from_spec(spec);spec.loader.exec_module(m)
GRAD='<gradient:#B3000C:#E40010:#D8D8D8:#1FD537:#00B32C>'
def edits(name,text,positions=None):
 current=yaml.safe_load(text);changes=[]
 def put(path,value):
  nonlocal text,current
  import re
  cursor=current
  for key in path[:-1]:cursor=cursor.setdefault(key,{})
  cursor[path[-1]]=value
  root=path[0];rendered=yaml.safe_dump({root:current[root]},allow_unicode=True,sort_keys=False,width=1000)
  pattern=r"(?m)^"+re.escape(root)+r":[^\n]*(?:\n(?![^ \n#])[^\n]*)*\n?"
  match=re.search(pattern,text)
  if match:text=text[:match.start()]+rendered+text[match.end():]
  else:text=text.rstrip()+"\n"+rendered
  current=yaml.safe_load(text);changes.append('.'.join(path))
 file=Path(name).name
 if file=='events.yml':
  for k,v in [('marker-horizontal-radius',1.5),('marker-vertical-radius',2.5)]:
   if k not in current['delivery']:put(('delivery',k),v)
 elif file=='locations.yml':
  for npc,skin in [('santa','SantaClaus'),('market','MHF_Villager')]:
   if 'skin' not in current['npcs'][npc]:put(('npcs',npc,'skin'),skin)
   put(('npcs',npc,'hide-name'),True)
  for key,title,lines in [('santa','SANTA’S DELIVERY',['<white>Help Santa deliver village presents.</white>','<green>Right-click Santa to start your route</green>','<gray>/xmas delivery progress</gray>']),('market','CHRISTMAS MARKET',['<white>Seasonal gifts • Cosmetics • Keepsakes</white>','<green>Right-click the merchant to browse</green>','<gray>Currency: Christmas tokens</gray>'])]:
   if key not in current['holograms']:put(('holograms',key),{})
   put(('holograms',key,'npc'),key);put(('holograms',key,'height'),2.7);put(('holograms',key,'lines'),[GRAD+'<bold>'+title+'</bold></gradient>']+lines)
  for key,lines in [('calendar',[GRAD+'<bold>ADVENT CALENDAR</bold></gradient>','<white>25 days • 25 Christmas surprises</white>','<green>/xmas calendar</green>','<gray>December 1–25 • Europe/Bucharest</gray>']),('tokens',[GRAD+'<bold>YOUR CHRISTMAS JOURNEY</bold></gradient>','<white>Tokens: <green>%tsxmas_tokens%</green></white>','<white>Presents found: <green>%tsxmas_hunt_found%</green></white>','<gray>View every leaderboard: /xmas top</gray>'])]:
   if key in current['holograms']:put(('holograms',key,'lines'),lines)
 elif file=='messages.yml':
  defaults=yaml.safe_load((m.RESOURCES/'messages.yml').read_text())
  for key in ['activity-state-info','delivery-start-button','delivery-start-hint','activity-complete','activity-visit','hunt-search-hint']:
   if key not in current:put((key,),defaults[key])
  put(('delivery-started',),'Your route is ready! Right-click each village mailbox or its bamboo fence. Check /xmas delivery progress for remaining stops.')
  put(('location-saved',),'Location saved. NPCs and attached holograms have been refreshed.')
 elif file=='regions.yml':
  if positions is None:return text,[]
  targets={**{k:v for k,v in positions['points'].items() if k in ['forest-present','candy-cane','snowman','house-one','house-two','house-three','workshop','market','grinch','secret']},**{'npc-'+k:v for k,v in positions['npcs'].items()}}
  for key,point in targets.items():
   if not point.get('configured'):continue
   region='tsxmas-interact-'+key
   if region in current['regions']:continue
   x,y,z=[math.floor(point[k]) for k in ['x','y','z']]
   put(('regions',region),{'min':{'x':x-2,'y':y-3,'z':z-2},'max':{'x':x+2,'y':y+4,'z':z+2},'members':{},'flags':{'use':'allow','interact':'allow','chest-access':'deny'},'owners':{},'type':'cuboid','priority':10})
 yaml.safe_load(text);return text,changes
