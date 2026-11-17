# Server operations

## Public endpoints

| Service | Address |
| --- | --- |
| Java | `play.theysix.tech` |
| Bedrock | `bedrock.theysix.tech:25832` |
| Main website | <https://theysix.tech> |
| Events website | <https://events.theysix.tech> |

Private connection details belong in ignored local configuration.

## Local, snapshot and LIVE

The checkout is working configuration. `.remote-sync/` is a disposable comparison
snapshot. LIVE is Sparked Host. Use inspect → compare → decide → validate → stage
→ commit → deploy. A GitHub push does not deploy the server.

`ts-sync.sh` downloads comparison files only and discovers its checkout directory
dynamically. It reads ignored `.vscode/sftp.json` and a user-owned mode-600
credential at `~/.config/ts-server/sftp-password` or `TS_SERVER_SFTP_CREDENTIAL`.
That historical credential directory name is independent of the checkout name.
Verify the SFTP host key before connecting.

Upload an explicitly reviewed file list only after deployment authorization.
Never mirror destructively or upload the entire repository root: it contains
sources, documentation and other files Minecraft does not need.

## Runtime and worlds

The last verified upgrade used Paper 26.3 and Java 25. See the
[upgrade project](projects/paper26-upgrade/README.md) for exact historical plugin
versions and test evidence. JARs are acquired separately, respecting licenses.

Take a complete off-host backup before upgrading Paper or importing a world.
Preserve player data, permissions, balances and databases. Test an isolated copy
first. Git is not a recovery source for runtime state.

Multiverse manages configured worlds. Christmas is the separate
`tsxmas_christmas_2026` world. Paper 26.3 migrated its terrain into
`world/dimensions/minecraft/tsxmas_christmas_2026/`; this is expected. Do not
recreate its former root folder or import a duplicate. Never replace lobby or
minigame worlds with event-map files.

## Christmas

- [Build and installation](projects/tsxmas/README.md)
- [Locations and event setup](projects/tsxmas/docs/PLAYBOOK.md)
- [Map, protection and backups](projects/tsxmas/docs/OPERATIONS.md)
- [Required packs and hashes](projects/tsxmas/docs/RESOURCE-PACK.md)
- [Leaderboards](projects/tsxmas/docs/LEADERBOARDS.md)
- [Mailbox/NPC repair](projects/tsxmas/docs/EVENT-REPAIR-1.1.1.md)
- [Local 1.1.2 changes, not deployed](projects/tsxmas/docs/LOCAL-1.1.2.md)

After reviewing rewards and locations, `/xmas start ready` asks for confirmation
and starts stopped activities that validate. `/xmas staff` shows states. The
build contest needs plots and WorldGuard memberships. The Advent Calendar uses
Europe/Bucharest and December 1–25.

Hologram `npc` and `height` settings anchor a label to an NPC. Skin services
require network access. Verify markers, menus and rewards with ordinary Java
and Bedrock players before announcing the event as ready.

## Permissions and integrations

LuckPerms runtime storage is authoritative. Inspect `/lp editor` and the current
plugin permission definitions before granting nodes. Do not recreate groups
from historical bulk command lists. TSXMAS separates `tsxmas.player`,
`tsxmas.staff`, `tsxmas.admin` and `tsxmas.debug`; restrict staff/admin grants.

WorldGuard and TSXMAS both protect the event map. Staff editing needs the
configured TSXMAS bypass, appropriate WorldGuard access and a build-capable
gamemode. Do not grant global building access to solve a staff setup issue.

BlueArcade uses `/ts` and `/tsa`. Inspect existing arenas before registering
anything and keep its data folder named `BlueArcade3`. See [its guide](plugins/BlueArcade3/README.md)
and [games](GAMES.md). Rewards use its credits and XP.

Keep DiscordSRV tokens and channel mappings private. Check chat relay,
AdvancedBanZ/BanWebhook notifications and vote-leaderboard visibility after
deployment. VotifierPlus and DankVotes are retained; VotingPlugin is not active.
Avoid reintroducing obsolete voting placeholders.

## Maintenance and recovery

Announce confirmed maintenance with a timezone. Channels relayed to in-game chat
must not receive internal details. Stop cleanly in the panel before replacing
JARs or editing world/database files. Retain rollback copies of every changed file.

After starting, inspect startup logs, enabled plugins, world loading, permissions,
Java/Bedrock joins and affected gameplay. Startup alone does not prove rewards or
integrations work. Verify status pings and vote leaderboards if problems recur.

For rollback, stop and restore the matching JAR/config set. Converted worlds may
need the full pre-upgrade backup; do not open them with an older server. Never
overwrite player databases with test data.
