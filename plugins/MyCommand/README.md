# MyCommand — Branding Guide

MyCommand provides custom (unregistered) commands for **TheySix Events**.
The server-facing branding lives in two places.

## Server name references

| File | Field | Value |
|------|-------|-------|
| `config.yml` | comment under `TAB_SUGGESTION_BLOCKER_LISTENER` | `Enabled for TheySix Events` |
| `tabsuggestionblocker.yml` | comment under `COMMAND_LIST` | `TheySix Events player commands + essentials-style basics.` |
| `commands/theysix-aliases.yml` | header / `tsa.permission` | `TheySix Events` / `theysixevents.admin` |

## Alias commands

`commands/theysix-aliases.yml` forwards the `TheySix Events` shorthand to the
underlying `BlueArcade3` (`/ba`) commands:

| Alias | Forwards to | Permission |
|-------|-------------|------------|
| `/ts`, `/tse` | `/ba $args` | everyone |
| `/tsa` | `/baa $args` | `theysixevents.admin` |
| `/votelb-install` | creates LuckPerms `votelb-*` groups | `theysixevents.admin` |
| `/votelb-ensure` | applies saved daily/weekly/monthly view on join | everyone (not tab-listed) |
| `/votelb-daily` `/votelb-weekly` `/votelb-monthly` | swap vote-leaderboard NPC/hologram set | everyone (NPC click) |

The permission node `theysixevents.admin` is hardcoded in the
BlueArcade3 core jar and cannot be changed from config — see
`docs/rebrand-notes.md`.

## Anti-TAB filter

`tabsuggestionblocker.yml` is in `WHITELIST` mode. Players only see commands
listed for their group. Admins holding `mycommand.antitab.admin` bypass the
filter entirely.

If you add new player-facing commands, add them to the appropriate group
(`default`, `vips`, `moderator`) so they appear in tab-completion.