# After the 26.3 SFTP upgrade

The server is intentionally left **stopped** after file activation. Use the
hosting panel's **Start** button; console commands cannot start a stopped server.
Java 25 was confirmed in the last LIVE startup log. Keep Java 25 selected.

Your existing off-server backup is the full rollback source. The deployment
also preserves replaced JARs under a timestamped `.deploy-backups/` directory;
that directory is a file rollback aid, not a world/player-data backup. Once
26.3 opens worlds, restore the matching full backup for a downgrade.

## 1. Console — after Start finishes

Run without a leading slash:

```text
maintenance on
version
plugins
version WorldGuard
version FastAsyncWorldEdit
version WorldServer
version TSStaff
version Essentials
version ProtocolLib
version packetevents
mv list
geyser version
tsxmas version
```

Expected key versions: Paper 26.3 build 251, WorldGuard 7.0.20, FAWE 2.16.2,
WorldServer 1.9.4-theysix.1, TSStaff 1.3.1, EssentialsX 2.22.1-dev+28,
ProtocolLib 5.5.0-SNAPSHOT and PacketEvents 2.14.0.

Check that all intended plugins enable and the existing worlds load. Multiverse
may migrate its configuration from v4 to v5; review the startup output. The
isolated tests did not contain the production worlds, so a missing production
world **on LIVE** needs investigation. Do not create replacement worlds or
invent a new `/spawn` destination to suppress an error.

If startup fails, leave maintenance enabled, stop cleanly and share the relevant
error with secrets/license/account identifiers removed. Do not run global
`reload`, PlugMan or destructive data-reset commands.

## 2. In game — one Java and one Bedrock test player

- Join Java at `play.theysix.tech`; join Bedrock at
  `bedrock.theysix.tech:25832`. Confirm skins, inventory and permissions.
- Open `/ts`, join a minigame and complete a round with both clients. Check
  arena loading/reset, inventories, built-in credits and XP. Current paid
  BlueArcade core/modules were retained; gameplay still needs this check.
- Run `/hide info status`; test the desired hiding behavior with a second
  viewer and restore your previous preference afterward. Check TAB formatting.
- Run `/npc list` and `/hologram list`. Inspect the existing vote leaderboard
  and click its NPCs to test period switching. Do not rerun leaderboard-install
  or reset existing votes just for verification.
- Check `/bal` in the relevant existing world(s). Confirm existing balances,
  permissions and cosmetic data survive the plugin migrations.
- Confirm moderation commands and the existing AdvancedBanZ/BanWebhook
  integration using your normal test procedure. Check DiscordSRV's startup
  connection, vote processing, HeadDB and UltraCosmetics.
- Refresh the server-list ping several times and check for the previous
  `status_response` EncoderException/NullPointerException. The fresh-settings
  localhost status test passed; the LIVE configuration must be checked too.

## 3. Christmas remains disabled until the venue exists

```text
tsxmas validate
```

Missing Christmas world/locations are expected until setup is completed.
TSXMAS keeps events and its resource pack disabled. The supplied map has been
audited and its files placed in the separate `tsxmas_christmas_2026` folder,
with the namespaced asset registry pack in the primary world's datapacks.
The owner's later log confirms the Multiverse import succeeded. Paper migrated
the map to `world/dimensions/minecraft/tsxmas_christmas_2026/`; the old top-level
folder is no longer the terrain location. Follow the next-startup and protection
steps in [MAP-AUDIT.md](../tsxmas/docs/MAP-AUDIT.md) before enabling any event.
Do not repeat the import.

To administer the later Christmas setup in game, grant the appropriate group:

```text
lp group owner permission set tsxmas.admin true
```

Optional grants, only if these are your intended staff groups:

```text
lp group admin permission set tsxmas.admin true
lp group moderator permission set tsxmas.staff true
```

Then `/xmas menu` checks the UI. Complete the map/resource-pack setup in
`projects/tsxmas/docs/OPERATIONS.md` before enabling any event.

## 4. Reopen when checks pass

Console:

```text
maintenance off
```

No routine database resets, permission reinstallation, `/spawn` changes, or
resource-pack replacement are required for this plugin upgrade. Existing LIVE
YAML, databases, worlds, Floodgate keys and unrelated assets were preserved.
