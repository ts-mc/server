#!/usr/bin/env bash

set -euo pipefail

HOST="p-ash-13.apollopanel.com"
PORT="2022"
USER="mihaiu.devbdr.dec2515e"

PROJECT="$HOME/github/ts-server"
STAGING="$PROJECT/.remote-sync"

mkdir -p "$STAGING"

echo "Connecting to Sparked Host..."
echo "Downloading server configuration to:"
echo "  $STAGING"
echo

lftp -u "$USER" -p "$PORT" "sftp://$HOST" <<EOF
set sftp:auto-confirm yes

mirror --verbose \
  --parallel=4 \
  --exclude-glob logs/ \
  --exclude-glob cache/ \
  --exclude-glob libraries/ \
  --exclude-glob versions/ \
  --exclude-glob world/ \
  --exclude-glob world_nether/ \
  --exclude-glob world_the_end/ \
  --exclude-glob world_lobby/ \
  --exclude-glob event_arena/ \
  --exclude-glob event_lobby/ \
  --exclude-glob '*.log' \
  --exclude-glob '*.zip' \
  --exclude-glob 'server.jar' \
  / "$STAGING"

bye
EOF

echo
echo "Download complete."
echo "Remote snapshot is available at:"
echo "  $STAGING"
