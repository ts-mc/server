# TheySix Paper 26.3 plugin upgrade

**Current status:** the tested upgrade has been applied locally and activated
on the stopped LIVE server through SFTP at the owner's explicit request. Paper
and 26 plugin JARs were replaced/added; 47 plugin JARs are active. Worlds, databases,
LIVE YAML, keys and unrelated assets were preserved. Nothing was pushed to GitHub.
See [the deployment record](DEPLOYMENT.md) and [your console/in-game checklist](AFTER-DEPLOY.md).

The original isolated bundle and its test notes below describe preparation before
that direct deployment. BetterModel 3.6.0 was subsequently found on LIVE, confirmed
identical to the official stable release, and included in the final 47-plugin test.

The candidate contains 45 plugin JARs plus TSXMAS, the existing 19 BlueArcade3
modules, and their existing bundled libraries. 26 of the 45 plugin artifacts
were replaced/updated or added; 19 were retained. Some same-version official
downloads have different bytes from the previous local copy, so both hashes
are recorded. See [PLUGINS.md](PLUGINS.md).

## Test results

Paper 26.3 build 251, Temurin Java 25.0.5:

- Final set reached Done and stopped cleanly.
- No plugin-enable exceptions or Java exception classes in the final log.
- All 19 current BlueArcade3 modules loaded.
- WorldGuard 7.0.20 loaded with FAWE 2.16.2.
- WorldServer compatibility fork, TSStaff 1.3.1 and TSXMAS enabled.
- A real localhost status request returned `Paper 26.3`, protocol 777.
- Console TSXMAS version/validate/debug/backup/reload commands ran.
- Copies of existing WorldServer and Multiverse configuration were used in the
  final test. Real worlds/player databases and credential-bearing configs were
  deliberately not copied.

The final log has expected Multiverse errors for the nine registered production
world folders absent from the isolated test. LibsDisguises' optional BisectHosting
network check also failed. These are **not** an error-free production-clone test.
DiscordSRV has no bot token in staging; no real Discord messages were sent.
No players joined. Actual Java/Bedrock login, game rounds, NPC/hologram visibility,
moderation webhooks, voting, economy balances, cosmetic rewards and configuration/
database migrations still require private staging acceptance before LIVE.

The successful status probe uses fresh status-plugin settings and does not prove
that the pre-existing LIVE `status_response` error is resolved with LIVE settings.

## Important version decisions

- **Geyser 2.12.0 is unchanged**, as required. The existing local configuration
  already selects NetherNet; do not replace it with staging defaults. Preserve
  Floodgate authentication, private key/runtime identity and ItemJoin `BR_`.
- BlueArcade3 **3.4.5 custom paid build is retained**, with original modules.
  Its updater reports newer module builds requiring core API **3.5**. They were
  skipped rather than mixed with API 3.4. An owner-provided vendor release and
  review of the custom TheySix changes are required to update that paid core.
  Loading current modules does not certify gameplay on the new server.
- WorldGuard **7.0.20** is deliberate: 7.0.21 fails against FAWE 2.16.2 because it
  expects a `YAMLProcessor(Path,...)` constructor FAWE does not provide. Do not
  independently update one side of this tested pair.
- EssentialsX **2.22.1-dev+28-f8724e2** (official build 1833), ProtocolLib
  **5.5.0-SNAPSHOT-9cb125f**, and UltraCosmetics **3.17-DEV-b1** are development/beta
  compatibility builds. They are not presented as stable releases.
- FancyNpcs 2.12.1 works with the retained stable FancyHolograms 2.12.0 in the
  startup test. The unrelated FancyHolograms 3 alpha was not installed.
- TSStaff source was found locally and rebuilt as **1.3.1**, with automatic
  running-Minecraft-version selection for `/update`. TSHide 1.0.1 was retained;
  source was not found and its player-facing behavior remains untested.
- PlayerLogs 1.0 was obtained from the `yoferox` Modrinth project the user
  identified by name. It was absent locally and does not advertise 26.3; it
  loaded in staging. Confirm this is the exact PlayerLogs implementation used
  on LIVE before replacing a same-named plugin or reusing its data.
- WorldServer upstream only publishes 1.9.4. Its local compatibility fork is
  **1.9.4-theysix.1**, described below.
- EasyCommandBlocker's official 1.19.1 download returned HTTP 403. Its published
  Paper/core source was built as **1.19.1-theysix.1**, using the existing shaded
  YAML library. This is a **Paper-only source build**, not a Velocity/Waterfall
  binary. TSEvent and all excluded/retired plugins are absent from the bundle.

