# AquaSpawn — Spawn Guide

AquaSpawn manages spawn behaviour on **TheySix Events**
(`play.theysix.tech`): where players appear on first join and after death.

## Files

| File | Purpose |
|------|---------|
| `config.yml` | Spawn settings (**empty = defaults**) |
| `menu.yml` | Spawn menu (**empty = defaults**) |

## Commands

| Command | Effect |
|---------|--------|
| `/spawn` | teleport to the spawn |
| `/setspawn` | set the spawn (staff) |
| `/aquaspawn reload` | reload the config |

## Notes

- EssentialsX also provides `/spawn`; make sure only one plugin owns the spawn
  point to avoid conflicts. See
  [`../Essentials/README.md`](../Essentials/README.md).
- The minigames' own main lobby is set separately with `/ba setmainlobby` — see
  [`../BlueArcade3/README.md`](../BlueArcade3/README.md).
