#!/usr/bin/env python3
import argparse,hashlib,json,re,subprocess,zipfile
from pathlib import Path
p=argparse.ArgumentParser()
p.add_argument('--java-home',required=True);p.add_argument('--paper-api',required=True)
p.add_argument('--original',required=True);p.add_argument('--output',required=True)
a=p.parse_args();root=Path(__file__).resolve().parent
if hashlib.sha256(Path(a.original).read_bytes()).hexdigest() != 'caf58c7143c05d7298c36359ab51a9cab1c33545a30ed427d6efbc702a05da11': raise SystemExit('Original WorldServer hash does not match the reviewed artifact')
classes=root.parents[1]/'.backups/paper26-upgrade/worldserver-classes';classes.mkdir(parents=True,exist_ok=True)
source=root/'worldserver-compat/src/dev/efnilite/ws/vilib/util/Version.java'
subprocess.run([a.java_home+'/bin/javac','--release','25','-proc:none','-cp',a.paper_api,'-d',str(classes),str(source)],check=True)
test=classes/'VersionTest.java'
test.write_text('''import dev.efnilite.ws.vilib.util.Version;
public class VersionTest {
 public static void main(String[] args) {
  if(Version.fromMinecraftVersion("1.21.11-R0.1-SNAPSHOT")!=Version.V1_21) throw new AssertionError();
  if(Version.fromMinecraftVersion("26.3")!=Version.V26_3) throw new AssertionError();
  if(Version.fromMinecraftVersion("26.3.build.251-stable")!=Version.V26_3) throw new AssertionError();
  if(Version.fromMinecraftVersion("1.20.5")!=Version.V1_20_5) throw new AssertionError();
  try {Version.fromMinecraftVersion("1.8.8");throw new AssertionError();} catch(IllegalArgumentException expected){}
  System.out.println("WorldServer version parser: 5 cases passed");
 }
}''')
cp=str(classes)+':'+a.paper_api
subprocess.run([a.java_home+'/bin/javac','--release','25','-cp',cp,'-d',str(classes),str(test)],check=True)
subprocess.run([a.java_home+'/bin/java','-cp',cp,'VersionTest'],check=True)
entry='dev/efnilite/ws/vilib/util/Version.class'
with zipfile.ZipFile(a.original) as old,zipfile.ZipFile(a.output,'w',zipfile.ZIP_DEFLATED) as out:
 for info in old.infolist():
  if re.match(r'META-INF/.*\.(SF|RSA|DSA)$',info.filename):raise SystemExit('Refusing to alter a signed original')
  data=old.read(info)
  if info.filename==entry:data=(classes/entry).read_bytes()
  if info.filename=='plugin.yml':data=re.sub(rb'(?m)^version:.*$',b'version: 1.9.4-theysix.1',data)
  out.writestr(info,data)
print('Built local WorldServer 1.9.4-theysix.1:',hashlib.sha256(Path(a.output).read_bytes()).hexdigest())
