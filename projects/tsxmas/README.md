# TSXMAS — TheySix Christmas

Independent Paper event plugin, Java 25, version 1.1.2. Source target is Paper
1.21.11; the standard Bukkit API is used for forward compatibility testing on
26.3. BlueArcade3 is neither a dependency nor a source of event rewards.

**Release status: staging candidate; player acceptance remains pending.** The
reconciled 47-plugin set passed the isolated Paper 26.3 startup test and was
deployed over SFTP at the owner's request; see
[upgrade deployment](../paper26-upgrade/DEPLOYMENT.md). The supplied WeihnachtsMC
map was audited offline and placed in a separate LIVE event-world folder with
an asset-only registry datapack. The owner's later LIVE log confirms successful
Multiverse import and clean shutdown. Paper migrated the terrain into
`world/dimensions/minecraft/tsxmas_christmas_2026/`; the November 15 log confirms
restart autoload and player entry. Texture rendering with the newly required
packs still needs a client check. See
[map audit and console checklist](docs/MAP-AUDIT.md) and
[operations](docs/OPERATIONS.md). Build and clean-server evidence is recorded
in [validation](docs/VALIDATION.md). No existing world terrain, player data
or permission groups were replaced. The owner later approved required Java
packs; their targeted configuration deployment is documented in
[RESOURCE-PACK.md](docs/RESOURCE-PACK.md). To turn the menus into configured
activities, follow [PLAYBOOK.md](docs/PLAYBOOK.md). The latest mailbox, NPC and
hologram fixes and startup commands are in [event repair](docs/EVENT-REPAIR-1.1.1.md).

## Build and install

Install Java 25 and Maven 3.9+, then:

```sh
cd projects/tsxmas
mvn clean verify
```

Output: `target/TSXMAS.v1.1.2.jar`, including SQLite JDBC. Paper and PlaceholderAPI
are compile-time dependencies and are not bundled. JARs, target output, database
files, test worlds and backups are ignored by Git. Semantic version changes
belong in `pom.xml`; the descriptor and JAR name derive from it.

After a reviewed full backup and staging acceptance, stop the destination
server, put exactly one TSXMAS JAR in `plugins/`, and start. Defaults generate
only inside `plugins/TSXMAS/`. Events start disabled and STOPPED. TSXMAS never
imports, generates, deletes or replaces a world. Do not use PlugMan or Paper's
global `/reload`.

## Configuration and event layout

All six configuration files have `schema-version: 1`. `/tsxmas reload` parses
all files before replacing the active configuration. A failed reload preserves
the old settings. Season, year and database filename require a restart. Reward
queue payloads are snapshots; editing rewards does not change a queued prize.

| File | Purpose |
| --- | --- |
| `config.yml` | Season, timezone, calendar, protection and pack policy |
| `messages.yml` | Main messages, help text and menu labels |
| `rewards.yml` | Named lists of reward actions |
| `events.yml` | Objectives, delivery stops, categories, raffle, prices |
| `locations.yml` | Venue, boundary, plots, points, NPCs, holograms |
| `database.yml` | Local SQLite filename |

Error details and some status labels are currently English code strings; they
are not all localized through messages.yml. Shipped coordinates are disabled
examples. The installed configuration has the supplied map's measured hub and
bounds; other event points still need an in-game survey.

| Map area | Location / feature |
| --- | --- |
| Village square | `hub`, calendar hologram and `/xmas calendar` |
| Workshop | `workshop`, Santa NPC, delivery start |
| Market street | `market`, merchant NPC and market GUI |
| Village houses | `secret`, `house-one`, `house-two`, `house-three` |
| Forest | `forest-present`, `candy-cane`, `snowman` |
| Grinch area | `grinch`, final challenge requiring hunt + deliveries |
| Building area | `build`, non-overlapping cuboid `plots` |
| Hidden staff area | `staff` |

Set the separate `world`, boundary bounds, and plot bounds in locations.yml.
Stand at each point and run `/xmas setlocation <name>`; `/xmas setspawn` records
only TSXMAS's `hub`. It does not claim `/spawn` or change a server/world spawn.
For block interaction points, use coordinates identifying the intended clicked
block. `/xmas hunt find <id>` and `/xmas delivery deliver <stop>` are equivalent
nearby interaction fallbacks, including for Bedrock.

