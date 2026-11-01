# Halloween Event Staff Guide

This guide covers staff observation, handling suspected cheating, and helping
players join and understand Halloween Event 1.2.6 on Paper 1.21.11. The event
supports Java and Bedrock clients. Players do not install Fabric or the supplied
Fabric mods.

## Quick facts for staff

- The event is underground zombie survival. Contestants loot, build temporary
  barricades, buy perks with event points, and try to outlast the other players.
- **There is no PvP.** First, second, and third place are based on survival time,
  not zombie kills or points. A high kill or point count is not evidence of
  cheating.
- The first two minutes are a zombie-free looting period. Instructions are
  shown automatically in the first 30 seconds. Chest refills happen every
  minute; zombie stages also advance every minute, on a separate clock.
- Stage 30 starts at 31:00 and continues. It does not automatically end the
  match.
- Contestants start with a stone sword, four bread, 32 cobblestone, and the
  Perks Shop Nether Star in hotbar slot 9. Imported inventory is unavailable;
  event supplies are not carried back to the main server.
- A required event resource pack must load. Bedrock players use the Bedrock pack
  flow and a simpler apparition effect than Java players.

## Staff access and controls

Staff observation does not count as a contestant. Use the command in-game:

```text
/event staff join
```

If `/event` conflicts with another plugin, use its explicit fallback:

```text
/tsevent:event staff join
```

Staff observers use Adventure-mode item controls. Crouch to return to those
controls if you entered native free Spectator mode; native free Spectator mode
cannot click elevator buttons. Staff can ride the event elevator using the
observer controls.

Administrators can start a normal match with at least two contestants, or use
the following match controls:

| Command | Use |
|---|---|
| `/event start` | Start a ranked match; requires at least two contestants. |
| `/event forcestart` | Start an unranked solo-capable administrator test. |
| `/event forcestart <player>` | Include a named player in an unranked test. |
| `/event forcestart <admin> creative` | Creative test; target must be an event administrator. |
| `/event observe` | Enter staff observation; an admin contesting a forced test switches to Spectator. |
| `/event stop` | Abort a running match. |
| `/event prepare` | Rebuild/prepare the stopped arena from its template. |
| `/event reset` | Reset a stopped arena. |
| `/event diagnose` | Write diagnostics and show event/performance metrics. |
| `/event skipstage` | Advance zombie difficulty only; does not advance survival/refill/blackout clocks. |

`/event blackout 1`, `/event blackout 5`, `/event scare null`, and
`/event scare 67` are restricted preview controls for administrator forced
tests. Apparitions need clear space ahead. Do not use test controls during a
normal hosted match. The `/event test ...` development checks are disabled during
normal launch.

Commands needing a position, held item, or camera must be run in-game. In the
server console, omit the leading `/`.

### Permission boundaries

- `tsevent.play`: join as a contestant.
- `tsevent.staff`: join as an uncounted observer.
- `tsevent.admin`: administration and restricted testing; includes staff
  observation and stage skipping.
- `tsevent.skipstage`: advance zombie difficulty.
- `tsevent.staff` alone does not permit starting, stopping, or skipping stages.

The configured ranks are expected to give match administration to owner,
manager, and admin; observation to owner, manager, admin, headmoderator, helper,
moderator, and staffteam; and contestant joining to all existing groups.

## How to guide players into the event

Tell players they can click the lobby entrance NPC or run:

```text
/event join
```

If another plugin claims `/event`, have them use:

```text
/tsevent:event join
```

The command requests or loads the event resource pack and sends the player to
the protected waiting lobby. If the pack prompt appears, they must accept it to
use the required event assets. The main server lobby remains separate from the
event lobby. The event NPC opens the event menu.

For connection help, provide only the public addresses:

- Java: `play.theysix.tech`
- Bedrock: `bedrock.theysix.tech`, port `25832`
- Event pack URL (for troubleshooting):
  `http://212.192.28.194:25665/halloween.zip`

Java authentication remains enabled. Floodgate allows Bedrock players without
a Java purchase, but Bedrock still requires Microsoft/Xbox sign-in.

### Short player briefing

Staff can paste or read this before a match:

> Join with `/event join` or the lobby NPC, then accept the required resource
> pack. When the administrator starts the match, there is a 30-second countdown
> and then two minutes to loot before zombies begin. You start with a stone
> sword, four bread, 32 cobblestone, and the Perks Shop star in hotbar slot 9.
> Right-click the star or use `/event shop` to spend points earned from zombie
> kills. There is no PvP: placement is based on how long you survive. Your event
> inventory is separate and restored/removed when the match ends.

Remind players that lobby protection prevents damage, hunger loss, PvP, building,
and breaking. If someone misses a prompt or needs a rules recap, they can use
`/event howtoplay`, `/event menu`, `/event status`, or `/event pack`.

## Explain the match and controls

1. Players join the waiting lobby. The administrator runs `/event start` once
   at least two contestants have joined.
2. A 30-second countdown runs. Contestants deploy to randomized validated
   positions around the underground bunker.
3. The first 30 seconds show automatic instruction cards. The remaining 90
   seconds of the two-minute grace period are for uninterrupted looting.
4. Zombie stages begin at 2:00. Chest refills run on their own 60-second clock.
   The first refill is at 1:00; the first blackout is scheduled at 3:00.
