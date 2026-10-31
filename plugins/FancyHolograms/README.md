# FancyHolograms — Hologram Guide

FancyHolograms adds floating text holograms to **TheySix Events**
(`play.theysix.tech`) — for example arena signs, lobby welcomes and event
banners.

## Files

| File | Purpose |
|------|---------|
| `config.yml` | FancyHolograms settings |
| `holograms.yml` | Saved hologram definitions |
| `config_old.yml` | Previous config (kept for reference) |
| `featureFlags.yml` | Feature toggles |

## Commands

| Command | Effect |
|---------|--------|
| `/hologram create <name> <text>` | create a hologram at your position |
| `/hologram edit <name> addline <text>` | add a line |
| `/hologram edit <name> remove` | delete a hologram |
| `/hologram list` | list holograms |
| `/hologram reload` | reload the config |

## Notes

- Supports MiniMessage formatting — use the brand gradient
  `&#4CADD0` … `&#B2F9FF` for titles.
- The main server IP to show is `play.theysix.tech`; the display name is
  **TheySix Events**.
- FancyHolograms pairs with FancyNpcs — see
  [`../FancyNpcs/README.md`](../FancyNpcs/README.md).
- Vote leaderboard holograms (`leaderboard_*_hologram`, `_daily`, `_weekly`)
  use `linkedNpc` plus `PERMISSION_REQUIRED` so they toggle with the NPCs.
  Click the NPC to cycle Daily → Weekly → Monthly. Run `/votelb-install` once
  (MyCommand) so LuckPerms grants `fancyholograms.viewhologram.<name>`.