Configure the hub/boundary and the points used by an activity. Run
`/tsxmas validate <event>`, then enable gameplay, reload and `/xmas start <event>`.
Stopped build/Grinch activities do not require their locations or plots to be
configured for other events. `/tsxmas validate` checks the common venue;
reloads also check any running/paused activities. Start/stop/pause/resume require confirmation. State
persists across restart; stopping does not delete progress. Pausing blocks
progress, purchases and claims, but already-earned reward delivery continues.
Delivery timers use wall-clock time, including pauses and restarts.

## Event leaderboards

`/xmas top` opens the board selector; `/xmas top <event>` and
`/xmas <event> top` show rankings for all eight activities. There are GUI, console,
PlaceholderAPI and optional live FancyHolograms views. Scores derive from existing
season progress; Secret Santa stays private until public reveal. No balances,
progress or reward entitlements are changed. See
[LEADERBOARDS.md](docs/LEADERBOARDS.md) for metrics, display placement, placeholders
and rollback. The Grinch point remains a location-based finale checkpoint;
its stone-brick platform is not made clickable or changed.

## Commands and permissions

`/xmas help` lists player commands; `/xmas staff` shows event states.

| Permission | Access |
| --- | --- |
| `tsxmas.player` (default true) | Menus, calendar, progress, rewards, purchases, gifts, votes |
| `tsxmas.staff` (default op) | Event transitions, location setup, moderation, inspection, reload, unlocks, hunt/delivery resets |
| `tsxmas.admin` (default op) | Admin root, token changes, manual rewards, raffle draw/refund, matching/reveal, general resets |
| `tsxmas.debug` (default op) | Debug diagnostics, additionally requires admin |

```text
lp group default permission set tsxmas.player true
lp group moderator permission set tsxmas.staff true
lp group admin permission set tsxmas.admin true
lp group admin permission set tsxmas.debug true
```

No existing permissions are edited automatically. Staff/admin inherit player
permission through plugin.yml. Destructive commands prompt for a sender-bound,
single-use confirmation valid for 30 seconds and recheck permissions on use.

Player features:

- `/xmas [menu|help|calendar|events|rewards|stats]`
- `/xmas calendar claim <day>`; staff: `preview`, `unlock <player> <day>`
- `/xmas hunt [progress|top|find <id>|reset <player>|reload]`
- `/xmas delivery [start|progress|deliver <stop>|reset <player>]`
- `/xmas build join [category]`, `submit`, `vote [player]`, `winners`, `status`
- `/xmas secret [join|gift|status]`; admin: `match`, `reveal`, `inspect <player>`, `reset <player>`
- `/xmas raffle [buy <amount>|status]`; admin: `draw`, `cancel`, `refund`
- `/xmas market [stock|buy <item>]`; staff: `restock <item>`, `reload`
- `/xmas grinch`, `/xmas pack [on|off]`, `/xmas music <on|off>`

Staff commands include `start|stop|pause|resume <event>`, `setspawn`,
`setlocation|setnpc|sethologram <name>`, `reload`, `inspect <player>`,
`broadcast <message>`. Admin commands include `give <player> <reward>`,
`tokens add|remove <player> <amount>`, `reset <player> <feature>`, and
`/tsxmas setup|validate|version|reload|backup|restore|migrate|debug|resolve|shutdown`.
`/tsxmas setup` reports setup errors; it never imports a map. Restore is an
offline procedure. `/tsxmas shutdown` disables this plugin, not the server.

## Event rules

Calendar uses December 1–25 of the configured year in Europe/Bucharest.
`LOCKED` allows only today's door; `LATE` allows earlier December doors;
`STAFF` requires manual unlock for missed doors. Future doors remain locked
even when staff unlocks them. Preview does not claim. UUIDs identify players,
including Floodgate players; usernames and the `BR_` prefix are never used as
database keys.

Hunt markers grant each discovery once per season, plus a completion prize.
Delivery starts near the workshop, tracks configured houses, and grants one
route completion per season. Resetting progress retains the reward ledger to
prevent duplicate rewards. A completed delivery route cannot be re-awarded by
reset. Use a new season to intentionally reset entitlements.

