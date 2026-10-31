# TheySix Events — Project Overview

TheySix Events is a **minigame Minecraft server** running Paper 1.21.11 (Java 21) at `play.theysix.tech`. It features 19 minigames across 6 arena worlds (PG-01…PG-06), team-based party games, a hub lobby, and full staff/moderation tooling.

## Server info

| Item | Value |
|------|-------|
| Java | `play.theysix.tech` |
| Bedrock | `bedrock.theysix.tech:25832` |
| Version | Paper 1.21.11 / Java 25 |
| MOTD | TheySix Events |
| Online mode | true |
| Website | https://theysix.tech |
| Events website | https://events.theysix.tech |

## File tree

```
/home/mihu/github/ts-server
├── server.jar                    # Paper 1.21.11 server core
├── server.properties             # MOTD, port, online-mode
├── eula.txt                      # Minecraft EULA (must be true)
├── spigot.yml / bukkit.yml       # server config
├── commands.yml / help.yml       # command/help customization
├── permissions.yml               # server permissions (ops, etc.)
├── ops.json                      # operator UUIDs
├── whitelist.json                # server whitelist
├── banned-ips.json / banned-players.json
├── usercache.json                # UUID→name cache
├── version_history.json          # deployment version tracking
├── wepif.yml                     # WEPIF permissions config
├── config/
│   ├── paper-global.yml          # Paper global settings
│   └── paper-world-defaults.yml  # Paper per-world defaults
├── libraries/                    # shared Maven dependencies (loaded by Paper)
│   └── com/sk89q/wepif/wepif.jar # WorldEdit permissions framework
├── logs/
│   └── latest.log                # current server log
├── plugins/
│   ├── BlueArcade-3.4.5.jar      # Minigames core (display: TheySix Events)
│   ├── BlueArcade3/              # core data folder (DO NOT RENAME)
│   ├── WorldGuard/               # region protection
│   ├── Multiverse-Core/          # world management
│   ├── FastAsyncWorldEdit-Paper-2.15.4.jar  # schematic paste
│   ├── LuckPerms-Bukkit-5.5.85.jar         # permissions
│   ├── Vault.jar                 # economy/permissions bridge
│   ├── PlaceholderAPI-2.12.3.jar # placeholder expansion API
│   ├── ProtocolLib-5.4.0.jar     # packet hooking
│   ├── ViaVersion / ViaBackwards / ViaRewind  # version cross-play
│   ├── EssentialsX / EssentialsXChat / EssentialsSpawn  # utilities
│   ├── TAB v6.1.2.jar            # tab list + name tags
│   ├── MyCommand-v5.7.4.jar      # command aliases (/ts, /tse, /tsa)
│   ├── DiscordsRV-Build-1.30.5.jar  # Discord integration
│   ├── FancyHolograms-2.12.0.jar # holograms
│   ├── FancyNpcs-2.12.0.jar      # NPCs
│   ├── FancyGlow-2.10.2.jar      # glow effect
│   ├── UltraCosmetics-3.16-RELEASE.jar    # cosmetics
│   ├── SkinsRestorer.jar         # skin management
│   ├── Geyser-Spigot.jar         # Bedrock cross-play
│   ├── packetevents-spigot-2.13.0.jar     # packet events
│   ├── HeadDB-5.0.0-rc.15.jar    # head database
│   ├── ItemJoin-6.1.7.jar        # join items
│   ├── DeltaCosmetics / decorative plugins
│   └── ... (48 total)
├── world/                        # main hub world
├── world_lobby/                  # lobby world (not currently loaded)
├── world_nether/                 # nether hub
├── world_the_end/                # end hub
├── PG-01 … PG-06/               # arena worlds (1v1, 2v2, 5v5, chaos, 10v10, FFA)
├── versions/
│   └── 1.21.11/
│       └── paper-1.21.11.jar     # Paper server jar
├── docs/                         # project documentation (this server side)
└── .docs/                        # repo-only docs (NOT uploaded to server)
    ├── docs/                     # guides, setup, inventories
    ├── README.md                 # explains what stays local vs uploaded
    └── ...
```

## Important — DO NOT EDIT

| File / type | Why |
|-------------|-----|
| `.vscode/sftp.json` | SFTP connection config for the server. Editing this will change how files are synced and may break deployments. |
| Any `.jar` file | Plugin JARs are pre-built binaries. Do not edit, rename, or replace unless you know exactly what you're doing. Renaming plugin JARs can break plugin loading and dependency resolution. |
| `plugins/BlueArcade3/` folder name | The data folder name is load-bearing — the core plugin requires it to be `BlueArcade3`. |
| `libraries/` contents | These are Paper-managed shared dependencies. Only add files here if you know they need to be on the shared classloader. |

## Key concepts

- **Arena worlds** (PG-01…PG-06): The level names MUST match the folder names exactly. Multiverse loads worlds by folder name, not by directory name.
- **BlueArcade3 data folder**: Internal name of the minigames plugin. Players see "TheySix Events" but the folder must stay `BlueArcade3`.
- **MyCommand aliases**: Player-friendly commands `/ts`, `/tse`, `/tsa` are forwarded to `/ba`, `/ba`, `/baa` via MyCommand.
- **Wepif**: `libraries/com/sk89q/wepif/wepif.jar` provides permissions integration for WorldGuard. Must be in the shared classloader.

## Deployment

- Everything in the repo **root** is uploaded to the Minecraft server host
- Everything in `.docs/` is repo-side only (documentation, guides, configs that never touch the server)
- The SFTP extension syncs root-level files to the server; do NOT sync `.docs/` or `.vscode/` to the server
