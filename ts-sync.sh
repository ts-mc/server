#!/usr/bin/env bash

set -euo pipefail


PROJECT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
CREDENTIAL_FILE="${TS_SERVER_SFTP_CREDENTIAL:-$HOME/.config/ts-server/sftp-password}"
mapfile -t sftp_connection < <(python3 - "$PROJECT/.vscode/sftp.json" <<'PY_CONNECTION'
import json,sys
with open(sys.argv[1]) as handle:
    config=json.load(handle)
for key in ("host","port","username"):
    value=str(config[key])
    if not value or any(c in value for c in "\r\n\x00"):
        raise SystemExit("Invalid local SFTP configuration")
    print(value)
PY_CONNECTION
)
if [[ ${#sftp_connection[@]} != 3 ]]; then
    echo "Configure .vscode/sftp.json before refreshing the snapshot." >&2
    exit 1
fi
HOST="${sftp_connection[0]}"
PORT="${sftp_connection[1]}"
SFTP_USER="${sftp_connection[2]}"
STAGING="$PROJECT/.remote-sync"

mkdir -p "$STAGING"

if [[ ! -f "$CREDENTIAL_FILE" || ! -O "$CREDENTIAL_FILE" || "$(stat -c %a "$CREDENTIAL_FILE")" != 600 ]]; then
    echo "Missing user-owned mode-600 SFTP credential file: $CREDENTIAL_FILE" >&2
    exit 1
fi

IFS= read -r SFTP_PASSWORD < "$CREDENTIAL_FILE"
if [[ -z "$SFTP_PASSWORD" ]]; then
    echo "SFTP credential file is empty" >&2
    exit 1
fi

lftp_quote() {
    local value=${1//\\/\\\\}
    value=${value//\"/\\\"}
    printf '"%s"' "$value"
}

echo "Connecting to Sparked Host..."
echo "Downloading server configuration to:"
echo "  $STAGING"
echo

{
    printf 'open -u %s -p %s %s\n' "$(lftp_quote "$SFTP_USER,$SFTP_PASSWORD")" "$PORT" "$(lftp_quote "sftp://$HOST")"
    unset SFTP_PASSWORD
    cat <<'LFTP_COMMANDS'
set sftp:auto-confirm no;
set net:max-retries 3;
set net:timeout 20;

mirror \
    --verbose \
    --only-newer \
    --parallel=4 \
    --exclude-glob 'PG-*/' \
    --exclude-glob '*.jar' \
    --exclude-glob '*.db' \
    --exclude-glob '*.sqlite' \
    --exclude-glob '*.sqlite3' \
    --exclude-glob '*.log' \
    --exclude-glob 'logs/' \
    --exclude-glob 'cache/' \
    --exclude-glob 'backups/' \
    --exclude-glob '.deploy-backups/' \
    --exclude-glob '.deploy-staging/' \
    --exclude-glob '*.db-*' \
    --exclude-glob '*.sqlite-*' \
    --exclude-glob '.apollo/' \
    --exclude-glob '.cache/' \
    --exclude-glob '.trash/' \
    --exclude-glob 'libraries/' \
    --exclude-glob 'versions/' \
    --exclude-glob 'server-data-dump/' \
    --exclude-glob 'ops.json' \
    --exclude-glob 'whitelist.json' \
    --exclude-glob 'banned-players.json' \
    --exclude-glob 'banned-ips.json' \
    --exclude-glob 'usercache.json' \
    --exclude-glob 'crash-reports/' \
    --exclude-glob 'world/' \
    --exclude-glob 'world_nether/' \
    --exclude-glob 'world_the_end/' \
    --exclude-glob 'world_lobby/' \
    --exclude-glob 'event_lobby/' \
    --exclude-glob 'event_arena/' \
    --exclude-glob '.event-halloween/' \
    --exclude-glob '*/region/' \
    --exclude-glob '*.zip' \
    --exclude-glob '*.tar' \
    --exclude-glob '*.tar.gz' \
    --exclude-glob '*.pem' \
    --exclude-glob '*.key' \
    --exclude-glob '*/playerdata/' \
    --exclude-glob '*/advancements/' \
    --exclude-glob '*/stats/' \
LFTP_COMMANDS
    printf '    / %s;\n\nbye;\n' "$(lftp_quote "$STAGING")"
} | lftp -f /dev/stdin

echo
echo "========================================"
echo " Remote snapshot complete."
echo "========================================"
echo
echo "LIVE server:"
echo "  Sparked Host (connection details stay private)"
echo
echo "Local snapshot:"
echo "  $STAGING"
echo
echo "IMPORTANT:"
echo "  .remote-sync is comparison data only."
echo "  Do NOT blindly copy it over the local repo."
echo