Build contestants reserve one plot and category. Submitting freezes their
plot. Staff uses `/xmas build approve|reject <player>`. First `build end` opens
voting; second ends voting and queues category winner/approved-participant
prizes. One vote per UUID per round, no self-votes. Ties use UUID ordering for
deterministic results. `build reset` starts a new round and retains old data;
it does not clear blocks or export schematics. Schematic export is a manual
FAWE operation described in the operations guide.

Secret Santa registers until its configured deadline, then admin `secret
match` makes a random cycle with no self-assignment. Recipients are visible
only to their giver and admins until reveal. Gift transfer accepts only a
plain, whitelisted held stack; containers and custom metadata are rejected.
The optional token budget is a fixed participation cost, not an appraisal of
item value. All gifts must be submitted before public reveal. Matches cannot
be reset after matching; repairing them needs a reviewed offline migration.

Raffle tickets cost tokens, with per-player caps. Drawing samples by ticket
weight without replacement; prizes follow the configured list, cycling if
needed. Winners are unique within a draw. Only undrawn raffles can be cancelled;
refunds return each player's recorded spend exactly once, including offline
players. The schedule triggers on the first running tick at/after the draw
time. Early drawing is an explicit admin action. This release supports one
raffle round per season.

Market stock, player limits and purchase history are persistent. Reload never
restocks. Token purchases are atomic with reward enqueue. Vault orders reserve
stock before external withdrawal; interrupted or rejected orders are held for
staff review rather than blindly refunded or charged again.

## Rewards and integrations

Reward actions support items, display names/lore, enchantments, model data,
tokens, XP, Vault money, temporary LuckPerms permissions, HeadDB heads,
UltraCosmetics command rewards, console/player commands, sounds, particles
and DiscordSRV messages. Optional command examples require verification against
the installed plugin version before assigning them to an event. `{uuid}` and
`{player}` are the only substitution variables. Player commands use normal
player permissions; they are never elevated.

SQLite claims and outbox enqueue are one transaction. Offline or full-inventory
rewards stay pending; nothing is dropped on the ground. Token delivery and its
acknowledgement are atomic. Items/XP/commands/economy cross external persistence
boundaries: a crash can make their result uncertain. Such actions become
`HELD`, never automatically replayed. Inspect the recipient inventory/economy
and `/xmas inspect <player>`, then use `/tsxmas resolve <id> done|retry` only
after deciding whether the effect occurred. This avoids claiming impossible
exactly-once guarantees for arbitrary console commands.

Optional Fancy NPCs/holograms are ephemeral and namespaced `tsxmas_`. Configure
their coordinates and `/tsxmas reload`; only TSXMAS-created objects are removed
on reload/shutdown. NPC callbacks retain player permissions and proximity /
WorldGuard checks. If an adapter is unavailable, normal commands remain usable.
These adapters require in-game verification on the actual selected plugin builds.

PlaceholderAPI: `%tsxmas_tokens%`, `%tsxmas_hunt_found%`,
`%tsxmas_delivery_done%`, `%tsxmas_calendar_claims%`, `%tsxmas_season%`,
`%tsxmas_state_hunt%` (other event names also supported). No TAB config is edited.

DiscordSRV uses an existing game-channel mapping, default `global`; enable
announcements in config.yml after configuring DiscordSRV privately. No token or
webhook is stored by TSXMAS. Public announcements omit private Secret Santa
matches until intentional reveal. Optional staff-only Discord notifications
are not implemented in this release.

For music, install a licensed NBS file in `plugins/TSXMAS/music/`, enable music,
and use `/xmas music on`. Playback stops on world change, logout, reload or
shutdown. Without NoteBlockAPI, gameplay continues.

## Tests and release acceptance

Run `mvn verify` for date/timezone policies, configuration validation, SQLite
creation, rollback, idempotence, season isolation, backup and crash recovery.
The isolated runtime harness is:

```sh
python3 tools/smoke-test.py --java /path/to/java25 --paper /path/to/paper.jar \
  --plugin target/TSXMAS.v1.1.2.jar --directory /tmp/new-tsxmas-test --accept-eula
```

Add `--existing-jars /path/to/plugins` for JAR-only compatibility testing.
The destination must not exist. No source configs, worlds or credentials are
copied. Runtime acceptance, actual map safety, every player command/permission,
full inventory, disconnects, Java/Bedrock GUI clicks and integration payouts
still require the manual checklist in [operations](docs/OPERATIONS.md).
