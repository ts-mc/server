# TheySix Events — Plugin Inventory

Complete list of plugins shipped in the repository (copied from the archived
server + the Party Games setup).

## Core server

| File | Version | Notes |
|------|---------|-------|
| `server.jar` | Paper 1.21.11 | server core |

## Minigames

| Plugin | Version | Notes |
|--------|---------|-------|
| `plugins/BlueArcade3/` | Arcade by Blueva core with TheySix Events display branding | core + 19 modules |
| `plugins/BlueArcade3/modules/*-Module-*.jar` | various | per-game modules |

### TheySix Events modules

| Module | Jar |
|--------|-----|
| All Against All | `AllAgainstAll-Module-1.0.3.jar` |
| Block Party | `BlockParty-Module-Minecraft-1.0.4.jar` |
| Bridge Race | `BridgeRace-Module-Minecraft-1.0.0.jar` |
| Chairs | `Chairs-Module-Minecraft-1.0.2.jar` |
| Exploding Sheep | `ExplodingSheep-Module-1.0.3.jar` |
| Fast Zone | `FastZone-Module-1.0.2.jar` |
| Knockback | `Knockback-Module-1.0.2.jar` |
| Minefield | `Minefield-Module-1.0.3.jar` |
| One In The Chamber | `OneInTheChamber-Module-Minecraft-1.0.2.jar` |
| Race | `Race-Module-Minecraft-1.0.1.jar` |
| Red Alert | `RedAlert-Module-1.0.1.jar` |
| Run From The Beast | `RunFromTheBeast-Module-Minecraft-1.0.4.jar` |
| Snowball Fight | `SnowballFight-Module-1.0.1.jar` |
| Spleef | `Spleef-Module-1.0.2.jar` |
| Splegg | `Splegg-Module-1.0.1.jar` |
| TNT Run | `TNTRun-Module-1.0.6.jar` |
| TNT Tag | `TNTTag-Module-Minecraft-1.0.2.jar` |
| Traffic Light | `TrafficLight-Module-1.0.3.jar` |
| Water Well | `WaterWell-Module-1.0.0.jar` |

## Support / API plugins

| Plugin | Version |
|--------|---------|
| FastAsyncWorldEdit | `FastAsyncWorldEdit-Paper-2.15.4.jar` |
| WorldGuard | `worldguard-bukkit-7.0.13-dist.jar` |
| Multiverse-Core | `multiverse-core-4.3.14.jar` |
| Vault | `Vault.jar` |
| LuckPerms | `LuckPerms-Bukkit-5.4.156.jar` |
| PlaceholderAPI | `PlaceholderAPI-2.12.3.jar` |
| NoteBlockAPI | (in PG setup) |
| ItemJoin | `ItemJoin-6.1.7.jar` |
| TAB | `TAB v6.1.2.jar` |
| ViaVersion / ViaBackwards / ViaRewind | 5.7.1 / 5.7.1 / 4.0.14 |
| EssentialsX (+Chat, +Spawn) | 2.22.0 |
| ProtocolLib | 5.4.0 |
| spark | profiler |
| bStats | metrics |
| Maintenance | 4.0.1 |
| MiniMOTD | 2.2.4 |
| LibsDisguises | 26.8.13-Free |
| HeadDB | 5.0.0-rc.15 |
| UltraCosmetics | 3.16-RELEASE |
| SkinsRestorer | — |
| AquaSpawn | — |
| HubPVP | 3.1.2 |
| FancyHolograms | 2.10.1-java21 |
| FancyNpcs | 2.10.1-java21 |
| FancyGlow | 2.10.2 |
| DeluxeMenus / DeluxeTags | 1.14.0 / 1.8.3 |
| DeluxeHub | — |
| DiscordSRV (+Staff Chat) | 1.30.5 / 1.4.6 |
| AdvancedBanZ | 4.0.1 |
| BanWebhook | 1.0.0 |
| EasyCommandBlocker | 1.18.1 |
| FreedomChat | 1.7.7 |
| LogFilter | 3.2.0 |
| MyCommand (packetevents) | 2.13.0 |
| ConditionalEvents | 4.74.1 |
| VoidGen | 2.3.4 |
| WorldServer | 1.9.4 |
| Warps | — |
| LPC | 3.6.2 |
| SuperBlockCommands | — |
| ProBlockedCommands | — |
| BlockParticles | 1.12 |

## Configuration files

| File | Purpose |
|------|---------|
| `server.properties` | MOTD, port, online-mode |
| `config/paper-global.yml` | Paper global settings |
| `config/paper-world-defaults.yml` | Paper per-world defaults |
| `spigot.yml` / `bukkit.yml` / `commands.yml` / `help.yml` | core config |
| `world/*/paper-world.yml` | per-world overrides |