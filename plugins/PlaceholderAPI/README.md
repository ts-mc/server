# PlaceholderAPI — Placeholders Guide

PlaceholderAPI lets plugins expose dynamic placeholders (e.g. `%player_name%`)
to other plugins such as TAB, scoreboards and chat. It is installed on
**TheySix Events** (`play.theysix.tech`).

## Files

| File | Purpose |
|------|---------|
| `config.yml` | PlaceholderAPI settings (empty — defaults are used) |

## Placeholders used on this server

| Placeholder | Source flow | Used by |
|-------------|-------------|---------|
| `%player_name%` | built-in | TAB scoreboard, chat |
| `%player_ping%` | built-in | TAB latency line |
| `%server_online%` / `%server_max_players%` | built-in | TAB online line |
| `%luckperms-prefix%` / `%luckperms-suffix%` | LuckPerms expansion | TAB groups and rank line |
| `%luckperms_prefix%` | LuckPerms expansion | TAB staff scoreboard |
| `%deluxetags_tag%` | DeluxeTags expansion | TAB tag line and tab suffix |
| `%fancyglow_color%` | FancyGlow expansion | TAB glow line and tag prefix |
| `%vault_eco_balance_formatted%` | Vault expansion (ExcellentEconomy "money") | TAB money line |
| `%votingplugin_total%` / `%votingplugin_total_alltime%` | VotingPlugin expansion | TAB vote line, ItemJoin vote item |
| `%votingplugin_dailyvotestreak%` / `%votingplugin_points%` | VotingPlugin expansion | ItemJoin vote item |
| `%votingplugin_top_all_1%` | VotingPlugin expansion | ItemJoin vote item (top voter) |
| `%statistic_player_kills%` / `%statistic_deaths%` | Statistic expansion | TAB kills/deaths animation |
| `%server_tps_1_colored%` | built-in | TAB staff scoreboard (TPS) |
| `%staffteam_chat%` / `%admin_chat%` | **external chat expansion** | TAB staff scoreboard |
| `%bluearcade_stat_global_*%` | **BlueArcade expansion** | TAB scoreboard |

> `%staffteam_chat%` and `%admin_chat%` are **not** TAB-native. They must be
> provided by a PlaceholderAPI expansion from your chat plugin. If no such
> expansion is installed/loaded, those lines render literally.

## BlueArcade placeholders — what actually exists

The BlueArcade expansion registers with the identifier **`bluearcade`**, but it
only resolves a fixed set of sub-keys. Verified against the shipped
`BlueArcade-3.4.5.jar` (`net/blueva/arcade/v3/libraries/placeholderapi/`):

| Sub-key | Meaning |
|---------|---------|
| `stat_<scope>_<stat>[_<period>]` | A stat value. `<scope>` is `global` or `module_<game_id>`; `<period>` is optional (`alltime` default, `daily`, `weekly`, `monthly`, `yearly`) |
| `stat_display_...` / `stat_description_...` / `stat_scope_...` | Stat metadata (display name, description, scope) |
| `tops_...` | Leaderboard values |
| `category_...` | Stat category totals/names |

Valid **global** stat keys (from `StatsManager`): `games_played`,
`mini_games_played`, `time_played`, `wins`, `kills`, `deaths`, `stars`,
`credits`, `first_place`, `second_place`, `third_place`, `best_win_streak`,
`current_win_streak`.

Valid examples:

```
%bluearcade_stat_global_wins%
%bluearcade_stat_global_kills%
%bluearcade_stat_global_credits%
%bluearcade_stat_module_tnt_run_wins%
%bluearcade_stat_global_wins_daily%
```

> **There is no `%bluearcade_votes_total%`, `%bluearcade_active_games%`,
> `%bluearcade_coins_earned%` or `%bluearcade_xp_earned%`.** Those keys were
> never registered and rendered literally in TAB. Use `credits` for BlueArcade's
> in-store currency, `games_played`/`wins`/`kills`/`deaths`/`stars` for stats, and
> see below for **money** (Vault/ExcellentEconomy) and **votes** (VotingPlugin).

How to verify a BlueArcade placeholder yourself in game:

