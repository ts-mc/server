# ViaVersion — Version Support Guide

ViaVersion lets newer Minecraft clients join older servers and — together with
ViaBackwards — lets older clients join the Paper 1.21.11 server running
**TheySix Events** (`play.theysix.tech`).

## Files

| File | Purpose |
|------|---------|
| `config.yml` | ViaVersion settings (**empty = defaults**) |

## Companion plugins

| Plugin | Role |
|--------|------|
| `ViaVersion` | protocol translation for the server's own version |
| `ViaBackwards` | lets older client versions connect |
| `ViaRewind` | adds support for very old client versions |

> The shipped jars are `ViaVersion-5.12.0.jar` and `ViaBackwards-5.12.0.jar`.
> ViaRewind is listed in the plugin inventory but its jar is not present here.

## Commands

| Command | Effect |
|---------|--------|
| `/viaversion list` | list players and their protocol versions |
| `/viaversion autoupload` | toggle error auto-upload |
| `/viaversion reload` | reload the config |

## Notes

- The TAB scoreboard has a `scoreboard-1.20.3+` variant specifically for
  newer clients — see [`../TAB/README.md`](../TAB/README.md).
- Keep Version/Backwards on matching major versions.