## What to upload

The prepared ZIP/folder is under `.backups/paper26-upgrade/`. It contains a
`server/` directory with Paper and the candidate plugin JARs, plus manifests,
hashes, old-filename removal instructions and source-build provenance. JARs,
ZIPs and runtime material remain ignored by Git.

**Replacing only Paper on LIVE does not apply these local plugin updates.**
The required operation is a stopped-server upgrade of Paper **and** the reviewed
plugin set, with the existing configuration/player data preserved.

1. Take a full private, off-server backup of worlds, player/plugin data, JARs,
   configs and keys. Test restoring that backup to a separate instance first.
2. Test this bundle against a private clone, with external messaging/webhooks
   disabled. Accept the player-facing and migration checks listed above.
3. Stop LIVE. Select **Java 25** in the hosting panel. Save the previous startup
   settings and verify the panel uses the intended server JAR filename.
4. Back up/remove only the **old plugin JARs being replaced**, using the removal
   list and plugin identities. An old JAR may have a different filename on LIVE.
   In particular, remove an existing TSStaff/PlayerLogs JAR before adding another
   copy of the same plugin. Never delete plugin directories/databases.
5. Upload the contents of `server/` into the server root. Do not upload the
   `server/` container itself as an extra directory. Do not re-enable TSEvent,
   EssentialsSpawn, EssentialsChat, AquaSpawn or ExcellentEconomy.
6. Preserve existing plugin YAML and runtime data. Review Multiverse 4→5,
   Maintenance 4→5 and HeadDB 5→6 migration output against backups. Change an old
   TSStaff `updates.minecraft-version: '1.21.11'` override to `auto` or `26.3`.
   Keep local forks pinned; review/disable any third-party automatic binary
   updater that could overwrite them. Do not copy test-generated configs.
7. Start in maintenance mode, check all plugins/worlds, then test Java/Bedrock,
   all minigame modules, WorldGuard, economy, moderation, votes, TAB/hide and
   status pings before admitting players. TSXMAS stays disabled until map setup.

Once 26.3 opens a production world, rollback requires restoring the **entire
matching pre-upgrade backup**. Putting the 1.21.11 JAR back is not a safe world
downgrade. No production world conversion or backup has been performed here.

## Reproducing source builds

TSStaff uses its tracked `plugins/TSStaff/build.sh` with Java 25 and a matching
Paper runtime libraries directory. Its runtime config remains ignored; only
sanitized defaults/examples were edited.

WorldServer's one-class GPL compatibility patch lives under `worldserver-compat/`.
The version enum understands both `1.21.11` and `26.3.build.251-stable`; five
parser cases passed. The compiled class and explicit plugin version are the
only changes to the original JAR. The build script refuses a different original
hash. Upstream source: WorldServer tag v1.9.4 at
`be5e0c6dbfd411a3913e9cc7caa7f6c18f756b20`; library dependency `c8976daa22`.

EasyCommandBlocker source revision:
`150df476094e503b956a5f126b9d00bba1756455`. `build-ecb-paper.py` compiles the
author's Paper/core classes and maps Simple-YAML imports to the existing shaded
library namespace. MIT attribution is included. Proxy classes/descriptors are
excluded, and the original JAR hash is checked.

`discover.py`, `github-metadata.py`, `prepare.py`, and `extra-candidates.py` record
public upstream metadata and verify published hashes where available. Jenkins
does not provide a published artifact digest in its response; the pinned job URL,
revision and computed SHA-256 are recorded. Metadata refresh is not deployment.

## Official sources

- [Paper 26.3 and irreversible-world-upgrade warning](https://papermc.io/news/26-3/)
- [FAWE](https://modrinth.com/plugin/fastasyncworldedit/versions), [WorldGuard](https://modrinth.com/plugin/worldguard/versions)
- [EssentialsX development builds](https://essentialsx.net/downloads.html)
- [ProtocolLib development release](https://github.com/dmulloy2/ProtocolLib/releases/tag/dev-build)
- [LibsDisguises release](https://github.com/libraryaddict/LibsDisguises/releases/tag/v26.11.10)
- [WorldServer source](https://github.com/Efnilite/WorldServer/tree/v1.9.4)
- [EasyCommandBlocker source](https://github.com/Ajneb97/EasyCommandBlocker)
- [PlayerLogs](https://modrinth.com/plugin/playerlogs)

Per-artifact source URLs and exact hashes are in the generated manifest.
