# AdvancedBanZ — Punishment Guide

AdvancedBanZ is the maintained fork of AdvancedBan. It handles bans, mutes,
kicks and warnings on **TheySix Events** (`play.theysix.tech`).

## Files

| File | Purpose |
|------|---------|
| `config.yml` | AdvancedBanZ settings (**empty = defaults**) |
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

- Permission nodes are `advancedban.*`.
- The shipped jar is `AdvancedBanZ-4.0.1-4.0.1-all.jar`.
- Only one of AdvancedBan / AdvancedBanZ should be active — both use the same
  command names and data layouts.
