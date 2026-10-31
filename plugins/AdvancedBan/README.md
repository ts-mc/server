# AdvancedBan — Punishment Guide

AdvancedBan handles bans, mutes, kicks and warnings on **TheySix Events**
(`play.theysix.tech`).

## Files

| File | Purpose |
|------|---------|
| `config.yml` | AdvancedBan settings (**empty = defaults**) |
| `Messages.yml` | Player-facing punishment messages (**empty = defaults**) |
| `Layouts.yml` | Layouts for the punishment screens (**empty = defaults**) |

## Commands

| Command | Effect |
|---------|--------|
| `/ban <player> [reason]` | ban a player |
| `/tempban <player> <time> [reason]` | temporary ban |
| `/kick <player> [reason]` | kick a player |
| `/mute <player> [reason]` | mute a player |
| `/warn <player> [reason]` | warn a player |
| `/banlist` | list active punishments |
| `/history <player>` | view a player's punishment history |
| `/unban <player>` | remove a ban |

## Notes

- Permission nodes are `advancedban.*` (e.g. `advancedban.command.ban`).
- This folder is the same plugin as [`../AdvancedBanZ/README.md`](../AdvancedBanZ/README.md)
  — AdvancedBanZ is the maintained fork; check which jar is actually loaded
  before editing configs.
