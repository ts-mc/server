# TSXMAS 1.0.1 and Christmas display update

The owner stopped LIVE and requested the build-contest setup blocker be fixed,
plus this seasonal gradient:

`#B3000C → #E40010 → #D8D8D8 → #1FD537 → #00B32C`

## Activity validation fix

The previous plugin checked every point before starting any activity or
reloading enabled gameplay. A missing build entrance therefore blocked the
already configured hunt, delivery and market. Version 1.0.1 separates common
venue checks from each activity's requirements:

| Command | Required venue points |
| --- | --- |
| `/tsxmas validate` | Loaded event world, boundary, hub |
| `/tsxmas validate calendar` or `raffle` | Common venue |
| `/tsxmas validate hunt` | Common venue + configured objective locations |
| `/tsxmas validate delivery` | Common venue + workshop + delivery stops |
| `/tsxmas validate market` or `secret` | Common venue + that activity's point |
| `/tsxmas validate grinch` | Common venue + configured Grinch checkpoint |
| `/tsxmas validate build` | Common venue + build entrance + at least one valid, non-overlapping plot |

Starting/resuming validates the requested activity. Reload validates common
setup and any already running/paused activities; stopped activities do not block
it. Build and Grinch remain available, with no automatic event-state changes.
No database migration/reset is required. The default private staff point is not
a required player-event location.

The owner already recorded most points/NPCs/holograms and set gameplay enabled
on LIVE. These files differ intentionally from LOCAL and were not blindly
copied over either state. All coordinates, enabled/music choices, pack settings,
quests, progress and player data are preserved.

## Seasonal display edits

- TAB header/footer, server titles, web/vote animations and rank badges use the
  requested five-stop gradient. Generated legacy RGB codes keep existing text
  and placeholders; no new placeholder expansion is needed.
- TAB keeps native LuckPerms prefixes, sorting and permission behavior. Rank
  badge names are preserved; LuckPerms data/metadata are not edited.
- BlueArcade's existing server prefix uses the five-stop MiniMessage gradient.
  Its name, games, credits/XP and routing are unchanged.
- TSStaff's existing prefix label uses native `&x` RGB codes supported by its
  existing formatter; no TSStaff JAR replacement is needed for the colors.
- TSXMAS's owner-selected `SERVER` prefix uses the same gradient. Version 1.0.1
  renders configured prefix markup and legacy hex correctly while keeping all
  player-supplied message content literal. LOCAL's different prefix label is
  preserved and recolored separately.
- LPC remains the chat formatter, preserving its existing `{prefix}` rank,
  player-name and message format after a seasonal `SERVER` prefix. No chat
  permissions, group formats or rank metadata are changed.

Seven display configurations and exactly one TSXMAS JAR are deployed. The old
JAR/configurations are retained in the remote backup path recorded in
`christmas-1.0.1-deployment.json`. Runtime JARs/backups stay ignored by Git.

## Verification and startup

Java 25 compilation and all **29 unit tests** passed, including activity-specific
location requirements, missing build plots, out-of-bound plots, custom objective
names and prefix markup/legacy-color compatibility. All seven YAML semantic
diffs were compared to the intended scalar paths; rank labels, conditions,
permission metadata, sorting and non-display settings are preserved.

The deployment tool refuses to activate against a running log or changed LIVE
file hashes. It stages and reads back all eight files before activation, backs
up old files, then reads active files back for SHA-256 verification. Only
`TSXMAS.v1.0.1.jar` should remain active. No Paper server was started by these
tools; visual rendering and in-game acceptance remain owner-startup checks.

After starting normally, run in console:

```text
tsxmas version
tsxmas validate
tsxmas validate hunt
tsxmas validate delivery
tsxmas validate market
xmas staff
```

For each desired activity shown STOPPED:

```text
xmas start hunt
xmas confirm <code printed by that command>
```

Repeat separately for delivery, market, calendar, secret or raffle as desired.
Each confirmation code belongs to the same sender and expires in 30 seconds.
Leave build stopped until actual plots are ready. There is no need to set a
dummy build location. Advent claims still follow December dates in Bucharest.

In game, check TAB/chat/plugin prefixes, `/xmas menu`, a hunt discovery, a
delivery and its queued reward using a non-OP account. See PLAYBOOK.md for the
full activity/protection steps. The previously required resource packs remain
configured; their assets and hashes are unaffected by this update.

To roll back, stop LIVE, preserve current files privately, move the new TSXMAS
JAR out of the active plugins folder and restore the matching old JAR plus the
seven configs from the recorded backup. Do not restore/delete world or player
databases for this display/plugin-code rollback.
