# LibsDisguises — Disguise Guide

LibsDisguises lets players and staff disguise as mobs, blocks and players on
**TheySix Events** (`play.theysix.tech`).

## Files

| File | Purpose |
|------|---------|
| `configs/libsdisguises.yml` | Main config |
| `configs/commands.yml` | Command settings |
| `configs/sounds.yml` | Disguise sounds |
| `configs/protocol.yml` | Protocol settings |
| `configs/displays.yml` | Display entities |
| `configs/events.yml` | Event behaviour |
| `configs/dangerous.yml` / `premium.yml` | Dangerous and premium features |
| `configs/self_disguise.yml` | Self-disguise settings |
| `configs/plugin-commands.yml` | Plugin command aliases |
| `Translations/*.yml` | Disguise names, options and messages |
| `internal/internal.yml` | Runtime data — do not hand-edit |

## Commands

| Command | Effect |
|---------|--------|
| `/disguise <entity>` | disguise as an entity |
| `/disguise player <name>` | disguise as another player |
| `/undisguise` | remove your disguise |
| `/disguisehelp` | list disguises |
| `/libsdisguises reload` | reload the config |

## Notes

- Requires ProtocolLib — see [`../ProtocolLib/README.md`](../ProtocolLib/README.md).
- Restrict disguises to staff/media ranks to prevent abuse; permission nodes are
  `libsdisguises.disguise.*`.
- This is the free build `LibsDisguises-26.8.13-Free.jar`.