```
/papi parse me %bluearcade_stat_global_wins%
/papi parse me %bluearcade_votes_total%   # returns the literal text = does not exist
```

## Money (ExcellentEconomy via Vault)

`ExcellentEconomy` is installed and configured as the **Vault-primary** economy
(`plugins/ExcellentEconomy/config.yml` → `Integration.Vault.EconomyCurrency: money`).
That makes the plain Vault placeholder the reliable one to use anywhere:

| Placeholder | Meaning |
|-------------|---------|
| `%vault_eco_balance_formatted%` | Player's `money` balance, formatted with the currency's `Format` |
| `%vault_eco_balance%` | Raw, unformatted balance |

Players earn `money` for every match via `plugins/BlueArcade3/rewards.yml`
(`excellenteconomy give money {player} <amount> -s`), mirroring the BlueArcade
`credits` they earn for the in-game store.

> ExcellentEconomy also has its own expansion (`%excellenteconomy_...%`), but the
> exact prefix was **not verifiable** from this repo (the jar is not committed).
> Until it is confirmed with `/papi parse me %excellenteconomy_balance_money%`,
> prefer the Vault placeholder above — it is guaranteed to work because Vault is
> loaded and ExcellentEconomy is its economy provider.

## Server-list voting (VotingPlugin + NuVotifier)

Installed. VotingPlugin's expansion registers `%votingplugin_*%` — verified
identifiers:

| Placeholder | Meaning |
|-------------|---------|
| `%votingplugin_total%` | Votes this month |
| `%votingplugin_total_alltime%` | All-time votes |
| `%votingplugin_dailyvotestreak%` | Current daily vote streak |
| `%votingplugin_points%` | Player's voting points |
| `%votingplugin_top_all_1%` | Username of the #1 all-time voter |
| `%votingplugin_top_all_<n>%` | Username of the #n all-time voter (verified up to rank 5 — used by `top_winner`) |
| `%votingplugin_top_all_<n>_position%` | Rank position string of the #n all-time voter |
| `%votingplugin_top_all_<n>_votes%` | All-time vote count of the #n all-time voter |
| `%votingplugin_top_daily_<n>%` | Username of the #n daily voter (leaderboard `_daily` NPCs/holograms) |
| `%votingplugin_top_week_<n>%` | Username of the #n weekly voter |
| `%votingplugin_top_month_<n>%` | Username of the #n monthly voter (`leaderboard_1`–`5`) |
| `%votingplugin_canvote%` | `true`/`false` if the player can vote now |

> **Not PAPI keys:** `Top_Month_`, `Top_Week_` and `Top_Daily_` exist in the
> VotingPlugin jar (`PlaceHolders` class) but are **only used internally to
> format the `/vote top` command output**. They are not registered as
> PlaceholderAPI placeholders and resolve literally if used. Only the
> `top_all_<n>[_position|_votes]` forms (and `top_daily/top_week/top_month` if
> registered at runtime) should be used in configs.

> Ranks above 5 (`%votingplugin_top_all_6%` … `%votingplugin_top_all_10%`, used
> by `leaderboard_3`–`5`) are expected to work since the expansion handles any
> `n`, but have **not been confirmed** — verify in-game with
> `/papi parse --null %votingplugin_top_all_6%` before relying on them.

`/vote` opens the vote GUI (or lists sites). NuVotifier (`plugins/Votifier/`) is
the listener that receives the votes.

> BlueArcade's own `/ba vote` is a **different, in-arena** vote (minigame voting
> while waiting in an arena). Do not confuse the two — the in-arena one is
> provided by BlueArcade and is not usable from the hub.

## Commands

| Command | Effect |
|---------|--------|
| `/papi list` | list installed expansions |
| `/papi ecloud download <expansion>` | download an expansion from the eCloud |
| `/papi reload` | reload PlaceholderAPI and its expansions |
| `/papi parse me <placeholder>` | test a placeholder as yourself |

## Configuration notes

- Expansions live in `plugins/PlaceholderAPI/expansions/` and auto-download from
  the eCloud.
- After installing a new expansion, run `/papi reload` before other plugins can
  use its placeholders.
