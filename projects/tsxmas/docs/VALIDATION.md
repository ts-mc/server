# Validation record — 2026-11-14

November 15 leaderboard release: TSXMAS 1.1.0 passed Java 25 compilation and
**41 Java tests**, plus **five additive-configuration tests**. Rankings cover
all eight activities with season/round isolation and cached Secret Santa privacy.
See LEADERBOARDS.md and `leaderboards-1.1.0-deployment.json` for the four-file
SFTP deployment and rollback. No Paper process was started for this release;
real client GUI/hologram acceptance remains pending on owner startup.

November 15 update: TSXMAS 1.0.1 passed Java 25 compilation and all 29 unit tests
and was deployed with seven targeted Christmas display configs. See
[CHRISTMAS-1.0.1.md](CHRISTMAS-1.0.1.md) and
`christmas-1.0.1-deployment.json` for the eight-file SFTP checksum verification
and rollback path. This new build was not started on Paper by the agent; owner
startup and in-game acceptance remain pending. The 1.0.0 runtime evidence below
is historical and is not a runtime test of 1.0.1.

Release status: staging candidate. The upgraded 47-plugin set passed isolated
26.3 startup/status testing and was deployed by SFTP; see
[deployment](../../paper26-upgrade/DEPLOYMENT.md). The supplied map passed its
offline audit and its separate world/asset files were placed on LIVE; see
[map evidence](MAP-AUDIT.md). The owner's subsequent import succeeded; SFTP
inspection found all 11 migrated MCA files, 440 parsed terrain chunks and the
persisted Multiverse registration. The November 15 log confirms restart autoload
and player entry. Rendering with the new required packs and the in-game
acceptance gates remain pending. Tools did not start Java during these changes.

## Build and unit tests

Java: Temurin 25.0.5. Maven: 3.9.11. `mvn verify` completed successfully.
19 JUnit tests passed, no failures/errors:

- 4 calendar tests: Bucharest midnight, future-day locks, missed-day policies,
  claimed state, invalid days and year boundary.
- 5 SQLite tests: duplicate claims/actions, transaction rollback, nonnegative
  balances/overflow, reconnect/crash recovery, WAL-aware backup, season isolation
  and rejecting future schemas.
- 6 configuration tests: defaults, timezone, production-world-name rejection,
  resource-pack URL/hash policy, missing reward reference and database traversal.
- 4 capacity tests: full storage, combined partial stack capacity, unstackable
  rewards and invalid item amounts. These test the delivery capacity calculation;
  they are not a real client inventory test.

Artifact: `target/TSXMAS.v1.0.0.jar`.
SHA-256: `7deaeb5eae23fee9aa2275715def5c6d4c6c24fd65b7ddc661c7b2da49ddcfa4`.
Descriptor name/version/main class verified. Class-file version is 69 (Java 25).
SQLite driver is bundled; Paper and PlaceholderAPI implementation classes are
excluded. Generated JARs remain ignored and untracked.

## Clean runtime tests

The **same final artifact hash** was tested in separate localhost instances:

| Runtime | Result |
| --- | --- |
| Paper 1.21.11 build 132, Java 25 | Started, TSXMAS enabled, console diagnostics/validation/backup/reload completed, clean stop |
| Paper 26.3 build 251, Java 25 | Started, TSXMAS enabled, console diagnostics/validation/backup/reload completed, clean stop |

Machine-readable summaries: [1.21.11](startup-1.21.11.json),
[26.3](startup-26.3.json). Optional plugins were absent in these clean tests;
TSXMAS loaded without them. Defaults were disabled with unconfigured locations;
validation correctly reported the missing venue rather than importing a world.

The previous 42-JAR 26.3 test **failed**, with multiple plugin failures and a
server crash; see UPGRADE-26.3.md. Production configurations, BlueArcade modules,
credentials and player data were not copied. That original failure was resolved
by the later reconciled 47-plugin test linked above.

The final JAR was also tested with those 42 existing JARs on **1.21.11**.
TSXMAS enabled (including its optional PlaceholderAPI registration) and Paper
reached Done as the 180-second harness deadline expired. The harness did not
observe startup before its deadline and therefore did not execute its console
checks. Fresh-config warnings include missing economy/Discord credentials,
missing BlueArcade modules and Geyser NetherNet support. These need review on the
private server clone. This is partial evidence, not a full compatibility pass;
see [existing-1.21.11.json](existing-1.21.11.json).

## Asset and repository checks

- Christmas pack ZIP downloaded and SHA-1 verified against Modrinth metadata.
  Its 680-entry inventory and pack.mcmeta were inspected; visual/map compatibility
  remains untested. The later required-pack deployment updates the server
  properties and seasonal delivery policy; see RESOURCE-PACK.md.
- The initial map download returned HTTP 403. The owner later supplied the
  folder: 440 terrain chunks, 11 MCA files and the included datapack/resources
  were audited offline. A separate world copy and asset-only registry pack were
  placed over SFTP; all 67 active files passed SHA-256 readback. Existing world
  terrain was not overwritten. Later owner import and read-only migration
  verification are recorded in MAP-AUDIT.md and map-import.json.
- Map audit utility checked with synthetic safe/traversal ZIP fixtures; traversal
  was reported. Python utilities passed syntax compilation.
- `git diff --check` passed. Runtime JAR/database/backup/world paths and Python
  cache files are ignored. Index remains unchanged. Existing owner edits preserved.

## Explicitly unverified

No player connected during tests. Java/Bedrock GUI and marker interactions,
player permission denial, all command arguments, destructive confirmations,
real full-inventory/disconnect delivery, item/XP/Vault/cosmetics payouts,
NPC/hologram/music adapters, real protected build plots, Secret Santa escrow,
raffle fairness and client resource-pack behavior still require in-game tests.
Unit tests for queued disconnect/crash states do not replace those tests.

The owner confirmed a separate full-server backup. Later SFTP upgrade/map file
placements are recorded in the reports linked above. The owner executed the
actual map import. The old private combined ZIP remains unused. November 15's
required-pack deployment hosts a venue + current LIVE BetterModel ZIP and adds
the author's unchanged seasonal ZIP separately; see RESOURCE-PACK.md.

The hosted venue ZIP passed SHA-1/SHA-256 download readback, 10,562 JSON parses
and custom model/texture reference checks. All six installed TSXMAS YAML files
parsed, three targeted property-edit safety tests passed, and both active LIVE
configuration files passed SHA-256 readback after their backups were retained.
No new plugin JAR was built or deployed for this configuration-only change.
Client download/decline/rendering acceptance remains pending on owner startup.
