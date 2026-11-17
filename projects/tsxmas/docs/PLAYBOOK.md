# Make the Christmas activities playable

TSXMAS 1.1.0 retains separate location validation for each activity. An unconfigured
build contest or Grinch checkpoint does not block other activities or reloads.
The owner's latest LIVE files already enable gameplay and record the hunt,
delivery, market, Secret Santa and Grinch points plus NPCs/holograms; those
positions are preserved by the update. Building remains unconfigured.
Menus are navigation; event states are saved in SQLite, so do not reset the database.

## 1. Set the venue points

Use an account with `tsxmas.admin` (OP already receives it). After starting LIVE:

```text
/mvtp tsxmas_christmas_2026
/tp -125 67 543
```

Stand at the actual place for each point and run its command. These commands
record your current position; do not paste them all while standing in the hub.
Keep every point inside X -288..63, Z 384..703 and Y -64..320, or deliberately
adjust the configured boundary after surveying the map.

| Stand here | Run in game |
| --- | --- |
| Santa's workshop starting point | `/xmas setlocation workshop` |
| Market entrance | `/xmas setlocation market` |
| Secret Santa meeting point | `/xmas setlocation secret` |
| Hidden present in the forest | `/xmas setlocation forest-present` |
| Candy-cane discovery | `/xmas setlocation candy-cane` |
| Snowman discovery | `/xmas setlocation snowman` |
| First delivery doorstep | `/xmas setlocation house-one` |
| Second delivery doorstep | `/xmas setlocation house-two` |
| Third delivery doorstep | `/xmas setlocation house-three` |
| Grinch checkpoint, only if using this activity | `/xmas setlocation grinch` |
| Build-contest entrance, only if plots are available | `/xmas setlocation build` |
| Optional hidden staff/testing area | `/xmas setlocation staff` |

In 1.0.1, `/tsxmas validate` checks the common world, boundary and hub.
`/tsxmas validate hunt` checks hunt points; `delivery` checks the workshop and
delivery stops; `market` / `secret` check their own points. Calendar and raffle
need only the common venue. Build validation alone requires its entrance and at
least one real plot, checking plot bounds/overlaps. The Grinch check needs only
its configured checkpoint plus the common venue. Stopped activities do not
block reloading another activity; running/paused activities keep their checks.
The GUI calendar also remains date-locked until December in Bucharest;
`/xmas calendar preview` is the staff preview, not a reward-claim bypass.

For discovery/delivery block clicks, locations must identify the clicked block,
not an unrelated air block above it. The proximity commands below work with
decorative displays/NPC markers and with protected blocks. An interaction radius
of four blocks is configured.

## 2. Protect the map, then add the visible entry points

While setting up, keep maintenance on. Set these flags for the Christmas world
only; leave lobby/survival and `/spawn` alone:

```text
/rg flag -w tsxmas_christmas_2026 __global__ passthrough deny
/rg flag -w tsxmas_christmas_2026 __global__ pvp deny
/rg flag -w tsxmas_christmas_2026 __global__ chest-access deny
/rg flag -w tsxmas_christmas_2026 __global__ tnt deny
/rg flag -w tsxmas_christmas_2026 __global__ creeper-explosion deny
/rg flag -w tsxmas_christmas_2026 __global__ other-explosion deny
/rg flag -w tsxmas_christmas_2026 __global__ fire-spread deny
/rg flag -w tsxmas_christmas_2026 __global__ lighter deny
```

Test interactions and protection with a non-OP account. Do not set the global
`build deny` flag: that prevents member plots from overriding it correctly.
TSXMAS adds its own map block/explosion protection only when `enabled` is true.
See OPERATIONS.md for contest regions and the remaining protection checks.

TSXMAS checks WorldGuard's `use` and `interact` flags at each target point,
including when using the proximity commands. If a denied flag blocks a marker,
select a small region around that marker/NPC, create a dedicated interaction
region and allow `use` / `interact` there while keeping containers protected:

```text
/rg flag <marker-region> use allow
/rg flag <marker-region> interact allow
/rg flag <marker-region> chest-access deny
```

Do not disable WorldGuard globally to make a quest work. Check overlapping
region priorities and test that the allowed interaction does not permit damage
to map decorations.

At Santa's intended standing position:

```text
/xmas setnpc santa
```

At the market merchant's standing position:

```text
/xmas setnpc market
```

At the calendar's floating text position (above the ground):

```text
/xmas sethologram calendar
```

At the token information display:

```text
/xmas sethologram tokens
```

NPCs/holograms refresh when enabled and reloaded. Santa's default click command
starts delivery; the merchant opens the shop. These integrations have command
fallbacks if the current Fancy API is unavailable. Check logs for
`Optional NPC unavailable` / `Optional hologram unavailable` rather than assuming
their spawn succeeded.

## 3. Enable gameplay and explicitly start activities

