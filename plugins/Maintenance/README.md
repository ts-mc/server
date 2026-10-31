# Maintenance — Maintenance Mode Guide

Maintenance puts the server into maintenance mode on **TheySix Events**
(`play.theysix.tech`), letting only whitelisted players join while you
work.

## Files

| File | Purpose |
|------|---------|
| `config.yml` | Maintenance settings (**empty = defaults**) |
| `language-en.yml` | English kick-screen text (**empty = defaults**) |
| `language-ad.yml` | Advertisement text shown during maintenance (**empty = defaults**) |
| `WhitelistedPlayers.yml` | Players allowed in during maintenance |

## Commands

| Command | Effect |
|---------|--------|
| `/maintenance on` | enable maintenance mode |
| `/maintenance off` | disable maintenance mode |
| `/maintenance status` | show current state |
| `/maintenance whitelist add <player>` | allow a player through |
| `/maintenance reload` | reload the config |

## Notes

- The shipped jar is `Maintenance-4.0.1.jar`.
- Players without permission see the text from `language-en.yml`; edit it to use
  the **TheySix Events** branding and `play.theysix.tech` address.
- Advertise scheduled maintenance in Discord — see
  [`docs/discord/announcements.md`](../../docs/discord/announcements.md).
