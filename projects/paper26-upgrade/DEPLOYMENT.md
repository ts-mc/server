# Local and LIVE SFTP deployment — 2026-11-14

Applied at the owner's explicit request, after they confirmed an existing
off-server server backup. No GitHub push, Git commit or staging was performed.

## Completed

- Read LIVE's plugin inventory, startup/shutdown log and targeted settings via
  the existing private SFTP profile. Connection details and secrets are omitted.
- Confirmed the latest LIVE lifecycle ended in completed shutdown; the last
  startup used Java 25 and Paper 1.21.11.
- Downloaded existing JARs and targeted settings into an ignored private
  comparison/rollback directory. No `.remote-sync` refresh or mirror was used.
- Found BetterModel 3.6.0 on LIVE, absent in the old local snapshot. Its hash
  matched the official stable 26.3-capable artifact; it was retained on LIVE
  and added to local configuration material's runtime set.
- Confirmed the paid/custom BlueArcade3, Geyser 2.12.0, TSHide and PlayerLogs
  matched the tested retained artifacts. No ambiguous same-name replacement
  or paid-core/module API mixing was performed.
- Ran the complete **47-plugin** set, all 19 existing BlueArcade modules and
  copies of world-manager configuration in an isolated Paper 26.3 test.
  Startup, console checks, clean shutdown and a real status ping passed.
- Applied Paper 26.3 build 251 and the reviewed plugin set locally. Preserved
  replaced local files under `.backups/paper26-local-20261114-210859/` and
  archived the retired root TSEvent JAR there. Existing world data was not opened.
- Uploaded **27 targeted files** into an inactive remote staging directory:
  Paper and 26 plugin JAR replacements/additions. No LIVE YAML patch was needed.
- Downloaded every staged file and verified its SHA-256 against the plan.
- Rechecked that LIVE was stopped and its plugin inventory had not changed.
  Activated using server-side renames, preserving replaced files under
  `.deploy-backups/paper26-20261114-210837/`.
- Verified the active remote plugin filename set: **47 JARs**, with superseded
  copies removed from the active folder. Local plugin identities are also 47
  unique names. Local activated JAR hashes match the SFTP-verified plan.

Machine-readable receipt: [deployment-result.json](deployment-result.json).

## Preserved

LIVE worlds, player data, databases, YAML settings, permissions, Floodgate keys,
Geyser/NetherNet configuration, BlueArcade modules/libraries, and intentional
assets were not uploaded or overwritten. The private comparison downloads and
runtime artifacts remain ignored. Existing owner edits in Geyser, TAB and the
sync helper were preserved.

The remote rollback directory contains replaced files, not a complete server
backup. After 26.3 opens real worlds, use the owner's full matching backup for
a downgrade rather than replacing only JARs.

## Next owner action

**The server remains stopped.** Start it through the hosting panel using Java 25,
then follow [AFTER-DEPLOY.md](AFTER-DEPLOY.md).

No actual LIVE startup or player session was performed after activation. The
successful compatibility/status test used an isolated server. The real worlds,
data migrations, Java/Bedrock gameplay and existing production integrations
still require the listed in-game checks.

TSXMAS is installed with disabled defaults for first startup. The WeihnachtsMC
map has not been imported, event locations are unset, and the seasonal pack is
not enabled. Existing production worlds and `/spawn` behavior remain untouched.
