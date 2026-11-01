#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
server_archive="${1:-$repo_root/server.jar}"
destination="${2:-/tmp/TSStaff-1.3.0.jar}"
jdk_path="${TSSTAFF_JDK:-}"

if [[ -z "$jdk_path" ]]; then
  if command -v javac >/dev/null 2>&1; then
    javac_path="$(command -v javac)"
    jdk_path="$(cd "$(dirname "$javac_path")/.." && pwd)"
  else
    echo "Set TSSTAFF_JDK to a Java 21 JDK directory." >&2
    exit 1
  fi
fi

if [[ ! -f "$server_archive" ]]; then
  echo "Paper server archive not found: $server_archive" >&2
  exit 1
fi

temp_dir="$(mktemp -d /tmp/tsstaff-build.XXXXXX)"
trap 'rm -rf "$temp_dir"' EXIT
mkdir -p "$temp_dir/libs" "$temp_dir/classes"

python3 - "$server_archive" "$temp_dir/libs" <<'PY'
from pathlib import Path
from sys import argv
from zipfile import ZipFile

with ZipFile(argv[1]) as archive:
    for name in archive.namelist():
        if name.startswith("META-INF/libraries/") and name.endswith(".jar"):
            (Path(argv[2]) / Path(name).name).write_bytes(archive.read(name))
PY

api_jar="$(find "$temp_dir/libs" -maxdepth 1 -name 'paper-api-*.jar' -print -quit)"
if [[ -z "$api_jar" ]]; then
  echo "No embedded Paper API found in the server archive." >&2
  exit 1
fi
classpath="$(find "$temp_dir/libs" -maxdepth 1 -name '*.jar' -print | paste -sd: -)"
mapfile -t sources < <(find "$(dirname "${BASH_SOURCE[0]}")/src/main/java" -name '*.java' -print)
"$jdk_path/bin/javac" --release 21 -proc:none -encoding UTF-8 -cp "$classpath" \
  -d "$temp_dir/classes" "${sources[@]}"
cp "$(dirname "${BASH_SOURCE[0]}")/src/main/resources/"* "$temp_dir/classes/"
"$jdk_path/bin/jar" --create --file "$destination" -C "$temp_dir/classes" .
echo "Built $destination"
