# Vault — Economy / Permissions API Guide

Vault is the bridge plugin that lets other plugins talk to an economy,
permissions and chat provider without depending on it directly. It is installed
on **TheySix Events** (`play.theysix.tech`).

## Files

| File | Purpose |
|------|---------|
| `config.yml` | Vault settings (empty — defaults are used) |

## What it provides here

| Service | Provider used |
|---------|---------------|
| Economy | EssentialsX (`plugins/Essentials/`) |
| Permissions | LuckPerms (`plugins/LuckPerms/`) |
| Chat | EssentialsXChat (`plugins/EssentialsXChat-2.22.0.jar`) |

Vault itself has no player-facing config or commands. Plugins that need an
economy (e.g. the minigames store) call Vault, and Vault forwards the request to
the registered provider.

## Notes

- If the economy appears broken, check that EssentialsX loaded **before** the
  dependent plugin and that `/vault-info` shows an economy provider.
- The Vault balance placeholder surfaced to TAB is
  `%vault_eco_balance_formatted%`.
