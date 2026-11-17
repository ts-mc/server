# Repository assessment

Inspected Git worktree/index/history, tracked-file inventory, plugin JAR
descriptors, server version artifacts, world registrations, representative
plugin configurations, existing permission documentation, command ownership,
resource-pack properties, ignore rules and the sync helper before implementation.
There are 802 tracked files before this change. This is not a claim that every
runtime/player file was read; credential-bearing and generated data are excluded.

There was no existing Java plugin source build at the repository root. The new
Maven project is isolated under `projects/tsxmas`, with no root build changes.
Existing project material is predominantly YAML; committed sanitized `.example`
templates coexist with ignored local credential-bearing configuration.

Local server artifacts identify Paper 1.21.11. Multiverse-Core registers the
existing main/nether/end and PG minigame worlds. WorldServer also manages world
chat/economy contexts; therefore Vault calls specify the player's current world.
No world registration or `/spawn` alias has been changed. Public endpoints remain
as documented in AGENTS.md; no connection credentials were used or documented.

BlueArcade3 remains the normal minigame system and its credits/XP rewards are
separate from TSXMAS tokens. The historical Halloween/TSEvent configuration is
inactive. A local TSEvent root JAR exists but was excluded from compatibility
tests and must not be activated. Fancy vote NPCs/holograms remain untouched.
Floodgate auth, Geyser 2.12.0 and ItemJoin prefix `BR_` are preserved.

Existing LuckPerms guidance defines owner/admin/moderator/default groups. TSXMAS
only declares its own permission hierarchy; it does not alter any group or grant
world-edit bypass permissions. Its `/xmas` and `/tsxmas` command names were checked
against the inspected MyCommand/DeluxeMenus/root aliases; no active collisions
were found. The original WeihnachtsMC plugin must not be installed alongside it.

`ts-sync.sh` currently downloads a comparison snapshot using lftp mirror into
`.remote-sync/`, excluding runtime files and worlds. It is not a deployment
pipeline and was not executed or edited. Existing owner changes in that script,
Geyser, TAB and untracked configuration/log directories were preserved. There
was no staged work at the start; no staging or commits have been performed.

Deployment remains a separate reviewed operation after full backup, compatibility
acceptance and map inspection. The new local smoke harness never copies source
worlds/configurations, never uses SFTP and never targets `.remote-sync/` or LIVE.
