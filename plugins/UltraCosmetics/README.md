# UltraCosmetics — Cosmetics Guide

UltraCosmetics provides cosmetic items on **TheySix Events**
(`play.theysix.tech`): hats, gadgets, pets, particle effects, morphs and
balloons.

## Files

| File | Purpose |
|------|---------|
| `config.yml` | UltraCosmetics settings (**empty = defaults**) |
| `custom_cosmetics.yml` | Custom cosmetic definitions (**empty = defaults**) |
| `data/<uuid>.yml` | Per-player owned cosmetics — do not hand-edit |

## Cosmetics categories

| Category | Example |
|----------|---------|
| Hats | wearable head items |
| Gadgets | right-click effects |
| Pets | following mobs |
| Particles | aura effects |
| Morphs | transform into a mob |
| Balloons | floating balloons |
| Emotes | cosmetic animations |

## Commands

| Command | Effect |
|---------|--------|
| `/cosmetics` | open the cosmetics menu |
| `/cosmetics <category>` | open a specific category |
| `/uc give <player> <cosmetic>` | give a cosmetic (staff) |
| `/uc reload` | reload the config |

## Notes

- The shipped jar is `UltraCosmetics-3.16-RELEASE.jar`.
- Purchasable cosmetics are surfaced through DeluxeMenus — see
  [`../DeluxeMenus/README.md`](../DeluxeMenus/README.md).
- Permission nodes are `ultracosmetics.*`.
