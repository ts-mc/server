# TAB — Branding Guide
This plugin renders the in-game tab list, bossbars, and scoreboards for
**TheySix Events** (`play.theysix.tech`).

- **Server IP (gradient):** `&#4CADD0ᴛ&#55B4D4ʜ&#5FBBD9ᴇ&#68C2DDʏ&#71C9E1s&#7AD0E5ɪ&#84D6EAx&#8DDDEE.&#96E4F2s&#9FEBF6ᴘ&#A9F2FBᴀ&#B2F9FFʀ&#A9F2FBᴋ&#9FEBF6ᴇ&#96E4F2ᴅ&#8DDDEE.&#84D6EAɴ&#7AD0E5ᴇ&#71C9E1ᴛ&#68C2DDᴡ&#5FBBD9ᴏ&#55B4D4ʀ&#4CADD0ᴋ`
- **Server IP (plain):** `play.theysix.tech`
- **Server name (gradient):** `&#4CADD0&lT&#5BB8D7&lh&#69C3DD&le&#78CEE4&ly&#86D8EB&lS&#95E3F2&li&#A3EEF8&lx &#A1ECF7&lE&#90E0EF&lv&#7FD3E8&le&#6EC6E0&ln&#5DBAD8&lt&#4CADD0&ls`
- **Server name (plain):** `TheySix Events`

## Branding references
| File | Field | Value |
|------|-------|-------|
| `config.yml` | header `default` line 2 | server-name gradient |
| `config.yml` | footer `default` | `%animation:minigames%` + IP gradient |
| `config.yml` | bossbar `ServerInfo.text` | server IP gradient |
| `config.yml` | scoreboard `scoreboard` Info line | server IP gradient |
| `config.yml` | scoreboard `staffteam.*` | staff rank, ping, TPS, staff chat, staff online |
| `config.yml` | scoreboard `admin.*` | the above **plus** `%admin_chat%` |
| `config.yml` | layout `1|Server` | slot `2` server IP gradient |
| `config.yml` | layout `15|Minigames` | slots 16-34: all 19 minigames |
| `config.yml` | `date-locale` | `en` |
| `animations.yml` | `web` / `vote` typing-effect texts | `play.theysix.tech` |
| `animations.yml` | `minigames` | 19-game cycler used by the footer |

## Minigames in the tab section

The 19 minigames from `docs/minigames-list.md` are listed in
`config.yml` → `layout.layouts.default.fixed-slots` (slots `16`-`34`), under the
`15|&3Minigames&f:` heading. The footer also cycles the same list through the
`minigames` animation.

## Staff / admin scoreboard
Two extra scoreboards are defined under `scoreboard.scoreboards`:

| Scoreboard | Shown to | Key lines |
|------------|----------|-----------|
| `staffteam` | primary group `staffteam` | staff rank, `%player_ping%`, `%server_tps_1_colored%`, `%staffteam_chat%`, `%staffonline%` |
| `admin` | primary group `admin`, `owner` or `manager` | the same plus `%admin_chat%` |

If the `%staffteam_chat%` / `%admin_chat%` lines render literally, the
chat-expansion plugin providing them is not installed/loaded — see the note
below.

> `%staffteam_chat%` / `%admin_chat%` are **not** TAB-native placeholders. They
> must come from a chat plugin (e.g. a PlaceholderAPI expansion for
your staff-chat system).

## Minigames stats scoreboard (fixed 2026-09-22)

The `scoreboard-1.20.3+` scoreboard shows player stats from several sources. It
previously used four placeholders that **do not exist** in the BlueArcade
expansion (`%bluearcade_active_games%`, `%bluearcade_votes_total%`,
`%bluearcade_coins_earned%`, `%bluearcade_xp_earned%`), so those lines rendered as
literal text. They were replaced, and **money** (ExcellentEconomy via Vault) and
**votes** (VotingPlugin) lines were added:

| Line | Placeholder | Source |
|------|-------------|--------|
| Games played | `%bluearcade_stat_global_games_played%` | BlueArcade |
| Wins | `%bluearcade_stat_global_wins%` | BlueArcade |
| Kills | `%bluearcade_stat_global_kills%` | BlueArcade |
| Deaths | `%bluearcade_stat_global_deaths%` | BlueArcade |
| Money | `%vault_eco_balance_formatted%` | Vault → ExcellentEconomy |
| Votes | `%votingplugin_total%` | VotingPlugin (this month) |
| Vote streak | `%votingplugin_dailyvotestreak%` | VotingPlugin |
| Credits | `%bluearcade_stat_global_credits%` | BlueArcade (in-store currency) |
| Stars | `%bluearcade_stat_global_stars%` | BlueArcade |

Matching `placeholder-refresh-intervals` entries were added for the money and
vote placeholders (5 s each — vote totals change rarely).

See `plugins/PlaceholderAPI/README.md` for the full, verified placeholder lists.

## Regenerating typing-effect frames
`animations.yml` uses a character-by-character typing animation. When the
domain changes, regenerate the `web` and `vote` blocks from the domain with:

```python
ip = "play.theysix.tech"
frames = [f"&e{ip}"] * 5
frames += [ip[:i] + "&6" + ip[i] + "&e" + ip[i+1:] for i in range(len(ip))]
frames.append(f"&e{ip}")
```

The frame format is:

- fully-typed: `&e` + BASE
- typing frame for index `i`: BASE[:i] + `&6` + BASE[i] + `&e` + BASE[i+1:]

Do not hand-edit individual frames — the count must match the string length
or the animation desyncs.
