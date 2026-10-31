# TheySix Events — Setup Guide
This guide gets a TheySix Events server running from the files in this
repository, and covers the **arena wiring step** that is easy to miss.

> **Java:** `play.theysix.tech` · **Bedrock:** `bedrock.theysix.tech:25832` · **MOTD:** `TheySix Events` ·
> `online-mode=true` (online mode; review proxy authentication before changing it).
> **Version:** Paper 1.21.11 / Java 21.

---

## ⚠️ Read this first — core jar and data folder name
**The core jar is present:** `plugins/BlueArcade-3.4.5.jar` (declares itself as
plugin name `BlueArcade3`, version 3.4.5, main `net.blueva.arcade.v3.PluginEntrypoint`).

**The data folder is `BlueArcade3`** — Bukkit derives the data folder from the
plugin `name:` in `plugin.yml`. The player-facing rebrand is applied in its
configuration and language files; the internal folder name remains
`BlueArcade3` so the core can load the repository config.

So the layout this guide assumes is:

```
plugins/
  BlueArcade-3.4.5.jar     # the core
  BlueArcade3/             # its data folder (TheySix Events branding)
```

The folder name is invisible to players — it is internal plumbing only. The
player-facing rebrand happens in `language.yml`, the menus, `items.yml` and the
`/ts` alias (see *Command rebrand* below).

> Renaming the jar file itself is harmless (Bukkit reads `plugin.yml`, not the
> filename), but the folder name is load-bearing.

Verify before going further: after `java -jar server.jar`, the console must print
an enable line for the plugin. If it does not, check the folder name first.

---

## Repository layout
```
server.jar                # Paper 1.21.11 server core
server.properties         # MOTD, port (25625), online-mode=true
config/
  paper-global.yml        # Paper global settings
  paper-world-defaults.yml
world/                    # main hub world (TheySix Events)
world_lobby/              # TheySix Events lobby world
PG-01 .. PG-06/           # 6 arena worlds (folder name == level name)
world_nether/             # nether
plugins/
  BlueArcade3/            # config + modules + data for the minigames plugin
    settings.yml          # global plugin config
    items.yml language.yml menus/ store/ sounds/ rewards.yml achievements.yml
    modules/              # 19 game module jars + their per-game config folders
    data/
      global.json         # main lobby spawn
      arenas/<id>/arena.json
      arenas/<id>/games/<game_id>.json
      arenas/<id>/schems/
  ...
```

### World folder names vs. level names — important
Bukkit identifies worlds by **level name**, not by folder. The arena folders are
now named to match their level names, so there is no longer a mismatch:

| Folder | Level name used everywhere else |
|--------|----------------------------------|
| `world/` | `world` (hub) |
| `PG-01/` … `PG-06/` | `PG-01` … `PG-06` (arenas) |
| `world_nether/` | `world_nether` |
| `world_the_end/` | `world_the_end` |
| `world_lobby/` | `world` until repaired — see note below |

The arena folders were previously `world_01` … `world_06` while their level
names were `PG-01` … `PG-06`. That mismatch meant Multiverse could not load them
and could not attach an alias, so DiscordSRV showed the raw world name. The
folders were renamed to `PG-0N` so folder, level name and every `arena.json` /
`games/*.json` reference (`"world": "PG-01"`) all agree.

**The folder name must equal the level name.** Multiverse loads a world by
asking Bukkit for a folder named after the `worlds.yml` key; if the key is
`PG-01` but the folder is `world_01`, Bukkit's `WorldCreator` finds no world and
Multiverse reports *"could NOT be loaded because it contains errors and is
probably corrupt"*. Keep `PG-01/` … `PG-06/` as the folder names.

> `world_lobby/level.dat` is truncated (no `SpawnX/Y/Z`) and its level name is
> `world`, which collides with the hub. It is not loaded. Repair its `level.dat`
> and rename its level name to `world_lobby` to use it.

## Required plugins (already present)

| Plugin | Purpose |
|--------|---------|
| `BlueArcade3` (display name: TheySix Events) | minigames core + 19 modules |
| `FastAsyncWorldEdit` | schematic paste for arenas |
| `WorldGuard` | region protection per arena |
| `Multiverse-Core` | world management (PG-01…PG-06) |
| `Vault` | economy API |
| `LuckPerms` | permissions |
| `PlaceholderAPI` | placeholders |
| `NoteBlockAPI` | `.nbs` music |
| `ItemJoin` | join items |
| `TAB` | tab list |
| `ViaVersion` / `ViaBackwards` / `ViaRewind` | version cross-play |
| `EssentialsX` | spawn, kits, teleport |

