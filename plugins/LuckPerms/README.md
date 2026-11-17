# LuckPerms — Permissions Guide

LuckPerms provides permissions, groups and prefixes for **TheySix Events**
(`play.theysix.tech`).

## Files

| File | Purpose |
|------|---------|
| `config.yml` | LuckPerms settings (empty — defaults are used; data lives in the H2 database next to it) |

> The config is empty because the server uses the defaults. Group/prefix data is
> stored in the LuckPerms H2 database, not in a YAML file.

## Groups on this server

| Group | Weight | Notes |
|-------|--------|-------|
| `owner` | 1000 | full access, includes `theysixevents.*` and legacy `bluearcade.admin` |
| `manager` | 950 | management tier |
| `admin` | 900 | includes `theysixevents.admin.*` and legacy `bluearcade.admin` |
| `headmoderator` | 850 | senior staff |
| `moderator` | 800 | moderation (kick/stop/setspawn) |
| `helper` | 750 | junior staff |
| `staffteam` | 700 | staff tab group — drives the staff scoreboard in TAB |
| `vip` | 400 | paid rank |
| `voter` | 300 | voter rank |
| `default` | — | `theysixevents.play`, `.stats`, `.achievements`, `.level`, `.store`, `.party.*`, `.vote`, `.join`, `.quickjoin`, `.leave` |
| `votelb-daily` / `votelb-weekly` / `votelb-monthly` | 1 | display-only; `see` / `viewhologram` for one vote-leaderboard period. Created by `/votelb-install`. Not a player rank. |

## Legacy permission node

The core minigames jar still checks the legacy node `bluearcade.admin` for the
in-game admin bypass. It is granted to `owner`, `admin` and `moderator` alongside
the new `theysixevents.*` tree. When the core jar is recompiled/rebranded, these
grants can be removed.

## Staff scoreboard placeholders

TAB's `staffteam` scoreboard uses `%staffonline%`, which counts players holding
the TAB permission **`tab.staff`** — not the LuckPerms group. Grant `tab.staff`
to the staff track (helper → owner) for the count to be accurate.

## Commands

| Command | Effect |
|---------|--------|
| `/lp user <player> parent add <group>` | add a player to a group |
| `/lp user <player> permission set <node> true` | grant a permission node |
| `/lp group <group> permission set <node> true` | grant a node to a group |
| `/lp group <group> setweight <n>` | set a group's weight |
| `/lp editor` | open the web editor |

See [server operations](../../OPERATIONS.md) for current permission and integration
procedures. Historical rebuild instructions are not authoritative.
