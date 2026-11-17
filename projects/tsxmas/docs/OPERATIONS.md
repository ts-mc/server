# Deployment, map import, resource pack and acceptance

## Backup and rollback

The plugin upgrade was deployed via SFTP at the owner's explicit request; see
`projects/paper26-upgrade/DEPLOYMENT.md`. The owner confirmed a separate full
server backup. This repository is configuration material, not a recoverable
server backup. `/tsxmas backup` snapshots only its SQLite database and six
configuration files. It does not back up worlds or other plugins.

Before importing or upgrading, announce maintenance, stop LIVE cleanly, and use
the hosting panel to take a full off-server backup including all worlds,
playerdata, plugin data, JAR versions, configs and private keys. Keep secrets in
private backups only. Record checksums and test restoring that backup to an
isolated staging instance before changing LIVE. Preserve a separate untouched
pre-upgrade backup; Paper 26.3 worlds cannot be downgraded safely.

Rollback means stopping the upgraded server and restoring the entire matching
pre-upgrade backup, including plugin/player/economy data. Replacing only the
server JAR after worlds have converted is not rollback.

For TSXMAS-only restore: stop the server, copy its entire current data directory
to a private recovery folder (including any WAL/SHM files), verify the selected
snapshot with SQLite `PRAGMA integrity_check`, and restore the snapshot database
and its six matching configs together. Move old WAL/SHM files into the recovery
folder before startup so they cannot be replayed against the restored snapshot.
Never overwrite a running database. Keep both recovery and selected snapshots.
Do not replay rewards from backups without reconciling post-snapshot deliveries;
restoring the ledger cannot roll back items or external economies automatically.

## Safe WeihnachtsMC import

1. The owner supplied `WeihnachtsMC-Village/`. Its offline audit and prepared
   import copy are documented in [MAP-AUDIT.md](MAP-AUDIT.md). The original is
   preserved and ignored by Git. Do not install the original WeihnachtsMC plugin,
   regenerate this server with its seed, or change level-name.
2. Audit the archive offline before extraction: reject absolute paths, traversal,
   symlinks, unreasonable uncompressed sizes and executables/JARs. Read the map's
   redistribution terms. Keep the original archive outside Git.
   `python3 tools/audit-map.py /path/to/map.zip` provides an initial read-only
   inventory. It does not extract files, execute datapacks or certify compatibility.
3. Inspect `level.dat` DataVersion/Version and dimensions. The author labels it
   Java 26.3. Do not open it with Paper 1.21.11 or attempt a blind downgrade.
   A schematic-only transplant is a separate conversion project requiring
   block/entity and custom data validation, not a supported automatic import.
4. The supplied pack was inspected: 20 painting definitions, 3 jukebox songs,
   61 German impossible-trigger advancements with no rewards, and global vanilla
   dimension/biome overrides. The prepared copy retains only namespaced visual
   registries; it excludes the old advancements and global worldgen overrides.
   Per-world saved generation settings preserve the snow-biome generator.
5. Keep any unreviewed function/recipe/advancement gameplay disabled. Worldgen
   dependencies may be required to load the map: do not remove them blindly.
   First test with reviewed required worldgen data and with no original plugin.
   Verify missing registry errors, chunk generation at the edge and load/tick
   functions. Do not enable datapacks with conflicting scoreboards, commands,
   loot or rewards. Document which parts were retained and why.
6. In a private 26.3 staging server, extract a **copy** into the new folder
   `tsxmas_christmas_2026`. Refuse if that folder already exists. Never copy over
   `world`, `world_lobby`, nether/end, PG worlds or historical event worlds.
   Remove copied session locks/UID only on the staging copy as required by the
   world manager; preserve the original archive. Do not import a duplicate UID.
