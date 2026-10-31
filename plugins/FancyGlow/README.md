# FancyGlow — Glow Colour Guide

FancyGlow lets players apply a glowing outline colour to their name on
**TheySix Events** (`play.theysix.tech`).

## Files

| File | Purpose |
|------|---------|
| `config.yml` | FancyGlow settings (**empty = defaults**) |

## How it is used here

- TAB's tab-suffix uses `%fancyglow_color%` to show the player's chosen glow.
- The scoreboard has a `ɢʟᴏᴡ` line showing `%fancyglow_color%`.

## Commands

| Command | Effect |
|---------|--------|
| `/glow` | open the glow colour selector |
| `/glow <color>` | set your glow colour |
| `/glow off` | remove your glow |

## Notes

- Permission nodes are `fancyglow.*` (e.g. `fancyglow.color.red`).
- Colours offered depend on the permissions you grant; colour the tab entry by
  adding the matching `%fancyglow_color%` placeholder.
