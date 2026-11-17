import gzip,io,struct
class NBT:
 def __init__(self,data):
  if len(data)>64*1024*1024:raise ValueError('NBT exceeds inspection limit')
  self.f=io.BytesIO(data)
 def read(self,n):
  b=self.f.read(n)
  if len(b)!=n:raise ValueError('Truncated NBT')
  return b
 def number(self,fmt):return struct.unpack('>'+fmt,self.read(struct.calcsize(fmt)))[0]
 def string(self):return self.read(self.number('H')).decode('utf-8','replace')
 def payload(self,t,depth=0):
  if depth>64:raise ValueError('NBT depth limit')
  if t in range(1,7):return self.number({1:'b',2:'h',3:'i',4:'q',5:'f',6:'d'}[t])
  if t==8:return self.string()
  if t in [7,9,11,12]:
   sub=self.number('B') if t==9 else None;n=self.number('i')
   if n<0 or n>4_000_000:raise ValueError('NBT array limit')
   if t==7:return self.read(n)
   if t==9:return (sub,[self.payload(sub,depth+1) for _ in range(n)])
   return [self.number('i' if t==11 else 'q') for _ in range(n)]
  if t==10:
   out={}
   while True:
    sub=self.number('B')
    if sub==0:return out
    key=self.string();out[key]=(sub,self.payload(sub,depth+1))
  raise ValueError('Unknown NBT tag')
 def root(self):
  tag=self.number('B');name=self.string();return tag,name,self.payload(tag)
def load(data):
 if data[:2]==b'\x1f\x8b':
  with gzip.GzipFile(fileobj=io.BytesIO(data)) as f:data=f.read(64*1024*1024+1)
 return NBT(data).root()
def plain(tag):
 kind,value=tag
 if kind==10:return {k:plain(v) for k,v in value.items()}
 if kind==9:return [plain((value[0],v)) for v in value[1]]
 return value
def dump(root,compress=True):
 def num(fmt,n):return struct.pack('>'+fmt,n)
 def string(s):
  b=s.encode('utf-8');return num('H',len(b))+b
 def payload(t,v):
  if t in range(1,7):return num({1:'b',2:'h',3:'i',4:'q',5:'f',6:'d'}[t],v)
  if t==8:return string(v)
  if t==7:return num('i',len(v))+v
  if t==9:return num('B',v[0])+num('i',len(v[1]))+b''.join(payload(v[0],i) for i in v[1])
  if t in [11,12]:return num('i',len(v))+b''.join(num('i' if t==11 else 'q',n) for n in v)
  if t==10:return b''.join(num('B',k)+string(n)+payload(k,x) for n,(k,x) in v.items())+b'\0'
  raise ValueError('Unsupported NBT tag')
 t,name,v=root;data=num('B',t)+string(name)+payload(t,v)
 return gzip.compress(data,mtime=0) if compress else data
