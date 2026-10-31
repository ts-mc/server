# DeluxeHub — Lobby Hub Guide

DeluxeHub manages the lobby experience on **TheySix Events**
(`play.theysix.tech`): join items, the server selector, the scoreboard and
the lobby world rules.

## Files

| File | Purpose |
|------|---------|
| `config.yml` | Join items, scoreboard, lobby settings (**empty = defaults**) |
| `commands.yml` | Custom command aliases (**empty = defaults**) |
| `menus/serverselector.yml` | The server-selector GUI (**empty = defaults**) |
| `data.yml` | Runtime data — do not hand-edit |
| `messages.yml` | Player-facing messages (**empty = defaults**) |

## What it handles here

| Feature | Notes |
|---------|-------|
| Join items | lobby items players get on join |
| Server selector | GUI to move between servers |
| Lobby scoreboard | side scoreboard while in the lobby |
| World rules | block break/place/damage in the lobby |

## Commands

| Command | Effect |
|---------|--------|
| `/deluxehub` | reload/manage DeluxeHub |
| `/lobby` | send yourself to the lobby |
| `/setlobby` | set the lobby spawn (staff) |

## Notes

- The minigames have their own join GUI from BlueArcade3 — see
  [`../BlueArcade3/README.md`](../BlueArcade3/README.md).
- Player-facing text uses the brand name **TheySix Events** and IP
  `play.theysix.tech`.
