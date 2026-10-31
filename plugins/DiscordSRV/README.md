# DiscordSRV — Discord Bridge Guide

DiscordSRV links the Minecraft server to Discord: in-game chat appears in a
Discord channel and vice-versa, and it handles account linking. It runs on
**TheySix Events** (`play.theysix.tech`).

## Files

| File | Purpose |
|------|---------|
| `config.yml` | Main settings — bot token, server id, channels (**empty = defaults**) |
| `synchronization.yml` | Which channels mirror which servers/worlds (**empty = defaults**) |
| `messages.yml` | Player-facing bot messages (**empty = defaults**) |
| `voice.yml` | Voice-channel module settings (**empty = defaults**) |
| `linking.yml` | Account-linking settings (**empty = defaults**) |
| `alerts.yml` | Broadcast/alert settings (**empty = defaults**) |

## Channel setup on this server

| Channel key | Discord | Purpose |
|-------------|---------|---------|
| `global` | `1540386719176859701` | in-game chat ↔ Discord chat |
| console | `1540706810862772254` | server console output |

Configure the bot token with `/discord token <token>` (never commit it to this
repository), then `/discord reload`.

## Commands

| Command | Effect |
|---------|--------|
| `/discord` | show the bot status and link command |
| `/discord link` | get your account-linking code |
| `/discord reload` | reload the DiscordSRV configs |
| `/discord broadcast <message>` | broadcast to the configured channel |

## Related guide

See [`docs/discord/README.md`](../../docs/discord/README.md) for the channel
layout and [`docs/discord/templates.md`](../../docs/discord/templates.md) for
announcement templates.
