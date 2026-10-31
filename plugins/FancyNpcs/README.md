# FancyNpcs — NPC Guide

FancyNpcs adds server-side NPCs to **TheySix Events**
(`play.theysix.tech`) — for example lobby guides or join-NPCs.

## Files

| File | Purpose |
|------|---------|
| `config.yml` | FancyNpcs settings |
| `npcs.yml` | Saved NPC definitions |
| `languages/default.yml` | Plugin messages |
| `featureFlags.yml` | Feature toggles |
| `version.yml` | Data version metadata |

## Commands

| Command | Effect |
|---------|--------|
| `/npc create <name>` | create an NPC at your position |
| `/npc edit <id>` | open the NPC editor |
| `/npc remove <id>` | delete an NPC |
| `/npc list` | list NPCs |
| `/npc reload` | reload the config |

## Notes

- FancyNpcs is fully server-side: vanilla clients need no mod.
- Combine with FancyHolograms to add floating labels over NPCs — see
  [`../FancyHolograms/README.md`](../FancyHolograms/README.md).
- Brand text should use **TheySix Events** and `play.theysix.tech`.

## Vote leaderboard (daily / weekly / monthly)

`leaderboard_1`–`leaderboard_5` are the **monthly** set. Each rank also has
`_daily` and `_weekly` copies at the same spot. Linked holograms live in
`plugins/FancyHolograms/holograms.yml`.

Visibility is `PERMISSION_REQUIRED`. Clicking an NPC cycles the period via
MyCommand (`/votelb-daily`, `/votelb-weekly`, `/votelb-monthly`). Run
`/votelb-install` once, then `/votelb-ensure` is applied on join.

Place the five monthly NPCs first, then `/npc copy` each to
`leaderboard_<n>_daily` and `leaderboard_<n>_weekly` and `/npc move_here` (or
`move_to`) so all three periods share coordinates. Operators with `*` will see
every period stacked until those extra `see` nodes are denied.
