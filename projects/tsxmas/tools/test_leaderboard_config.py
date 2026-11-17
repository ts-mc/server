import importlib.util,unittest
from pathlib import Path
import yaml
spec=importlib.util.spec_from_file_location('leaderboard_patch',Path(__file__).with_name('leaderboard-config.py'));patch=importlib.util.module_from_spec(spec);spec.loader.exec_module(patch)

class AdditiveConfigTests(unittest.TestCase):
 def test_existing_policy_and_crlf_are_preserved(self):
  old='schema-version: 1\r\nenabled: true\r\nresource-pack: {required: true}\r\n'
  text,_=patch.edits('plugins/TSXMAS/config.yml',old);parsed=yaml.safe_load(text)
  self.assertTrue(text.startswith(old));self.assertTrue(parsed['enabled']);self.assertEqual(parsed['resource-pack'],{'required':True});self.assertEqual(parsed['leaderboards']['limit'],10)
 def test_existing_positions_and_custom_holograms_are_not_changed(self):
  old='schema-version: 1\nworld: tsxmas_christmas_2026\npoints: {grinch: {configured: true, x: -217, y: 72, z: 587}}\nholograms:\n  owner-display: {configured: true, x: 1, lines: [Keep]}\n  leaderboard-hunt: {configured: true, x: 3, y: 73, z: 9}\n'
  text,_=patch.edits('plugins/TSXMAS/locations.yml',old);a=yaml.safe_load(old);b=yaml.safe_load(text)
  self.assertEqual(a['points'],b['points']);self.assertEqual(a['holograms']['owner-display'],b['holograms']['owner-display'])
  self.assertEqual(b['holograms']['leaderboard-hunt'],{**a['holograms']['leaderboard-hunt'],'leaderboard':'hunt'})
  self.assertFalse(b['holograms']['leaderboard-calendar']['configured'])
 def test_existing_message_labels_prefix_and_help_remain_intact(self):
  old='schema-version: 1\nprefix: "Owner prefix "\nplayer-help: ["Keep this command"]\n'
  text,_=patch.edits('plugins/TSXMAS/messages.yml',old);parsed=yaml.safe_load(text)
  self.assertEqual(parsed['prefix'],'Owner prefix ');self.assertEqual(parsed['player-help'][0],'Keep this command');self.assertIn('leaderboard-row',parsed)
 def test_second_application_is_idempotent(self):
  for file in ['config.yml','messages.yml','locations.yml']:
   old=(patch.RESOURCES/file).read_text();first,_=patch.edits(file,old);second,changes=patch.edits(file,first)
   self.assertEqual(first,second);self.assertEqual(changes,[])
 def test_hologram_flow_mapping_is_merged_without_losing_owner_values(self):
  old='schema-version: 1\nholograms: {existing: {configured: true, x: 1}}\n'
  text,_=patch.edits('locations.yml',old);self.assertEqual(yaml.safe_load(text)['holograms']['existing'],{'configured':True,'x':1})

if __name__=='__main__':unittest.main()