Run `/tsxmas validate` and `/tsxmas validate <event>` for the activity you want
to open. LIVE already has the top-level enabled flag set by the owner; preserve
it. On a new installation only, change the top-level `enabled: false` to true.
Leave resource-pack settings as configured by the pack deployment. Then run:

```text
/tsxmas reload
/tsxmas validate
/tsxmas validate hunt
/xmas start hunt
```

Each `/xmas start` prints a confirmation code. Immediately execute
`/xmas confirm <that-code>` as the same sender. Do this separately for each
activity you want running. Check `/xmas staff` first; do not start an activity
that is already RUNNING:

```text
/xmas start delivery
/xmas start market
/xmas start secret
/xmas start raffle
/xmas start grinch
/xmas start calendar
```

Confirm each command before requesting the next one; requesting a new action
replaces that sender's pending confirmation. `/xmas staff` lists persisted
states. `/xmas pause <event>`, `/xmas resume <event>` and `/xmas stop <event>`
also require their own confirmation. They do not erase player progress.

Do not start the build contest until plots and WorldGuard membership are ready.
The map's original WeihnachtsMC plugin is absent by design: its animated rides,
character scripts and original quests do not become active by starting TSXMAS.
The Grinch activity currently checks completed hunt/delivery progress and grants
a configured reward at its location; it is not a combat boss.

## 4. What players actually do

`/xmas top` opens leaderboards for every activity, also available with
`/xmas <event> top`. Optional floating boards use
`/xmas sethologram leaderboard-<event>` followed by reload, at a position chosen
by staff. See LEADERBOARDS.md for metrics, privacy and PlaceholderAPI values.

| Activity | Play flow |
| --- | --- |
| Scavenger hunt | Reach each recorded discovery, then `/xmas hunt find forest-present`, `/xmas hunt find candy-cane`, `/xmas hunt find snowman`. Correctly configured block clicks also work. `/xmas hunt progress` and `/xmas hunt top` show progress. |
| Santa's delivery | At the workshop, click Santa or `/xmas delivery start`. Visit the three doorsteps and use `/xmas delivery deliver house-one`, then the other stop IDs. `/xmas delivery progress` tracks the route. |
| Market | `/xmas market`, buy with tokens earned from discoveries/deliveries. Default goods cost 10/25 tokens. |
| Advent | `/xmas calendar`, choose an available December door. Each day is claimed once per UUID. |
| Secret Santa | `/xmas secret join` before the configured December 15 deadline. Staff run `/xmas secret match` and confirm after registration. Hold an allowed gift item and `/xmas secret gift`, then confirm. Default allowed gifts: diamond, emerald, cookie or gold ingot, 1–64 items. |
| Raffle | `/xmas raffle buy <amount>` spends 5 tokens per ticket, maximum 100 per player. Drawing is configured for December 25 at 20:00 Bucharest; staff can draw, cancel or refund with the corresponding confirmed command. |
| Grinch finale | Finish the hunt and a delivery route, travel to the Grinch point, then `/xmas grinch`. This grants the configured finale reward once. |
| Pending rewards | `/xmas rewards` attempts delivery of queued rewards; full inventories retain item rewards for later delivery. |

Have a Java player and a Floodgate player complete a discovery, a delivery and
an item reward before public opening. Required Java ZIPs are not native Bedrock
packs; the model visuals require a separate Bedrock conversion. Keep the command
fallbacks and ordinary English text usable on Bedrock.

## 5. Build contest needs its own preparation

The two default plots are disabled examples. Select a genuinely empty build area
in game and set disjoint plot cuboids under `plots` in locations.yml; each needs
`configured: true` and valid `min-x/min-y/min-z/max-x/max-y/max-z` within the
venue boundary. Create matching WorldGuard plot regions. TSXMAS assigns a plot
in its database but does **not** automatically add the builder as a WorldGuard
member; staff must add each assigned builder with
`/rg addmember <plot-region> <player>` in this world (or configure a reviewed equivalent permission policy).

Then reload, validate and `/xmas start build`, confirming the command. Players
use `/xmas build join <category>` and `/xmas build submit`. Staff approve entries
with `/xmas build approve <player>`. `/xmas build end` (confirmed) changes the
registration phase to voting; running it again after voting selects winners and
queues prizes. `/xmas build winners` displays results. Do not reset a contest or
clear terrain until submissions/rewards and any exports have been preserved.

## 6. Acceptance before reopening

- Confirm both Java pack downloads succeed; declining a required pack should
  prevent Java login. Rejoin after changing a server-list pack preference.
- Verify lamp posts, furniture, trees, paintings and workshop decorations.
- Confirm hunt/delivery reward once-only behavior with a non-OP player, and
  rejoin with a full inventory to check queued item delivery.
- Check region boundaries, private plot membership and protection of map displays.
- Run `/xmas staff`, `/xmas stats`, `/tsxmas validate`; review the latest log.
- Keep events without tested locations/protection stopped. Only then turn
  maintenance off.
