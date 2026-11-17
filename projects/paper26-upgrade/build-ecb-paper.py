#!/usr/bin/env python3
from pathlib import Path
import subprocess,zipfile,re,hashlib
root=Path('.backups/paper26-upgrade');up=root/'EasyCommandBlocker-source';src=root/'ecb-paper-src';classes=root/'ecb-paper-classes'
if hashlib.sha256(Path('plugins/EasyCommandBlocker-1.18.1.jar').read_bytes()).hexdigest() != 'a94b28fe2e581628cfb322f3150c052c9d6977a59111a3e42b8bcb5e58191c67': raise SystemExit('Original ECB hash does not match the reviewed artifact')
classes.mkdir(exist_ok=True)
files=[]
for group in ['spigot','core']:
 for original in (up/'src/main/java/ecb/ajneb97'/group).rglob('*.java'):
  target=src/original.relative_to(up/'src/main/java');target.parent.mkdir(parents=True,exist_ok=True)
  target.write_text(original.read_text().replace('org.simpleyaml','ecb.ajneb97.libs.simpleyaml'))
  files.append(str(target))
cp=':'.join([str(p) for p in Path('/tmp/tsxmas-final-26.3/libraries').rglob('*.jar')]+['plugins/EasyCommandBlocker-1.18.1.jar',str(root/'candidate/plugins/ProtocolLib.jar'),str(root/'candidate/plugins/ViaVersion-5.12.2.jar')])
subprocess.run(['/tmp/jdk-25.0.5+8/bin/javac','--release','25','-proc:none','-encoding','UTF-8','-cp',cp,'-d',str(classes),*files],check=True)
out=root/'candidate/plugins/EasyCommandBlocker-1.19.1-theysix.1.jar'
with zipfile.ZipFile('plugins/EasyCommandBlocker-1.18.1.jar') as old,zipfile.ZipFile(out,'w',zipfile.ZIP_DEFLATED) as jar:
 for info in old.infolist():
  name=info.filename
  if name in ['plugin.yml','bungee.yml','velocity-plugin.json'] or name.startswith(('ecb/ajneb97/spigot/','ecb/ajneb97/core/','ecb/ajneb97/velocity/','ecb/ajneb97/waterfall/')):continue
  jar.writestr(info,old.read(info))
 for file in classes.rglob('*.class'):jar.write(file,file.relative_to(classes))
 text=(up/'src/main/resources/plugin.yml').read_text().replace('${project.version}','1.19.1-theysix.1')
 jar.writestr('plugin.yml',text)
 jar.writestr('META-INF/LICENSE-EasyCommandBlocker.txt',(up/'LICENSE').read_bytes())
 for file in (up/'src/main/resources').rglob('*'):
  if file.is_file() and file.name not in ['plugin.yml','bungee.yml','velocity-plugin.json'] and file.relative_to(up/'src/main/resources').as_posix() not in old.namelist():
   jar.write(file,file.relative_to(up/'src/main/resources'))
print('Built',out,hashlib.sha256(out.read_bytes()).hexdigest())
