# TheySix Events — Plugins

This folder contains every plugin on **TheySix Events**
(`play.theysix.tech`). Each plugin folder has its own `README.md` guide.

> **Java:** `play.theysix.tech` · **Bedrock:** `bedrock.theysix.tech:25832` ·
> **MOTD:** `TheySix Events` · **Website:** <https://theysix.tech> ·
> **Events:** <https://events.theysix.tech>

## Index

| Plugin | Role |
|--------|------|
| [`BlueArcade3/`](BlueArcade3/README.md) | **Minigames core** — 19 games, arenas, menus, store |
| [`TAB/`](TAB/README.md) | Tab list, bossbars, scoreboards, layout |
| [`MyCommand/`](MyCommand/README.md) | `/ts`, `/tse`, `/tsa` command aliases + AntiTAB |
| [`LuckPerms/`](LuckPerms/README.md) | Permissions, groups, prefixes |
| [`PlaceholderAPI/`](PlaceholderAPI/README.md) | Shared placeholders |
| [`Vault/`](Vault/README.md) | Economy / permission / chat API bridge |
| [`Essentials/`](Essentials/README.md) | Spawn, kits, teleport, economy |
| [`DeluxeHub/`](DeluxeHub/README.md) | Lobby join items and server selector |
| [`DeluxeMenus/`](DeluxeMenus/README.md) | Inventory GUI menus |
| [`UltraCosmetics/`](UltraCosmetics/README.md) | Hats, gadgets, pets, particles |
| [`FancyNpcs/`](FancyNpcs/README.md) | Server-side NPCs |
| [`FancyHolograms/`](FancyHolograms/README.md) | Floating text holograms |
| [`FancyGlow/`](FancyGlow/README.md) | Glow-outline colours |
| [`FancyAnalytics/`](FancyAnalytics/README.md) | Telemetry for the Fancy plugins |
| [`HeadDB/`](HeadDB/README.md) | Custom head database |
| [`ItemJoin/`](ItemJoin/README.md) | Join items |
| [`AquaSpawn/`](AquaSpawn/README.md) | Spawn management |
| [`HubPVP/`](HubPVP/README.md) | Hub PvP |
| [`LobbyFighting/`](LobbyFighting/README.md) | Lobby PvP |
| [`Multiverse-Core/`](Multiverse-Core/README.md) | World management (`PG-01` … `PG-06`) |
| [`WorldGuard/`](WorldGuard/README.md) | Region protection |
| [`FastAsyncWorldEdit/`](FastAsyncWorldEdit/README.md) | World editing / schematic paste |
| [`DiscordSRV/`](DiscordSRV/README.md) | Discord ↔ Minecraft bridge |
| [`DiscordSRV-Staff-Chat/`](DiscordSRV-Staff-Chat/README.md) | Staff/admin chat channels |
| [`FreedomChat/`](FreedomChat/README.md) | Chat behaviour control |
| [`AdvancedBan/`](AdvancedBan/README.md) / [`AdvancedBanZ/`](AdvancedBanZ/README.md) | Punishments |
| [`BanWebhook/`](BanWebhook/README.md) | Ban → Discord webhook |
| [`EasyCommandBlocker/`](EasyCommandBlocker/README.md) | Hide/block commands |
| [`Maintenance/`](Maintenance/README.md) | Maintenance mode |
| [`MiniMOTD/`](MiniMOTD/README.md) | Server-list MOTD |
| [`ViaVersion/`](ViaVersion/README.md) | Version support |
| [`ViaBackwards/`](ViaBackwards/README.md) | Older-client support |
| [`ProtocolLib/`](ProtocolLib/README.md) | Packet library (TAB, LibsDisguises, …) |
| [`LibsDisguises/`](LibsDisguises/README.md) | Disguises |
| [`SkinsRestorer/`](SkinsRestorer/README.md) | Skins |
| [`spark/`](spark/README.md) | Performance profiler |
| [`LogFilter/`](LogFilter/README.md) | Console log filtering |
| [`Updater/`](Updater/README.md) | Plugin updater |
| [`bStats/`](bStats/README.md) | Metrics library |

## Rebranding

All player-facing config is rebranded to **TheySix Events**. The canonical
public endpoints are Java `play.theysix.tech` and Bedrock
`bedrock.theysix.tech:25832`. The display name gradient is:

`&#4CADD0&lT&#5BB8D7&lh&#69C3DD&le&#78CEE4&ly&#86D8EB&lS&#95E3F2&li&#A3EEF8&lx &#A1ECF7&lE&#90E0EF&lv&#7FD3E8&le&#6EC6E0&ln&#5DBAD8&lt&#4CADD0&ls`

See [`../docs/rebrand-notes.md`](../docs/rebrand-notes.md) for the full
before/after mapping and what intentionally stays as `BlueArcade3`.

## Changelog
Changes to this server are logged in [`../updates/`](../updates/README.md) —
one dated file per work session (no git history). Latest entry:
`../updates/2026-10-18-menus-and-aliases.md`.

## Notes
- Most plugin configs are **empty** and use the plugin's built-in defaults; only
  BlueArcade3, TAB, MyCommand and bStats ship authored values.
- Every `.yml` under these folders is English; the empty Spanish locale stubs
  under `BlueArcade3/language/es/` are not referenced.
- `plugins/.paper-remapped/` is a runtime cache — do not edit it.