## Quick start
1. Install Java 21.
2. Place `server.jar` and the `plugins/` folder in the server directory.
3. Set `eula=true` in `eula.txt`.
4. Drop the **core TheySix Events jar** into `plugins/` (see the warning above).
5. Start the server once and confirm the plugin enables cleanly.
6. **Wire the arenas** (next section). The plugin does *not* auto-discover
   arenas from `data/arenas/` — you must register each one in game.
7. Stand on the hub spawn and run `/ba setmainlobby`. Until this is set, players
   get `There is no spawn configured for TheySix Events`.
8. Players join with `/ba join`, `/ba quickjoin`, or the join GUI item.

## Arena wiring (the step most guides skip)

The six arena configs under `plugins/BlueArcade3/data/arenas/1..6/` are
**data files, not registrations**. All six currently have
`basic.state: "ENABLED"` and `mode: "party"` in the file, but the plugin only
serves arenas it has registered at runtime. Copying an `arena.json` does not
create an arena by itself.

Do this once, per arena, as an operator:

1. `/baa help` — list the admin subcommands available on your core version.
2. Create/register the arena, then set its waiting lobby
   (`/baa arena <id> setlobby` while standing in the arena's lobby), set its name
   (`/baa arena <id> setname PG-0N`) and set min/max players. The plugin's own
   checklist (`incomplete_setup_list` in `language.yml`) is: set waiting lobby →
   set arena name → set min/max players → add and configure at least one
   minigame.
3. Add each minigame with `/baa`. Games are typed `mini` or `micro`; using the
   wrong type is rejected with `game_type_mismatch`.
4. For each game, set its bounds and spawns in game. The per-game files already
   define bounds and a 24-entry spawn list — verify them rather than trusting the
   numbers, and check that a spawn list of 24 does not place two players on the
   same block when the arena is full.
5. Confirm with `/baa arena <id> info` and check the arena appears in
   `/ba list`.

**Two things to fix before relying on the shipped configs:**

- **Identical waiting lobbies.** All six `waiting_lobby` blocks point at the
  same coordinates (`x≈0.40, y≈66.06, z≈0.49`) inside their respective worlds.
  That is correct only if the six arena worlds are genuinely mirrored builds.
- **Arena ids were all `1` — now fixed.** Arenas 2–6 all shipped with
  `"id": 1` copied from arena 1; each file now declares its own id (2–6) to match
  its folder. If you re-copy a Party Games setup, re-check this — five of the six
  files had it wrong.

## Arena configuration
Each arena is defined in
`plugins/BlueArcade3/data/arenas/<id>/arena.json`:

```json
{
  "arena": {
    "basic": {
      "id": 1,
      "state": "ENABLED",
      "display_name": "PG-01",
      "min_players": 2,
      "max_players": 24,
      "rounds": 8,
      "force_empty_inventory": false,
      "mode": "party"            // "party" (FFA) or "team"
    },
    "waiting_lobby": { "world": "PG-01", "x": 0.40, "y": 66.06, "z": 0.49,
                       "yaw": 89.70, "pitch": -1.65 },
    "games": [ "race", "spleef", "..." ]   // allowed minigames
  }
}
```

- `mode` — `party` (free-for-all) or `team`. Team games read team counts and
  colours from `settings.yml` → `teams.manager`; see `docs/minigames-list.md`.

Per-game settings (bounds, spawn list, win mode, time limit) are in
`games/<game_id>.json`:

```json
{
  "basic": { "enabled": true, "world": "PG-01", "time": 300, "win_mode": "last_standing" },
  "bounds": { "min": { "x": 769, "y": 63, "z": 773 },
              "max": { "x": 830, "y": 133, "z": 834 },
  "spawns": { "total": 24, "list": { "s1": { "x": ..., "y": ..., "z": ... }, "...": {} } }
}
```

**Most games never set `win_mode`.** Only `all_against_all`, `knockback`,
`one_in_the_chamber` and `snowball_fight` define it; the other 15 omit the key
and fall back to the module's built-in default. If you change a win condition and
it does not take effect, this is why — set `basic.win_mode` explicitly
(`last_standing` or `most_kills`) in the per-game file.

Minigame-specific options (menu icon, kit items, potion effects) are in
`modules/<game_id>/settings.yml`, alongside `language.yml` and
`achievements.yml` in the same module folder.

## Command rebrand — `/ts`, `/tse`, `/tsa`

The core jar declares its commands as `/arcade` (aliases `ba`, `barcade`,
`bluearcade`) and `/arcadeadmin` (aliases `baa`, `barcadea`, `bluearcadeadmin`).
The `/ba` alias is also **hardcoded inside ~15 compiled classes** (menus, signs,
item actions, command blocker), so it cannot be changed from config — see
`rebrand-notes.md`.

Rather than patch the jar, friendly aliases are registered through **MyCommand**:

| Alias | Forwards to | Who |
|-------|-------------|-----|
| `/ts` | `/ba` | everyone |
| `/tse` | `/ba` | everyone |
| `/tsa` | `/baa` | `theysixevents.admin` only |

Defined in `plugins/MyCommand/commands/theysix-aliases.yml`. `/ts join PG-01`
becomes `/ba join PG-01`; `/ts` alone opens the join GUI. GUI items, signs and
menus keep calling `/ba` internally and still work, because both resolve to the
same command — only the *typed* command differs.

The upstream names (`/arcade`, `/bluearcade`, `/barcadea`, …) are hidden from
players by **EasyCommandBlocker** so the old branding is never visible.

> **Requires the MyCommand jar:** `plugins/MyCommand-v5.7.4.jar` — present and
> > enabled. Reload with `/mycmd-reload` after editing the alias file.
>
> The alias definitions live in `plugins/MyCommand/commands/theysix-aliases.yml`
> and use MyCommand's `$args` variable to forward every argument, so `/ts join
> PG-01` becomes `/ba join PG-01`. Each alias runs with `type: RUN_CONSOLE` and
> `run_as: '%player%'`, so the forwarded `/ba` / `/baa` executes as the player who
> typed it (permission context preserved). `/tsa` is restricted to
> `theysixevents.admin`.
>
> **Fallback:** if `$args` forwarding ever breaks, the same file ships an
> `Explicit fallback` block — define one command per `/ba` subcommand
> (e.g. `/tsjoin` -> `/ba join $args`) and reload with `/mycmd-reload`.

## Hiding commands players cannot use
Players who lack permission can see and tab-complete commands they cannot run.
Two mechanisms are configured here:

**1. Tab-completion filter — MyCommand's AntiTAB feature** (already installed)
`plugins/MyCommand/config.yml` → `TAB_SUGGESTION_BLOCKER_LISTENER: true`, with the
allow-list in `tabsuggestionblocker.yml` (`MODE: WHITELIST`). In whitelist mode
players only see the commands listed for their group. Admins holding
`mycommand.antitab.admin` bypass the filter.

**2. Command blocking — EasyCommandBlocker** (already installed)
`plugins/EasyCommandBlocker/config.yml` blocks the listed commands and replies
with a custom message/title. It is already blocking `/plugins`, `/version`,
`/about` and now the upstream TheySix names.

### Recommended: CommandBlocker (by JadedMC)
**CommandBlocker 1.7.2** — supports Minecraft 1.8 – 26.1.2, so Paper 1.21.11 is
covered. Get it from [Hangar](https://hangar.papermc.io/JadedMC/CommandBlocker)
(or Modrinth/SpigotMC under the same name).

It is the **free** plugin; there is no paid tier to worry about. Its author also
publishes **CommandBlockerPro** — a separate, more advanced edition, also free —
but Pro is on version `1.4` and only declares support up to **1.21.1**, which
does *not* cover this server's 1.21.11. Use the base CommandBlocker.

It does exactly the job requested, per its feature list:
- hide specific commands from tab-complete **without blocking them**
- hide blocked commands from tab-complete
- optionally hide all commands containing a colon
- bypass with the permission `commandblocker.bypass`
- MiniMessage support in block messages
| | MyCommand AntiTAB + EasyCommandBlocker (installed) | CommandBlocker 1.7.2 |
|---|---|---|
| Cost | free, already present | free |
| MC support | n/a (installed) | 1.8 – 26.1.2 |
| Visibility driven by | group list in `tabsuggestionblocker.yml` | block/whitelist entries |
| Hide without blocking | partial | yes |
| Bypass permission | `mycommand.antitab.admin` | `commandblocker.bypass` |

**Recommendation:** the installed pair already covers the common case, so add
CommandBlocker only if you want the hide-without-block behaviour handled by a
purpose-built tool rather than a maintained list. CommandBlockerPro is worth
revisiting *only* once it declares support for 1.21.11+.

> Other free options: [CommandWhitelist](https://github.com/YouHaveTrouble/CommandWhitelist)
> (lock commands behind permission, block tab completion and execution) or
> [AdvancePlHide](https://modrinth.com/plugin/advancedplhide) (tab-completion only,
> requires ProtocolLib — which you already have).

## Key commands
Player commands use the alias `/ba` (or the friendlier `/ts` / `/tse`). Admin
commands use `/baa` (or `/tsa`).

| Command | Effect |
|---------|--------|
| `/ba join [id\|name]` | join a specific arena (or open the filter GUI) |
| `/ba quickjoin` | join a random arena |
| `/ba leave` | leave the current arena |
| `/ba vote [minigame\|page <n>]` | vote for the next minigame |
| `/ba team [join <team_id>]` | select a team (team games only) |
| `/ba stats (category) (alltime/monthly/yearly) (page)` | stats menu |
| `/ba tops (category) …` | top stats menu |
| `/ba achievements (category) (achievement) (page)` | achievements menu |
| `/ba level` | view your level |
| `/ba store` | open the cosmetic store |
| `/ba credits pay <player> <amount>` | pay credits |
| `/ba party <list\|create\|invite\|join\|…>` | party management |
| `/ba setmainlobby` | set the plugin's main lobby (admin) |
| `/ts`, `/tse` | player-friendly alias for `/ba` (needs the MyCommand jar) |
| `/tsa` | admin alias for `/baa` (needs the MyCommand jar) |
| `/baa help` | administrator help |
| `/baa arena <id> info` | inspect an arena |
| `/baa arena <id> setlobby\|setname\|…` | configure an arena |
| `/baa arena <id> forcestop` | force-stop a running arena |
| `/baa module store download <id>` | install a game module |

This table mirrors `language.yml` → `help`, which is the authoritative source;
re-check it after a core update.

## Production hardening
Review these **before** opening the server to the public:

| Setting | File | Current | Recommendation |
|---|---|
| `online-mode` | `server.properties` | `true` | Keep `true` unless auth is handled by an upstream proxy. Offline mode lets anyone impersonate any username. |
| `server-port` | `server.properties` | `25625` | Non-default; make sure the firewall allows it. |
| `max-players` | `server.properties` | check | Must be ≥ the sum of your arenas' `max_players`, or players get rejected mid-event. |
| `white-list` | `server.properties` | check | Enable for a private event. |
| `stats.sync.enabled` | `settings.yml` | `false` | Enable only with MySQL. The shipped `password: toor` is a placeholder — never leave it. |
| `metrics`, `anonymous_error_reporting` | `settings.yml` | `true` | Third-party telemetry. Set `false` if policy forbids it. |
| `updates.module_updates.enabled` | `settings.yml` | `true` | Downloads jars over the network at startup. Disable on a locked-down host. |
| `debug.enabled` | `settings.yml` | `false` | Leave `false` in production — it traces the whole arena lifecycle. |
| `bungee_mode.enabled` | `settings.yml` | `false` | Leave `false` for a single-server setup. |

Test one full round of every enabled game and confirm the arena resets. A game
that leaves its world dirty will corrupt the *next* game played in that arena.

## Known limitations
- **Data folder name is load-bearing** — it must remain `BlueArcade3` so the
  core plugin can find its configuration.
- **`$args` pass-through unverified** — `/ts join PG-01` → `/ba join PG-01`
  relies on MyCommand's `$args` variable and has not been tested live. If it does
  not forward arguments, define one explicit command per subcommand instead.
- The permission node `bluearcade.admin` and the `/ba` alias are hardcoded in the
  core jar; the config-level rebrand cannot change them. See
  `docs/rebrand-notes.md` and `docs/luckperms-setup.md`.
- Module jar filenames must not be renamed — the plugin loads modules by name.
- README/positional metadata in `settings.yml` still shows the upstream ASCII
  banner and `file_version: '8'`; bumping `file_version` is handled by the plugin,
  not by editing it by hand.

## Rebrand notes
See `rebrand-notes.md`. The plugin is shipped with **TheySix Events** branding;
the public Java address is `play.theysix.tech`, the Bedrock address is
`bedrock.theysix.tech:25832`, and the MOTD is
`TheySix Events`.
