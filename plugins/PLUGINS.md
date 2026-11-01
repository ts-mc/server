# TheySix Server Plugins

This repository stores configuration; it does not distribute plugin JARs. Obtain
plugins from their official project pages or the server administration. This
inventory was read from local root-level JAR metadata on 2026-11-01, except
LIVE-only plugins identified by SFTP listing and the previous startup log.
Versions below are local unless marked LIVE.

- Target: Paper 1.21.11, Java and Bedrock support.
- Java runtime: Java 25 in the last available LIVE startup log; recheck after
  the next controlled start.
- BlueArcade3 has 19 local minigame module JARs under its data directory. Keep
  the compatible module set with the TheySix build.

| Plugin | Purpose | Local version | Download / source | Required? | Notes |
| --- | --- | --- | --- | --- | --- |
| AdvancedBanZ | Moderation backend | 4.0.1 | [Modrinth](https://modrinth.com/plugin/advancedbanz) | Yes | Keep BanWebhook integration. |
| BanWebhook | Punishment webhook | 1.0.0 | Custom/private — contact administration | Yes | Credentials are runtime-only. |
| BetterModel | Custom model rendering | 3.6.0 (LIVE) | [Hangar](https://hangar.papermc.io/toxicity188/BetterModel) | Review | LIVE-only JAR; preserve pending dependency review. |
| BlockParticles | Particle effects | 1.12 | [Modrinth](https://modrinth.com/plugin/blockparticles) | Review | Verify current use. |
| BlueArcade3 | Normal minigame system | 3.4.5, TheySix build | Custom/private TheySix build — contact administration | Yes | Do not substitute upstream; keep 19 modules. |
| DankVotes | Vote commands, rewards and leaderboard placeholders | 1.2.0 | [Official SpigotMC project](https://www.spigotmc.org/resources/dankvotes-vote-rewards-votifier-vote-parties.139529/) | Yes | Kept alongside VotifierPlus; vote data stays runtime-only. |
| DeluxeMenus | GUI menus | 1.14.0 | [SpigotMC](https://www.spigotmc.org/resources/deluxemenus.11734/) | Yes | Minigame navigation. |
| DiscordSRV | Discord bridge | 1.30.5 | [GitHub](https://github.com/DiscordSRV/DiscordSRV) | Review | Secret configuration stays local. |
| EasyCommandBlocker | Command filtering | 1.18.1 | Source unverified — contact administration | Review | Check Paper compatibility. |
| EssentialsX | Core server commands | 2.22.1-dev+27 (LIVE filename) | [Official downloads](https://essentialsx.net/downloads) | Yes | Main JAR required on LIVE; Spawn and Chat add-ons are separate. |
| FancyHolograms | Lobby and vote leaderboard holograms | 2.12.0 | [Hangar](https://hangar.papermc.io/Oliver/FancyHolograms) | Yes | Keep paired with FancyNpcs; nine current definitions. |
| FancyNpcs | Lobby information and vote leaderboard NPCs | 2.12.0 | [Hangar](https://hangar.papermc.io/Oliver/FancyNpcs) | Yes | Preserve nine definitions, actions, visibility and period switching. |
| FastAsyncWorldEdit | WorldEdit implementation | 2.15.4 | [Modrinth](https://modrinth.com/plugin/fastasyncworldedit) | Yes | Required by WorldGuard. |
| Floodgate | Bedrock authentication | 2.2.5 snapshot b141 | [GeyserMC](https://geysermc.org/download) | Yes | Private key is runtime-only. |
| Geyser | Bedrock connectivity | 2.12.0 snapshot (local and LIVE) | [GeyserMC](https://geysermc.org/download) | Yes | Confirmed loaded after controlled restart; Bedrock login still needs an in-game test. |
| HeadDB | Head database | 5.0.0-rc.15 | Source unverified — contact administration | Review | Release candidate. |
| ItemJoin | Join items | 6.1.7 release b1135 | [SpigotMC](https://www.spigotmc.org/resources/itemjoin.12661/) | Yes | Bedrock prefix must remain `BR_`. |
| LibsDisguises | Disguises | 26.8.13 | [GitHub](https://github.com/libraryaddict/LibsDisguises) | Review | Confirm ProtocolLib compatibility. |
| LogFilter | Log filtering | 3.2.0 | [GitHub](https://github.com/4drian3d/LogFilter) | Review | Verify current use. |
| LPC | Chat formatting | 3.6.2 | Source unverified — contact administration | Review | Confirm source. |
| LuckPerms | Permissions | 5.5.85 | [Official site](https://luckperms.net/download) | Yes | Credential-bearing local config. |
| Maintenance | Maintenance mode | 4.0.1 | [SpigotMC](https://www.spigotmc.org/resources/maintenance.40699/) | Review | Verify current use. |
| MiniMOTD | Server status display | 2.2.5 | [GitHub](https://github.com/jpenilla/MiniMOTD) | Review | Status packet fault still needs diagnosis. |
| Multiverse-Core | World registration | 4.3.14 | [GitHub](https://github.com/Multiverse/Multiverse-Core/releases) | Yes | Never recreate retired event worlds. |
| MyCommand | Custom commands | 5.7.4 | Source unverified — contact administration | Yes | No authoritative `/spawn` destination. |
| nightcore | Plugin support library | 2.16.4 | Source unverified — contact administration | Review | Confirm dependent plugins. |
| NoteBlockAPI | Note block audio API | 1.6.3 | [GitHub](https://github.com/koca2000/NoteBlockAPI) | Review | Confirm dependent plugins. |
| PacketEvents | Packet API | 2.13.0 | [GitHub](https://github.com/retrooper/packetevents) | Yes | Used by packet-aware plugins. |
| PlayerLogs | Player activity logging | 1.0 (LIVE) | Source unverified — contact administration | Review | LIVE-only JAR; logs remain runtime data. |
| PlaceholderAPI | Placeholder expansion API | 2.12.3 | [SpigotMC](https://www.spigotmc.org/resources/placeholderapi.6245/) | Yes | Check expansions separately. |
| ProBlockedCommands | Command blocking | 1.9 | Custom/private or source unverified — contact administration | Review | JAR named `SuperBlockCommands.jar`. |
| ProtocolLib | Protocol API | 5.4.0 | [GitHub](https://github.com/dmulloy2/ProtocolLib/releases) | Review | Confirm explicit Paper 1.21.11 support before update. |
| SkinsRestorer | Java/Bedrock skins | 15.12.6 | [Official site](https://skinsrestorer.net/download) | Yes | Keep Floodgate integration. |
| TAB | Tab list and scoreboard | 6.1.2 | [GitHub](https://github.com/NEZNAMY/TAB/releases) | Yes | Event-world conditions retired. |
| TSHide | General `/hide` | 1.0.1 | Custom/private — contact administration | Optional | No longer required by TSEvent. |
| TSStaff | Staff tooling | 1.1.0 (LIVE) | Custom/private TheySix build — contact administration | Review | LIVE-only JAR; preserve until its role is reviewed. |
| UltraCosmetics | Cosmetics | 3.16 release | Source unverified — contact administration | Review | Generic Halloween cosmetics may remain. |
| Vault | Economy/permissions API | 1.7.3 b131 | [BukkitDev](https://dev.bukkit.org/projects/vault) | Review | Confirm dependents. |
| ViaBackwards | Older client protocols | 5.12.2 | [Official site](https://viaversion.com/backwards) | Yes | Coordinate with ViaVersion. |
| ViaVersion | Newer client protocols | 5.12.2 | [Official site](https://viaversion.com) | Yes | Coordinate with ViaBackwards. |
| VoidGen | Void world generation | 2.3.4 | [GitHub](https://github.com/NicoNekoDev/VoidGen) | Review | Preserve existing worlds. |
| VotifierPlus | Vote receiving | 1.4.3 | Source unverified — contact administration | Yes | Credentials are runtime-only. |
| Warps | Warps | 2.0.7 | Custom/private or source unverified — contact administration | Review | Confirm source. |
| WorldGuard | Region protection | 7.0.17 | [EngineHub](https://github.com/EngineHub/WorldGuard) | Yes | Requires a WorldEdit implementation; TheySix uses FAWE. |
| WorldServer | World management | 1.9.4 | Custom/private or source unverified — contact administration | Review | Confirm source. |

## Dependencies and update policy

WorldGuard requires a WorldEdit implementation; TheySix uses FastAsyncWorldEdit.
FancyNpcs and FancyHolograms jointly provide permanent lobby NPCs, holograms,
and vote leaderboard behavior. Geyser uses Floodgate authentication. AdvancedBanZ
and BanWebhook jointly handle moderation and punishment webhooks. Coordinate
ViaVersion and ViaBackwards. EssentialsX uses Vault for permissions integration
where configured; TAB and other menus use PlaceholderAPI expansions.

Version numbers alone do not establish Paper 1.21.11 compatibility. Review
release notes, Java requirements, config migrations, and plugin dependencies
before replacing a JAR. Keep rollback copies outside Git. Never commit keys,
credentials, databases, logs, world data, or plugin JARs.

## Update review (2026-11-01)

No local JAR was replaced during this review. A successful fresh LIVE snapshot,
confirmed runtime Java version, and a controlled Paper 1.21.11 test are needed
before changing the server's plugin set. These are confirmed official-source
findings; other plugins remain **version status unverified**.

| Plugin | Local → available | Decision |
| --- | --- | --- |
| [Multiverse-Core](https://github.com/Multiverse/Multiverse-Core/releases) | 4.3.14 → 5.8.2 | Review major-version migration and world registration before updating. |
| [FastAsyncWorldEdit](https://github.com/IntellectualSites/FastAsyncWorldEdit/releases) | 2.15.4 → 2.16.1 | Review Java/runtime and WorldGuard compatibility together. |
| [WorldGuard](https://github.com/EngineHub/WorldGuard/blob/version/7.0.x/CHANGELOG.md) | 7.0.17 → 7.0.20 | 7.0.19+ requires newer Minecraft than 1.21.11; do not install those versions. Review 7.0.18 with FAWE. |
| [FancyNpcs](https://hangar.papermc.io/Oliver/FancyNpcs/versions/) | 2.12.0 → 2.12.1 | Review vote NPC data and pair behavior with FancyHolograms. |
| [FancyHolograms](https://hangar.papermc.io/Oliver/FancyHolograms/versions) | 2.12.0 → 2.12.0+197 | Same base version, newer build; verify compatibility before replacing. Avoid 3.0 snapshots. |
| [ProtocolLib](https://github.com/dmulloy2/ProtocolLib/releases) | 5.4.0 → 5.4.0 stable | Current stable release only lists support through 1.21.8; 1.21.11 fixes are in development builds. Test before any change. |
| [PacketEvents](https://github.com/retrooper/packetevents/releases) | 2.13.0 → 2.14.0 | Newer stable release targets 26.3; coordinate packet-dependent plugins and test on Paper 1.21.11. |
| [TAB](https://github.com/NEZNAMY/TAB/releases) | 6.1.2 → 6.1.3 | Review scoreboard and placeholder behavior before replacing. |
| [DeluxeMenus](https://www.spigotmc.org/resources/deluxemenus.11734/) | 1.14.0 → 1.14.1 | Review menu actions and PlaceholderAPI integration before replacing. |
| [ItemJoin](https://www.spigotmc.org/resources/itemjoin.12661/) | 6.1.7 → 6.1.8 | Verify join item behavior and preserve the exact `BR_` prefix. |
| [AdvancedBanZ](https://modrinth.com/plugin/advancedbanz) | 4.0.1 → 4.0.1 | Current published version; preserve BanWebhook integration. |
| [ViaVersion](https://github.com/ViaVersion/ViaVersion/releases) | 5.12.2 → 5.12.2 | Current release. Coordinate any future change with ViaBackwards. |
| [ViaBackwards](https://github.com/ViaVersion/ViaBackwards/releases) | 5.12.2 → 5.12.2 | Current release; keep paired with ViaVersion. |
| [SkinsRestorer](https://github.com/SkinsRestorer/SkinsRestorer/releases) | 15.12.6 → 15.12.6 | Current release; preserve Bedrock skin integration. |
| [MiniMOTD](https://github.com/jpenilla/MiniMOTD/releases) | 2.2.5 → 2.2.5 | Current release; status response fault remains a separate test item. |

## Retired / historical plugins

TSEvent was retired after Halloween. Its historical configuration is in
`.event-halloween/`; it is not part of the intended active server. A local
`TSEvent-1.3.9.jar` still exists and must not be deployed as an active plugin.

DeluxeTags was retired in favor of TAB rank labels driven by LuckPerms.
VotingPlugin and standalone Votifier were retired; the intended voting stack is
DankVotes with VotifierPlus.
