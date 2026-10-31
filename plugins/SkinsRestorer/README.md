# SkinsRestorer — Skin Guide

SkinsRestorer lets players change their skin and applies skins to offline-mode
players on **TheySix Events** (`play.theysix.tech`).

## Files

| File | Purpose |
|------|---------|
| `config.yml` | SkinsRestorer settings (**empty = defaults**) |
| `messages.yml` (generated) | Player-facing messages |
| `SkinsRestorer.properties` | Runtime properties |

> Configuration is currently on defaults; the plugin generates its language
> files on first run.

## Commands

| Command | Effect |
|---------|--------|
| `/skin set <name>` | set your skin |
| `/skin clear` | remove your skin |
| `/skin update` | refresh your skin |
| `/skins` | open the skin GUI |
| `/sr reload` | reload the config (staff) |

## Notes

- Skins are fetched from Mojang; online-mode servers get skins automatically.
- The server is `online-mode=true`, so SkinsRestorer is mainly useful for
  players who want to change their skin or for any offline-mode testing.
