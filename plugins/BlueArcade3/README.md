# BlueArcade3 — TheySix Events (Minigames Core)

This is the data folder of the minigames core that powers **TheySix Events**.
It runs the 19 minigames listed in [`docs/minigames-list.md`](../../docs/minigames-list.md)
across arena worlds `PG-01` … `PG-06`.

> **Server:** `theySix Events` · **IP:** `play.theysix.tech` ·
> **Bedrock:** `bedrock.theysix.tech:25832` · **Website:** <https://events.theysix.tech>

## Important — the folder name is load-bearing

The folder must stay named **`BlueArcade3`**. Bukkit derives the data folder from
the plugin `name:` in the core jar, so renaming it breaks the plugin's ability to
find its own configuration. The player-facing branding is fully rebranded to
*TheySix Events*; the folder name is internal plumbing only. The core jar is
`plugins/BlueArcade-3.4.5.jar`.

## Files in this folder

| File | Purpose |
|------|---------|
| `settings.yml` | Global plugin config: display name, `server_ip`, brand colours, database, economy, XP, teams |
| `items.yml` | Lobby/menu items and per-game kit items (names use the brand gradient) |
| `language.yml` | Language manifest; English only (`en`) |
| `rewards.yml` | Daily/vote/level reward messages |
| `sounds.yml` | Sound definitions for minigames |
| `menus/` | `java/` and `bedrock/` game-selector menu screens |
| `store/` | Kill/death/victory effects and victory music |
| `modules/` | Per-game config: `settings.yml`, `language.yml`, `achievements.yml` |
| `data/` | Runtime data: `global.json`, `arenas/<id>/arena.json`, per-game JSON, schematics |

## Branding values

- Server name: **TheySix Events**
- Server name gradient: `&#4CADD0&lT&#54B3D4&lh&#5CB9D7&le&#64BFDB&ly&#6BC4DE&lS&#73CAE2&li&#7BD0E6&lx &#8BDCED&lE&#93E2F1&lv&#9AE7F4&le&#A2EDF8&ln&#AAF3FB&lt&#B2F9FF&ls`
- Server IP (plain): `play.theysix.tech`
- Server IP gradient: `&#4CADD0ᴛ&#55B4D4ʜ&#5FBBD9ᴇ&#68C2DDʏ&#71C9E1s&#7AD0E5ɪ&#84D6EAx&#8DDDEE.&#96E4F2s&#9FEBF6ᴘ&#A9F2FBᴀ&#B2F9FFʀ&#A9F2FBᴋ&#9FEBF6ᴇ&#96E4F2ᴅ&#8DDDEE.&#84D6EAɴ&#7AD0E5ᴇ&#71C9E1ᴛ&#68C2DDᴡ&#5FBBD9ᴏ&#55B4D4ʀ&#4CADD0ᴋ`
- Brand colours: primary `#4CADD0`, accent `#B2F9FF`; gradient `<gradient:#4CADD0:#B2F9FF>`

## Player commands

Player commands use the native `/ts` command (also `/tse`). Admin commands use the native `/tsa` command. `/ba` and `/baa` are no longer the recommended commands.

| Command | Effect |
|---------|--------|
| `/ts join [id\|name]` | join a specific arena (or open the filter GUI) |
| `/ts quickjoin` | join a random arena |
| `/ts leave` | leave the current arena |
| `/ts vote [minigame\|page <n>]` | vote for the next minigame |
| `/ts team [join <team_id>]` | select a team (team games only) |
| `/ts stats`, `/ts tops`, `/ts achievements`, `/ts level`, `/ts store` | progression menus |
| `/ts party <list\|create\|invite\|join\|…>` | party management |
| `/tsa help` | administrator help |
| `/tsa arena <id> info` | inspect an arena |

## Setup

Arenas are **data files, not registrations** — the plugin does not auto-discover
them. Register each arena once with `/tsa` (set lobby → set name → set min/max
players → add minigames) and set the main lobby with `/tsa setmainlobby`. Full
instructions: [`docs/setup-guide.md`](../../docs/setup-guide.md).

## Notes

- The custom JAR natively declares `/ts`, `/tse`, and `/tsa`. Internal legacy
  command references are being moved to those names in configuration.
- Module jar filenames (e.g. `Race-Module-Minecraft-1.0.1.jar`) must not be
  renamed; the plugin loads modules by name.
