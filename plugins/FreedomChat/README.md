# FreedomChat — Chat Guide

FreedomChat controls how chat messages are sent, letting the server use unsigned
chat on modern clients. It runs on **TheySix Events**
(`play.theysix.tech`).

## Files

| File | Purpose |
|------|---------|
| `config.yml` | FreedomChat settings (**empty = defaults**) |

## Notes

- No player-facing config or commands; it works in the background.
- On Paper 1.21.11, chat signing enforcement is normally controlled by
  `enforce-secure-profile` in `server.properties`. FreedomChat is only needed if
  you want to relax those checks independently.
- In-game chat is mirrored to Discord by DiscordSRV — see
  [`../DiscordSRV/README.md`](../DiscordSRV/README.md).
