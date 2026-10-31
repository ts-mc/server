# WorldGuard — Region Protection Guide

WorldGuard protects regions on **TheySix Events** (`play.theysix.tech`),
including the six arena worlds (`PG-01` … `PG-06`).

## Files

| File | Purpose |
|------|---------|
| `config.yml` | WorldGuard settings (empty — full commented defaults live in `config_old.yml` / the jar) |

> `config.yml` is empty because the server runs on WorldGuard's defaults.
> WorldGuard's own `worlds/` folder is generated at runtime and is not committed.

## Region flags used here

| Flag | Value | Why |
|------|-------|-----|
| `build` | `deny` | protect arena builds from players |
| `pvp` | `allow` | combat games need PvP |
| `block-break` / `block-place` | `allow` | minigames like Spleef, Bridge Race |
| `entry` / `exit` | `allow` | players move between arenas |
| `invincible` | `deny` | games manage their own damage |

## Commands

| Command | Effect |
|---------|--------|
| `//wand` | get the selection wand (needs WorldEdit/FAWE) |
| `/rg create <id>` | create a region from your selection |
| `/rg addmember <id> <player>` | add a member |
| `/rg flag <id> <flag> <value>` | set a flag |
| `/rg info <id>` | inspect a region |
| `/rg list` | list regions |

## Notes

- Selection depends on FastAsyncWorldEdit — see
  [`../FastAsyncWorldEdit/README.md`](../FastAsyncWorldEdit/README.md).
- Restrict arena edits to staff by granting `worldguard.region.*` only to the
  staff track.
