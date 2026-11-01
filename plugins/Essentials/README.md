# EssentialsX — Core Commands Guide

EssentialsX provides the everyday player and staff commands on **TheySix Events**
(`play.theysix.tech`): spawn, kits, teleport, homes, and the economy that
Vault exposes to other plugins.

## Files

| File | Purpose |
|------|---------|
| `config.yml` | EssentialsX settings (**empty = defaults**) |
| `kits.yml` | Kit definitions (**empty = defaults**) |
| `spawn.yml` | Spawn location |
| `worth.yml` | Item sell values (**empty = defaults**) |
| `tpr.yml` | Random-teleport settings |
| `custom_items.yml` | Custom item aliases |

> The three companion jars are `EssentialsX-2.22.0.jar`,
> `EssentialsXChat-2.22.0.jar` and `EssentialsXSpawn-2.22.0.jar`.

## Key commands

| Command | Effect |
|---------|--------|
| `/spawn` | teleport to the server spawn |
| `/sethome` / `/home` | set and use homes |
| `/kit` | list and claim kits |
| `/tpa <player>` | request a teleport |
| `/msg <player> <message>` | private message |
| `/balance` | show your economy balance |
| `/pay <player> <amount>` | pay another player |
| `/fly`, `/heal`, `/feed` | staff convenience commands |

## Notes

- The economy registered with Vault comes from EssentialsX — see
  [`../Vault/README.md`](../Vault/README.md).
- Permission nodes are `essentials.*` (e.g. `essentials.spawn`).
- The main lobby for the minigames is set with `/tsa setmainlobby`, not with
  EssentialsX.