5. Each killed zombie reserves its replacement slot for 20 seconds. Zombie
   targets may be under budget because safe spawn positions, crowding, or server
   performance can limit spawning.
6. Last contestant alive wins. Earlier eliminations determine second and third;
   simultaneous final deaths can draw. Leaving or disconnecting eliminates a
   contestant.

Contestants can use `/event leaderboard` or `/event stats` for the paginated
leaderboard. Zombie kills and event points are displayed on the sidebar but do
not decide placement. `/event sounds` toggles extra event audio.

### Perks Shop basics

The locked Nether Star named **Perks Shop** is in hotbar slot 9. Right-click it
or use `/event shop`, `/event skills`, or `/event perks`. Java opens an inventory
menu; Bedrock opens a button form. Only living contestants can buy; staff and
eliminated observers cannot. Points and upgrades reset between matches.

Players earn `4 + 2 × current stage` points per credited zombie kill. Before an
item enchantment purchase, select/hold the item, then open the shop. Protection
upgrades the lowest-enchanted equipped armor piece. Purchases check balance,
item, and purchase limits when clicked; a full inventory rejects a consumable
purchase without charging points. Enchanted golden apples are shop-only and
limited to one per contestant each round.

Second Chance is a one-use lethal-hit roll with a 50% survival chance. It is not
a resurrection after elimination and does not prevent elimination for leaving
or disconnecting.

### Eliminated contestants and observers

When a contestant dies during a running match, they automatically enter observer
mode. Ordinary living contestants cannot use spectating to escape elimination.
Observers cannot loot, fight, alter the map, or attract zombies. They can use:

| Hotbar slot | Control |
|---|---|
| 1 | Previous Player |
| 5 | Free Spectate |
| 8 | Leave Event |
| 9 | Next Player |

Right-click Previous/Next to use the native spectator camera. Crouch to return
from free Spectator mode to item controls. Observers can leave with `/event
leave`.

## Handling suspected cheating

The event is PvE. Do not accuse or punish someone based on zombie kills, points,
survival placement, or a single surprising moment. Strong loot, perks, stage
scaling, barricades, elevators, spectator controls, and Second Chance are all
intended mechanics. The event also has randomized deployments and safe-spawn
limits, so the same round can look different for each player.

When something looks suspicious:

1. **Observe first.** Join as staff with `/event staff join`. Follow the player
   and note the match time, location, stage, their actions, and what mechanic
   appears to be bypassed. Avoid disrupting contestants unless immediate harm
   or active exploit abuse requires intervention.
2. **Check event rules and context.** Confirm the player is alive or an observer,
   identify their game mode, and consider legitimate causes such as an item,
   perk, resource-pack visual, lag, or event transition. Remember the map is
   protected: players may only break blocks placed by contestants in that round.
3. **Preserve evidence.** Save a clip or screenshot where possible. Record the
   player name, approximate timestamp and timezone, match/stage, location, exact
   behavior, witnesses, and any relevant command or plugin log excerpt. Keep
   original evidence unchanged and share it only with staff who need it.
4. **Use PlayerLogs as supporting context.** The available PlayerLogs plugin
   metadata describes per-player chat, command, join, and quit logging. It does
   not claim to record movement, combat, inventory, block changes, or cheats.
   Use its records to establish what was said or which commands were run around
   the incident; do not treat absence of a PlayerLogs entry as proof that
   nothing happened. Do not assume commands or configuration options that are
   not documented by the installed plugin.
5. **Escalate proportionately.** Share the evidence with the event
   administrator/moderation team. For a confirmed server-rule violation, use
   the established AdvancedBanZ moderation process and follow the current
   evidence standard and punishment guidance. BanWebhook is retained for its
   punishment webhook integration; verify that integration at deployment.
   Staff observer permission alone does not grant punishment or event-admin
   access.
6. **Document the outcome.** Include the evidence reviewed, decision, staff
   involved, and any follow-up. Keep accusations and private evidence out of
   public chat; tell reporters to use the designated report channel.

If evidence is inconclusive, continue observing or request a clip. Do not promise
a punishment or publicly label a player a cheater before review. A player report
should include the event, approximate time and timezone, what happened, and a
screenshot or video if available.

## PlayerLogs configuration status

The repository does not contain a PlayerLogs config file or an authoritative
configuration reference. The available remote snapshot contains
`plugins/playerlogs.jar`; its `plugin.yml` describes the plugin as logging chat,
commands, and join/quit events per player. There is no verified configuration
schema in the files available for this guide, so no guessed YAML settings have
been added. Keep the plugin's generated per-player logs as runtime data and out
of Git. If configuration is needed, inspect the plugin's own documentation or
its generated default config on the server before editing it.

## Quick reference

| Need | Command |
|---|---|
| Player joins | `/event join` |
| Player rules | `/event howtoplay` |
| Player event status | `/event status` |
| Player leaderboard | `/event leaderboard` |
| Player shop | `/event shop` |
| Player sound toggle | `/event sounds` |
| Staff observer | `/event staff join` |
| Start normal match | `/event start` |
| Stop match | `/event stop` |
| Diagnostics | `/event diagnose` |

Use `/tsevent:event ...` if another plugin owns `/event`.