7. For a fresh audited 26.3 copy, use
   `/mv import tsxmas_christmas_2026 normal --skip-folder-check` if Multiverse's
   older folder validator rejects its native dimension layout. LIVE has already
   been imported: do not repeat this there. Paper moved its terrain into
   `world/dimensions/minecraft/tsxmas_christmas_2026/`; see MAP-AUDIT.md.
   Verify that name is new before executing it. Test the exact installed
   Multiverse release on 26.3 first; do not register the same world in two
   competing managers. The locally built WorldServer 1.9.4-theysix.1 version
   compatibility fix has passed the 26.3 plugin-set startup test.
8. Confirm existing lobby/survival folder contents and Multiverse registrations
   remain unchanged. Inspect chunks, interiors, entity counts, custom data and
   log warnings. Establish an event boundary inside the reviewed venue. Record
   coordinates only after an in-game survey.
9. No production import is authorized by a successful local TSXMAS build. Review
   the actual imported world and upgrade test before scheduling deployment.

## Protection and contest setup

Keep FAWE installed as the WorldEdit provider; install a reviewed compatible
FAWE/WorldGuard pair before the upgrade test. Do not substitute regular WorldEdit
alongside FAWE. On the Christmas world only, select the venue and create a
`tsxmas-venue` WorldGuard region. Deny PvP, explosions, fire spread, mob griefing
and untrusted construction. Allow only the interaction flags required by the
chosen inert markers/NPCs. Test each flag with a non-op player: staff bypasses
can hide mistakes.

Create `tsxmas-build` inside the venue and allow contest building there through
appropriate WorldGuard membership/region rules. Configure disjoint cuboid plots
inside it in locations.yml. TSXMAS adds plot ownership and submitted-build
freezing checks; it never overrides WorldGuard cancellation. The configured
`build.region` is descriptive in this candidate; automatic region creation,
membership and region-containment checks are not implemented. Staff must verify
the actual region bounds and permissions before starting the contest.

TSXMAS cancels non-contest placement/breaking in the configured event world when
enabled, and blocks buckets, pistons, fluids, explosions, burning and entity
block changes. WorldGuard must still protect hanging entities, armor stands,
containers, farmland, indirect damage and other mechanics. This is why startup
of an event requires WorldGuard by default. Do not advertise grief protection
until adversarial in-game checks pass.

For optional schematic export, staff uses FAWE to select a submitted plot and
`//copy` then `//schem save <reviewed-name>`. Verify the selected world and bounds
before each operation; keep exports outside Git. Automatic FAWE export/reset
is intentionally absent from this candidate. Never clear or paste over plots
automatically when advancing a round.

## Resource pack

The owner approved mandatory Java packs on November 15. The targeted SFTP
deployment is recorded in [RESOURCE-PACK.md](RESOURCE-PACK.md) and
`pack-deployment.json`: Paper serves the required venue + LIVE BetterModel pack,
and TSXMAS adds the required seasonal pack from its official author URL.
The top-level event enabled flag remains false until venue setup is finished;
follow [PLAYBOOK.md](PLAYBOOK.md). TSXMAS itself does not edit server.properties.

Verified download:

