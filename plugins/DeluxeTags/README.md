# DeluxeTags — Chat Tags Guide

DeluxeTags provides the selectable chat tags shown next to player names on
**TheySix Events** (`play.theysix.tech`).

## Files

| File | Purpose |
|------|---------|
| `config.yml` | Tag definitions and settings (**empty = defaults**) |
| `messages.yml` | Player-facing messages (**empty = defaults**) |
| `userdata/player_tags.yml` | Runtime data: which players own which tags — do not hand-edit |

## How it is used here

- TAB's `groups.yml` calls `%deluxetags_tag%` in the tab suffix and the tag line
  of the scoreboard.
- Players with permission can pick a tag with `/tags`.

## Commands

| Command | Effect |
|---------|--------|
| `/tags` | open the tag selector |
| `/tags <tag>` | equip a tag |
| `/tags clear` | remove your tag |

## Notes

- Permission nodes are `deluxetags.tag.<tag>`.
- Tags are awarded via permissions (LuckPerms) rather than purchased in most
  setups; configure the exact nodes in `config.yml`.
