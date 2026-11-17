# Event leaderboards — TSXMAS 1.1.0

All eight activities have a ranking GUI, player/console commands, PlaceholderAPI
values and optional live FancyHolograms displays. Rankings derive from existing
committed season records; the upgrade does not reset progress, alter balances,
replay rewards or migrate the SQLite schema. The activity-specific validation
fix from 1.0.1 and required-pack/Christmas theme settings are retained.

## Open a board

```text
/xmas top
/xmas top hunt
/xmas hunt top
/xmas delivery top
/xmas calendar top
/xmas build top
/xmas secret top
/xmas raffle top
/xmas market top
/xmas grinch top
```

`/xmas leaderboard <event>` and `/xmas leaderboards <event>` are aliases.
The main `/xmas menu` has a Leaderboards button. Players receive a GUI; console
receives text rows. Existing `tsxmas.player` permission applies. Reading a board
does not require starting an event or configuring a build plot.

| Activity | Ranked score |
| --- | --- |
| Calendar | Claimed December doors |
| Hunt | Distinct saved discoveries |
| Delivery | Distinct delivery stops completed |
| Build | Votes for staff-approved entries in the current contest round; approved entries with zero votes are shown |
| Secret Santa | Gifts submitted, shown only after staff's public reveal |
| Raffle | Tickets in the current round; cancelled rounds show no entries |
| Market | Committed paid purchases; held/unpaid reservations do not count |
| Grinch | Finale completions, once per player |

Equal scores share a competition rank (for example 1, 1, 3). UUID ordering makes
ties stable; it does not claim one tied player performed better. The default
list is the first ten entries. Ranking does not grant extra automatic rewards.
No completion times are invented for historical data that did not record them.

Secret Santa's names/counts stay hidden until its phase is REVEALED, including
through cached results, GUIs, holograms and placeholders. Leaderboards never
query or display giver/recipient assignments or gift contents.

## Place floating leaderboards

The upgrade adds eight **unconfigured** display definitions under `holograms`
in locations.yml. Existing NPC/hologram coordinates and text are preserved.
There are no guessed placement coordinates. Fly to the intended floating-text
position in the Christmas world and run the corresponding staff command:

```text
/xmas sethologram leaderboard-calendar
/xmas sethologram leaderboard-hunt
/xmas sethologram leaderboard-delivery
/xmas sethologram leaderboard-build
/xmas sethologram leaderboard-secret
/xmas sethologram leaderboard-raffle
/xmas sethologram leaderboard-market
/xmas sethologram leaderboard-grinch
```

Run each command at its own chosen location, then `/tsxmas reload`. A central
leaderboard area is fine; a board does not have to stand at its activity's
checkpoint. Do not put the hunt board above a hidden discovery unless you mean
to reveal that hiding place.

These displays carry a `leaderboard: <event>` field and generate their lines
from the board data. Custom display names can use that field too. Set
`configured: false` and reload to remove a TSXMAS-owned display. Other Fancy
NPCs/holograms, including the voting leaderboard, are not edited or removed.

Text refresh updates the existing hologram rather than respawning all NPCs.
If FancyHolograms is missing/incompatible, ranking commands, GUIs and optional
PlaceholderAPI values continue to work. These use ordinary items and text;
they do not require new custom assets for Java or Floodgate players.

## PlaceholderAPI and configuration

For event IDs `calendar`, `hunt`, `delivery`, `build`, `secret`, `raffle`,
`market`, `grinch`, and positions 1 through the configured list size:

```text
%tsxmas_top_hunt_1_name%
%tsxmas_top_hunt_1_score%
%tsxmas_top_hunt_1_rank%
```

Replace `hunt` and `1` as needed. Missing/private entries return `-` for names
and ranks and `0` for scores. They work without a viewer for global holograms.
No additional expansion download is needed beyond TSXMAS's existing optional
PlaceholderAPI integration.

In config.yml:

```yaml
leaderboards:
  limit: 10
  refresh-seconds: 10
```

The limit is 1–28; refresh seconds is 1–300. Titles, metric labels, row text,
private/empty messages and the Christmas hologram heading are in messages.yml.
Rankings use a bounded cache; reload invalidates it. Public reveal/cancellation
and contest-round changes are checked before cached entries can be returned.
Server-thread display updates refresh the ranking data at the configured rate.

Admin progress resets change the corresponding current-progress board after
refresh; the original reward ledger remains protected. Starting a new contest
round changes its board without deleting archived submissions or winners.

## Grinch platform clarification

The saved Grinch point on the village stone-brick platform is preserved.
The block is not made clickable, replaced or given a special block type.
Players complete the hunt and Santa's delivery route, visit the checkpoint
within the configured radius and use `/xmas grinch` (or the Grinch menu button).
The event must be RUNNING and the region's interactions must permit the action.
The configured finale reward is granted once per player; undelivered rewards
stay in the existing queue. It is currently a finale checkpoint, not a combat
boss. The Grinch leaderboard lists those completions with shared ranks.

## Validation, deployment and rollback

The Java 25 build passed all **41 Java unit tests**, plus **five configuration
patcher tests**. Evidence includes season isolation, duplicate progress,
shared ranks, all eight score definitions, current build/raffle rounds,
unapproved builds, held purchases, cached Secret Santa privacy and read-only
balance/reward behavior. Five configuration-patcher tests cover preserving
owner values/positions, line endings, existing help, flow mappings and repeat
application. The installed FancyHolograms 2.12 API methods were inspected locally.

The SFTP deployment receipt is `leaderboards-1.1.0-deployment.json`. The tool
compares current LIVE hashes, refuses activation against a running log,
backs up the old JAR and changed configs, then verifies staged/active hashes.
It installs exactly one active `TSXMAS.v1.1.0.jar`. It does not start Java or
touch worlds, databases, permissions, the resource-pack policy or other plugins.

After starting LIVE, check `/tsxmas version`, `/xmas top`, each board and an
ordinary discovery/delivery. Place one leaderboard hologram, reload, then verify
that it updates after a player earns progress. Check the Secret Santa board
remains private before reveal. Java/Bedrock GUI/rendering and optional adapter
acceptance require real client tests; unit tests are not that evidence.

Rollback: stop LIVE, preserve current files, move the new JAR out of plugins
and restore the prior JAR plus the backed-up configs from the receipt's backup
directory. No database rollback is needed for this release; keep the database
and event progress intact.
