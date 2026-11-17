#!/usr/bin/env python3
import hashlib,json,re,shutil,zipfile
from pathlib import Path
from prepare import descriptor,EXCLUDED
base=Path('.backups/paper26-upgrade');candidate=base/'candidate/plugins';test=Path('/tmp/theysix-upgrade-26.3-final');out=base/'upload'
if out.exists():raise SystemExit('Upload folder exists; preserve it and choose a fresh package location')
result=json.loads((test/'result.json').read_text())
if not(result['started'] and result['exit_code']==0 and not result['errors'] and result['tsxmas_ready'] and result['status_ping']['ok']):raise SystemExit('Final startup/status acceptance did not pass')
manifest=json.loads((base/'candidate/manifest.json').read_text());byname={r['plugin']:r for r in manifest}
(out/'server/plugins').mkdir(parents=True)
for source in sorted(candidate.glob('*.jar')):
 data=source.read_bytes();name,version=descriptor(data)
 if name in EXCLUDED:raise SystemExit('Excluded plugin in candidate')
 if hashlib.sha256((test/'plugins'/source.name).read_bytes()).digest()!=hashlib.sha256(data).digest():raise SystemExit('Tested artifact differs: '+name)
 record=byname[name]
 if record['sha256']!=hashlib.sha256(data).hexdigest():raise SystemExit('Manifest hash differs: '+name)
 record['same_bytes']=not record['changed']
 shutil.copy2(source,out/'server/plugins'/source.name)
xmas=Path('projects/tsxmas/target/TSXMAS.v1.0.0.jar');data=xmas.read_bytes()
if hashlib.sha256((test/'plugins'/xmas.name).read_bytes()).digest()!=hashlib.sha256(data).digest():raise SystemExit('TSXMAS differs from tested artifact')
shutil.copy2(xmas,out/'server/plugins'/xmas.name)
manifest.append({'plugin':'TSXMAS','new_file':xmas.name,'version':'1.0.0','sha256':hashlib.sha256(data).hexdigest(),'source':'projects/tsxmas','note':'Christmas staging candidate; disabled by default. Map and player acceptance incomplete.','old_file':None,'changed':True})
for folder in ['modules','libraries']:
 src=candidate/'BlueArcade3'/folder
 for source in src.rglob('*.jar'):
  relative=source.relative_to(candidate);tested=test/'plugins'/relative
  if not tested.exists() or hashlib.sha256(tested.read_bytes()).digest()!=hashlib.sha256(source.read_bytes()).digest():raise SystemExit('Minigame dependency changed during test: '+str(relative))
  target=out/'server/plugins'/relative;target.parent.mkdir(parents=True,exist_ok=True);shutil.copy2(source,target)
paper=Path('/tmp/tsxmas-paper-26.3.jar');expected='3e7591f1c5b40a379c0ef494bcbeda128a62f5819e32cfc5e6335880bee39c55'
if hashlib.sha256(paper.read_bytes()).hexdigest()!=expected:raise SystemExit('Paper hash mismatch')
shutil.copy2(paper,out/'server/server.jar')
report={**result,'plugin_count':len(manifest),'loaded_bluearcade_modules':19,'java':'Temurin 25.0.5','paper_build':'26.3-251','paper_sha256':expected,'full_live_configurations_tested':False,'world_data_included':False,'notes':['Expected missing production world folders in isolated Multiverse test','Optional LibsDisguises hosting network check failed','Player joins and gameplay not tested']}
(out/'test-result.json').write_text(json.dumps(report,indent=2)+'\n')
(out/'manifest.json').write_text(json.dumps(manifest,indent=2)+'\n')
remove=sorted({r['old_file'] for r in manifest if r.get('old_file') and r['old_file']!=r['new_file']})
(out/'REMOVE-OLD-JARS.txt').write_text('Stop server and make full backup first. Remove ONLY these old JAR files, not plugin data folders.\nCheck actual LIVE plugin identities too; filenames may differ.\n\n'+'\n'.join('plugins/'+n for n in remove)+'\n\nAlso check for old TSStaff and PlayerLogs JARs (not present in this local root).\nDo not retain two active JARs for the same plugin. Never reactivate TSEvent or excluded plugins.\n')
shutil.copy2('projects/paper26-upgrade/README.md',out/'README-FIRST.md')
# Source provenance and relevant licenses, without Git internals or private runtime files.
sources=out/'sources';sources.mkdir()
with zipfile.ZipFile(sources/'WorldServer-corresponding-source.zip','w',zipfile.ZIP_DEFLATED) as z:
 src=base/'WorldServer-source'
 for p in src.rglob('*'):
  if p.is_file() and '.git' not in p.relative_to(src).parts:z.write(p,Path('WorldServer-1.9.4')/p.relative_to(src))
 z.write(base/'vilib-original-source.zip','vilib-c8976daa22.zip')
 for p in Path('projects/paper26-upgrade/worldserver-compat').rglob('*'):
  if p.is_file():z.write(p,Path('theysix-compat')/p.relative_to('projects/paper26-upgrade/worldserver-compat'))
 z.write('projects/paper26-upgrade/build-worldserver-compat.py','build-worldserver-compat.py')
with zipfile.ZipFile(sources/'EasyCommandBlocker-Paper-source.zip','w',zipfile.ZIP_DEFLATED) as z:
 src=base/'EasyCommandBlocker-source'
 for p in src.rglob('*'):
  if p.is_file() and '.git' not in p.relative_to(src).parts:z.write(p,Path('upstream')/p.relative_to(src))
 z.write('projects/paper26-upgrade/build-ecb-paper.py','build-ecb-paper.py')
with zipfile.ZipFile(sources/'TSStaff-source.zip','w',zipfile.ZIP_DEFLATED) as z:
 for p in Path('plugins/TSStaff').rglob('*'):
  if p.is_file() and (p.name in ['README.md','build.sh','config.yml.example'] or 'src' in p.relative_to('plugins/TSStaff').parts):z.write(p,p.relative_to('plugins'))
checks=[]
for p in sorted(out.rglob('*')):
 if p.is_file():checks.append(hashlib.sha256(p.read_bytes()).hexdigest()+'  '+p.relative_to(out).as_posix())
(out/'SHA256SUMS.txt').write_text('\n'.join(checks)+'\n')
archive=base/'TheySix-Paper-26.3-staging.zip'
with zipfile.ZipFile(archive,'w',zipfile.ZIP_DEFLATED) as z:
 for p in out.rglob('*'):
  if p.is_file():z.write(p,p.relative_to(out))
print(json.dumps({'zip':str(archive),'bytes':archive.stat().st_size,'sha256':hashlib.sha256(archive.read_bytes()).hexdigest(),'plugins':len(manifest),'modules':19},indent=2))
