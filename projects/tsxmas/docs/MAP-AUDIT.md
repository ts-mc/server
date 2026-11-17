# WeihnachtsMC village audit and import preparation

The owner supplied `WeihnachtsMC-Village/`. The original folder is preserved,
hash-inventoried and ignored by Git. Preparation tools did not start Java. The
owner subsequently started LIVE, imported the map and stopped it cleanly.

## Offline findings

- Minecraft **26.3**, DataVersion **5023**, matching the deployed Paper 26.3
  target. No downgrade/conversion was performed or required by the file version.
- Native 26.3 storage: `dimensions/minecraft/overworld/{region,entities,poi}`.
  This matches the generated 26.3 staging worlds; it must not be flattened into
  the pre-26.3 root `region/` layout.
- 440 terrain chunks; 629 terrain/entity NBT records read successfully; 11 MCA
  files copied. No audit parse errors, custom block IDs or command blocks found.
- Recorded spawn: **-125, 67, 543**, yaw/pitch 0. Measured saved-terrain bounds:
  X **-288..63**, Z **384..703**. These bounds describe saved terrain, not an
  in-game verification of every event area.
- 1,081 item displays, 988 interaction entities, 132 markers and 21 paintings
  were found, along with villagers/animals/hostile mobs. Decorations were
  preserved. Render/tick performance still needs a client/server test.
- The map contains 18 mob spawners and existing hostile mobs. Set this event
  world to peaceful during setup and establish protection before public entry.
- No symlinks or executable archives found. No source player-data folder was
  supplied/copied. Original Paper UUID metadata, scoreboard/bossbar state,
  chunk tickets, raids and scheduled-event data are not carried into the copy.

The source has a huge vanilla overworld biome-source override and a
`minecraft:snowy_plains` biome override. Installing those as a global datapack
could affect unrelated world generation. They are **excluded**. The copied
world's own saved generation settings retain the snow-biome generator without
installing those global overrides.

## Prepared separate world

`tsxmas_christmas_2026/` contains verified copies of the terrain, entities and
POIs, the needed per-world settings, and a rewritten display name/datapack list.
Its copied-world UUID metadata was omitted so Paper can create a new identity.
Missing references to the original plugin and absent `wmc_orte` / `wmc_hauptdorf`
packs were removed. The original source is unchanged.

The original pack contains 61 German `minecraft:impossible` advancements with
no reward functions. They are excluded rather than recreating the original
plugin's achievement system. No original WeihnachtsMC plugin JAR is installed.
Map decoration entities and their own NBT remain part of the venue.

The small `tsxmas-map-assets` datapack retains only 20 painting variants and
3 jukebox-song registry definitions in the `weihnachtsmc` namespace. All custom
painting variants referenced by saved entities have matching definitions.
There are no vanilla-namespace files, worldgen overrides, functions, loot or
advancements in this generated registry pack.

**Secondary Multiverse worlds do not supply the main server's dynamic registry
definitions merely by containing a datapacks folder.** The same registry-only
pack must be present in the primary world's `datapacks/tsxmas-map-assets/` at
server startup, before importing the secondary world. No existing pack is
replaced. The event-world copy also includes it for standalone map review.

The local TSXMAS hub and boundary now use the measured source spawn/bounds.
Other event points, build plots, NPCs and holograms remain unconfigured; their
locations need an in-game survey. Event logic remains disabled.

## LIVE file placement

After the owner confirmed a clean console `stop`, SFTP placed the separate
event-world folder, the primary-world registry pack and the measured TSXMAS
hub/boundary configuration. All 67 staged files passed SHA-256 readback before
placement; all 67 active files also passed readback after placement. The original
TSXMAS locations file is retained in
`.deploy-backups/tsxmas-map-20261114-222113/locations.yml` on LIVE.

Existing terrain/player files and world-manager registrations were not changed.
The primary world's only addition is `datapacks/tsxmas-map-assets/`; it contains
the namespaced painting/song definitions described above. Java was not started
and no Multiverse import was executed. The owner's separate full backup remains
the recovery source for the server; this targeted configuration backup does not
replace it.

## Resource pack assessment

The original `resources.zip` has 8,661 entries and the namespaces `bettermodel`,
`weihnachtsmc` and `minecraft`. Pack metadata advertises resource format 97.
No embedded JAR/executable, unsafe ZIP path, or bundled license/credits file
was found. Artwork/model assets are required for the intended decorations.

A private merged artifact was prepared:

`.backups/tsxmas-map-audit/TheySix-Christmas-26.3-merged.zip`

SHA-1: `6f82f431b22c30267c22cb65d09e89f302d74e6f`

- The published seasonal-pack SHA-1 was verified before merging.
- 38 overlapping snow/GUI textures use A Very Minecraft Christmas's versions.
- The map-specific `weihnachtsmc` and BetterModel asset namespaces are retained.
- The original vanilla background-music mute was omitted; the absent original
  plugin is not available to replace that music with its own playback.
- Registry jukebox definitions still use the original intentionally-empty
  sound event. Custom music is not claimed to play automatically.
