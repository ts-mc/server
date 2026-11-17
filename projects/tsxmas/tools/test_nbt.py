import struct,unittest
from nbt import NBT,dump,load
class NBTTests(unittest.TestCase):
 def test_all_tag_types_roundtrip(self):
  root=(10,'',{'b':(1,-1),'s':(2,-123),'i':(3,5023),'l':(4,-2**63),'f':(5,1.5),'d':(6,2.75),'raw':(7,b'\x00\xff'),'text':(8,'Winter Grüße'),'list':(9,(10,[{'x':(3,-125)}])),'ints':(11,[-1,0,2147483647]),'longs':(12,[-2**63,2**63-1])})
  self.assertEqual(root,load(dump(root)))
 def test_uncompressed_roundtrip(self):
  root=(10,'Data',{'version':(3,5023)});self.assertEqual(root,load(dump(root,False)))
 def test_truncated_input_rejected(self):
  with self.assertRaises(ValueError):load(b'\x0a\x00')
 def test_negative_array_rejected(self):
  data=b'\x0a\x00\x00\x07\x00\x01x'+struct.pack('>i',-1)+b'\x00'
  with self.assertRaises(ValueError):load(data)
 def test_unknown_tag_rejected(self):
  with self.assertRaises(ValueError):load(b'\x42\x00\x00')
if __name__=='__main__':unittest.main()
