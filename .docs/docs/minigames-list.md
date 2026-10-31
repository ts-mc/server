# TheySix Events — Minigames List

This document lists every minigame available in the **TheySix Events** plugin
(formerly "Arcade by Blueva" / "Party Games"). Each game runs inside an arena
world (PG-01 … PG-06) and is selected by player voting at the start of a round.

> **Team formats:** team play is a property of the **arena**, not the game. Set
> `mode: team` in `arena.json` → `basic.mode` and the plugin assigns teams from
> `settings.yml` → `teams.manager.definitions`, up to `teams.manager.count`
> (currently 16 with all 16 colour definitions present). With `mode: party` a
> game runs as a free-for-all. Games are not restricted to a fixed 1v1/2v2/5v5
> shape — team size is whatever the arena's player count divides into.
>
> Every arena in this repo is currently configured `mode: party`, except that
> `run_from_the_beast` is a 1-vs-N **beast** game by design (its `Teams` column
> below reads `team` for that reason).

---

## 1. Movement / Parkour

| Game | ID | Mode | Time (s) | Teams | Description |
|------|----|------|----------|-------|-------------|
| **Race** | `race` | last standing | 120 | any | Sprint to the finish line. Avoid death blocks and obstacles. First to finish wins. |
| **Bridge Race** | `bridge_race` | last standing | 120 | any | Build a bridge of bricks to the finish line. First to cross wins. |
| **Fast Zone** | `fast_zone` | last standing | 120 | any | Sprint at high speed toward the goal. Dodge walls — one crash ends your run. |
| **TNT Run** | `tnt_run` | last standing | 300 | any | Run across layered sand + TNT floors. Each step collapses the ground. |
| **Traffic Light** | `traffic_light` | last standing | 120 | any | Advance on green, freeze on red. Reach the goal without moving on red. |
| **Minefield** | `minefield` | last standing | 120 | any | Dash to the safe zone. Pressure plates knock you back instead of killing you. |

## 2. Elimination / Combat

| Game | ID | Mode | Time (s) | Teams | Description |
|------|----|------|----------|-------|-------------|
| **All Against All** | `all_against_all` | last standing | 300 | any | Swords, bows and blocks. Build, break and outlast everyone. |
| **One In The Chamber** | `one_in_the_chamber` | most_kills | 120 | any | One bow, one arrow per life. One hit = one elimination. Keep a wooden sword for close range. |
| **Snowball Fight** | `snowball_fight` | most_kills | 120 | any | Shoot snowballs to eliminate opponents. |
| **Knockback** | `knockback` | most_kills | 120 | any | Use a knockback stick to send rivals into the void. |
| **Exploding Sheep** | `exploding_sheep` | last standing | 180 | any | Shear sheep before their visible fuse runs out, or the floor disappears. |
| **Run From The Beast** | `run_from_the_beast` | last standing | 300 | team | Runners loot chests and cooperate to defeat the beast. 1 beast vs N runners. |

## 3. Party / Rhythm

| Game | ID | Mode | Time (s) | Teams | Description |
|------|----|------|----------|-------|-------------|
| **Block Party** | `block_party` | last standing | 300 | any | Music plays, then a colour is revealed. Stand on the chosen block before the floor falls. Power-ups (BEACON) spawn. |
| **Chairs** | `chairs` | last standing | 600 | any | Musical chairs. Sit before the timer ends; no seat = elimination. |
| **Spleef** | `spleef` | last standing | 120 | any | Shovel the snow from under rivals. Last survivor wins. |
| **Splegg** | `splegg` | last standing | 180 | any | Shoot eggs to destroy the floor beneath opponents. |
| **Red Alert** | `red_alert` | last standing | 300 | any | `chaos`: tiles overheat randomly. `trail`: tiles heat up where players step. |
| **TNT Tag** | `tnt_tag` | last standing | 900 | any | Random players get TNT each round. Pass it before the fuse hits zero. |
| **Water Well** | `water_well` | last standing | 60 | any | Jump from above into the water well. Land in water for points. |

---

## Recommended arena setups
> **Reality check:** the table below is a *target* layout, **not** what ships in
> this repo. As shipped, all six arenas are identical: `min_players: 2`,
> `max_players: 24`, `rounds: 8`, `mode: party`, with all 19 games enabled. You
> have to actually apply these numbers in game with `/baa` (see
> `setup-guide.md` → *Arena wiring*).

| Format | Arena | min_players | max_players | rounds | games |
|--------|-------|-------------|-------------|--------|-------|
| 1v1 duels | PG-01 | 2 | 2 | 8 | one_in_the_chamber, snowball_fight, knockback, race, traffic_light |
| 2v2 | PG-02 | 4 | 4 | 8 | all_against_all, spleef, bridge_race, tnt_run |
| 5v5 | PG-03 | 10 | 10 | 8 | run_from_the_beast, block_party, chairs, red_alert |
| 5v5 chaos | PG-04 | 10 | 10 | 8 | tnt_tag, exploding_sheep, fast_zone, minefield |
| 10v10 | PG-05 | 20 | 20 | 8 | all_against_all, splegg, water_well, race |
| Free-for-all | PG-06 | 2 | 24 | 8 | all 19 games enabled |

**Watch the `max_players` vs. spawn list.** Each game file defines exactly **24**
spawns (`spawns.total: 24`). Setting `max_players` above 24 for any single game
leaves players without a spawn — keep `max_players ≤ 24` until you extend the
list.

Arena definitions live in `plugins/BlueArcade3/data/arenas/<id>/arena.json`.
Each arena folder also contains per-game configs in `games/<game_id>.json`
(bounds, spawn list, win mode, time limit).

> **Win mode is mostly implicit.** Only `all_against_all` (`last_standing`),
> `one_in_the_chamber`, `knockback` and `snowball_fight` (`most_kills`) set
> `basic.win_mode` in their game files. The other 15 games omit the key and fall
> back to the module default, so the `Mode` column below describes intended
> behaviour, not necessarily what the JSON enforces.

> **Time limits:** the `Time (s)` column matches `basic.time` in each game file
> in this repo.