- The original map resources.zip is preserved in the separate copy for review.
  Dedicated Paper does not automatically serve it to connecting players.

The merged ZIP is not hosted or enabled. Existing LIVE pack configuration and
the BetterModel pack stack must be reviewed before choosing a hosted merged
URL/hash or layering policy. Do not overwrite BetterModel's current pack with
this artifact. Check author terms/attribution before public hosting; the map
page credits Kevin MacLeod music and public-domain painting sources, while the
seasonal pack is by ninaxenchanted. Java packs do not install on Bedrock; vanilla
gameplay must remain usable while Bedrock asset conversion is pending.

November 15 update: that private combined artifact remains unused. The owner
approved a required venue + current LIVE BetterModel pack hosted separately
from the author's official seasonal download. See RESOURCE-PACK.md and the
verified two-file deployment receipt `pack-deployment.json`.

## Owner import and Paper storage migration

The owner's LIVE log confirms that the asset datapack loaded, all 47 plugins
initialized, TSXMAS enabled with events disabled, and startup completed.
The first import at 20:32:07 was rejected because Multiverse's folder validator
expected the older layout. The initial checklist omitted `--skip-folder-check`
for this audited 26.3 copy. The subsequent import at 20:32:31 succeeded;
Adventure mode and Peaceful difficulty were confirmed, then LIVE stopped cleanly.

Paper migrated the terrain into the primary save's separate dimension:

```text
world/dimensions/minecraft/tsxmas_christmas_2026/
```

Read-only SFTP inspection found all 11 MCA files at this new path. Nine retain
their uploaded hashes; one terrain and one entity region changed during the
server run. Multiverse registers `minecraft:tsxmas_christmas_2026`, with legacy
name `tsxmas_christmas_2026`, `gamemode: adventure`, `difficulty: peaceful` and
`auto-load: true`. The old top-level folder is absent after migration.

Do not recreate that old folder, re-upload the map, flatten its dimension or
repeat the import. The event dimension is separate from the primary overworld's
terrain despite sharing its save container. Backups must now include the full
primary world's `dimensions/` tree and matching world-manager registrations.

## On the next owner-controlled startup

The import succeeded. Run `mv list`, then use the teleport/protection steps
below. Do not repeat the import on this LIVE state.

For a **fresh isolated copy only**, the initial-import sequence is recorded here:

Use **Paper 26.3 / Java 25**. No import command has been executed automatically.
Run in console, without leading slashes:

```text
maintenance on
datapack list enabled
```

Verify `file/tsxmas-map-assets` is loaded and there are no registry/painting
decode errors. If it is missing or fails to load, stop and inspect the error
before importing. Then:

```text
mv import tsxmas_christmas_2026 normal --skip-folder-check
mv modify tsxmas_christmas_2026 set gamemode adventure
mv modify tsxmas_christmas_2026 set difficulty peaceful
mv list
```

Apply protection to this world only before allowing players in. In game:

```text
/mvtp tsxmas_christmas_2026
/tp -125 67 543
/rg flag -w tsxmas_christmas_2026 __global__ passthrough deny
/rg flag -w tsxmas_christmas_2026 __global__ pvp deny
/rg flag -w tsxmas_christmas_2026 __global__ chest-access deny
```

Do not set global `build deny`; WorldGuard recommends the passthrough policy so
future member-owned build plots can override the global region correctly.
Complete the other protection checks in OPERATIONS.md, including hanging
entities/displays, explosions, fire and containers, with a non-op test player.
Test protected discovery interactions separately; use the proximity command
fallbacks where a chest's denied vanilla interaction would cancel a click.

Survey the workshop, market, houses, forest markers, Grinch and build area, then
use `/xmas setlocation <name>`, `/xmas setnpc <name>` and
`/xmas sethologram <name>`. Configure disjoint build plots and their WG region.
Run `/tsxmas reload` and `/tsxmas validate`. Enable events only after those
checks and Java/Bedrock play tests pass. Nothing here changes global `/spawn`.

## Validation limits and evidence

Five NBT reader/writer tests passed, including numeric/array/list roundtrips,
uncompressed input and malformed-data rejection. The source level.dat roundtrip,
every source-file hash, every copied MCA hash and all required painting registry
references were checked. Prepared and uploaded artifacts have separate manifests
under the ignored `.backups/tsxmas-map-audit/` directory.

The owner's log confirms actual LIVE world import, dimension migration and a
clean stop. The read-only SFTP check confirms terrain files and registration.
No player connected in that log: entity rendering, terrain appearance, gameplay,
TPS under load still need verification. The November 15 LIVE log confirms
subsequent world autoload and player entry; required-pack client rendering
remains pending after the texture configuration fix.

Command references: [Multiverse commands](https://mvplugins.org/core/fundamentals/commands-usage/),
[world properties](https://mvplugins.org/core/fundamentals/world-properties/),
[WorldGuard global region](https://worldguard.enginehub.org/en/latest/regions/global-region/),
[region commands](https://worldguard.enginehub.org/en/latest/regions/commands/).
