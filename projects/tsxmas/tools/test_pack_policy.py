import importlib.util,unittest
from pathlib import Path
spec=importlib.util.spec_from_file_location('pack_policy',Path(__file__).with_name('pack-policy.py'))
policy=importlib.util.module_from_spec(spec);spec.loader.exec_module(policy)

class PropertySafetyTests(unittest.TestCase):
 def test_required_pack_edit_preserves_other_keys_and_crlf(self):
  before=b'# owner formatting\r\nprivate-placeholder=CHANGE_ME\r\nrequire-resource-pack=false\r\nresource-pack-prompt=old\r\n'
  after=policy.set_property(before,'require-resource-pack','true')
  self.assertEqual(after,before.replace(b'require-resource-pack=false',b'require-resource-pack=true'))
 def test_add_missing_key_without_merging_into_previous_value(self):
  self.assertEqual(policy.set_property(b'other=value','resource-pack','https://example.invalid/pack.zip'),b'other=value\nresource-pack=https://example.invalid/pack.zip\n')
 def test_replacement_is_literal(self):
  self.assertEqual(policy.set_property(b'resource-pack-prompt=old\n','resource-pack-prompt','{"text":"Pack: required"}'),b'resource-pack-prompt={"text":"Pack: required"}\n')

if __name__=='__main__':unittest.main()