- [A Very Minecraft Christmas 1.3.0 for 26.3](https://modrinth.com/resourcepack/a-very-minecraft-christmas/version/5VWcQiKa)
- SHA-1: `848a7a8e8d2ef149bec10b336b77185cb15936a2`
- Downloaded ZIP: 680 entries; pack.mcmeta declares min_format 75, max_format 999.
  This metadata and hash check does not establish visual compatibility with the map.

For other installations, OPT_IN sends only on `/xmas pack
on`; SEASONAL sends on join. TSXMAS adds its UUID pack to Java's stack and removes
only that UUID with `/xmas pack off`, preserving existing packs. Required status
is configurable; a client declining a required pack may be disconnected. Use
optional mode unless the owner approves mandatory seasonal textures. Mandatory
status has been approved for this LIVE deployment. Restart/client acceptance
still needs the checks in RESOURCE-PACK.md.

For reference, the active Paper venue-pack settings are below. TSXMAS sends the
different seasonal ZIP, preserving the venue pack in the Java stack:

```properties
resource-pack=https://files.catbox.moe/0xupdx.zip
resource-pack-sha1=dd85146b2795270bdbf6bb4cdf50704d0ceb6733
resource-pack-id=8c4ad7b3-c182-4c6b-9f07-42c6ef4a0c66
resource-pack-prompt={"text":"TheySix Events needs this pack for Christmas village models and textures.","color":"aqua"}
require-resource-pack=true
```

Self-host a permitted copy on an HTTPS endpoint returning the ZIP bytes directly
without login, HTML redirects or expiring links. Verify redistribution rights
before hosting. Compute `sha1sum christmas-1.3.0.zip` on the **final hosted file**;
any merge/recompression changes the hash. Test download size, certificate, cache
invalidation and client decline/download-failure behavior.

The map's resources.zip contains `bettermodel`, `weihnachtsmc` and vanilla assets.
A private merged ZIP was built at
`.backups/tsxmas-map-audit/TheySix-Christmas-26.3-merged.zip`. Its SHA-1 is
`6f82f431b22c30267c22cb65d09e89f302d74e6f`. The 38 overlapping snow/GUI textures
use the seasonal pack's versions; map-specific namespaces are retained. The
original pack's vanilla background-music mute was omitted. See MAP-AUDIT.md.

That older combined ZIP is **not hosted or enabled**. The deployed policy uses
a newly built venue + current LIVE BetterModel ZIP and a separate unchanged
official seasonal download. Do not enable the old private combined artifact.
See RESOURCE-PACK.md for hashes, attribution, hosting and rollback.

Java ZIPs are not Bedrock packs. Floodgate UUID detection skips Java pack sends
for Bedrock players. Keep all commands, marker actions and rewards usable with
vanilla visuals. A licensed Bedrock conversion is a separate asset project;
only a tested `.mcpack` belongs in Geyser's runtime packs directory. Keep
Floodgate authentication and keys private and preserve ItemJoin's `BR_` setting.

## Migration and upgrades

Database schema 1 is created transactionally; newer schemas are refused.
`/tsxmas migrate` reports the current migration policy; there is no blind import
of other plugins' data. Before future versions, take a TSXMAS snapshot and full
server backup, read the migration notes, test against a clone and reconcile held
rewards. Never delete data to make an upgrade start.

Use a distinct season for testing; promote configuration without copying test
wallets, claims, gifts, purchases or submissions into production. Changing
season or year requires a restart. Preserve the test database privately for
audit. An existing production season should never be silently replaced.

## Required acceptance before release

- [ ] All affected plugins updated to approved compatible builds; full clone starts without errors.
- [ ] EssentialsX active; moderation webhook, vote leaderboard, BlueArcade modules and status pings checked.
- [ ] Real map archive/license/datapacks/resources inspected; new world imported; original worlds unchanged.
- [ ] Every point, boundary, WorldGuard flag and disjoint plot verified with non-op Java and Bedrock players.
- [ ] Every command/permission/tab completion and expired/reused confirmation tested.
- [ ] Calendar December boundaries, missed-day rules, duplicate clicks and preview tested.
- [ ] Token/XP/item payouts verified, full inventory/disconnect/server-crash cases reconciled.
- [ ] Hunt/delivery completion and resets, timers, pause/resume and restart verified.
- [ ] Build moderation, frozen plots, anti-grief, self/duplicate votes, tie handling and prizes verified.
- [ ] Secret Santa odd/even registrations, no self-match, anonymity, gift escrow and reveal verified.
- [ ] Raffle weighted drawing, purchase caps, unique winners and repeat-refund safety verified.
- [ ] Market stock, limits, restart persistence and WorldServer/Vault world context verified.
- [ ] Optional NPC/hologram/music adapters verified against selected builds; missing-plugin fallbacks checked.
- [ ] Resource-pack accept/decline/fail/cache behavior, stacking, map assets and Bedrock fallback tested.
- [ ] Backup restored to a separate test instance; staged-file/secret checks passed; deployment separately approved.

Automated unit and startup tests cover only the specific results in VALIDATION.md.
Unchecked acceptance items are open release gates, not implicit passes.
