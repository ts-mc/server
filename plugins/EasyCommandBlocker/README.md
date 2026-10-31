# EasyCommandBlocker — Command Visibility Guide

EasyCommandBlocker hides or blocks commands from players on **TheySix Events**
(`play.theysix.tech`). It hides the upstream minigames command names
(`/arcade`, `/bluearcade`, `/barcadea`, …) so the old branding is never visible.

## Files

| File | Purpose |
|------|---------|
| `config.yml` | Blocked command list and response message/title (**empty = defaults**) |

## Blocked commands on this server

| Command | Reason |
|---------|--------|
| `/plugins` | hide the plugin list |
| `/version`, `/about` | hide server internals |
| `/arcade`, `/bluearcade`, `/barcade` | upstream minigames branding |
| `/arcadeadmin`, `/bluearcadeadmin`, `/barcadea` | upstream admin branding |

Players should use the friendly aliases `/ts`, `/tse` and `/tsa` — see
[`../MyCommand/README.md`](../MyCommand/README.md).

## Notes

- The shipped jar is `EasyCommandBlocker-1.18.1.jar`.
- Blocked commands reply with a custom message and/or title, set in `config.yml`.
- For hide-without-blocking behaviour, see the CommandBlocker recommendation in
  [`docs/setup-guide.md`](../../docs/setup-guide.md).
