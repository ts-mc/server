# DiscordSRV-Staff-Chat — Staff/Admin Chat Guide

DiscordSRV-Staff-Chat adds separate staff and admin chat channels, bridged
between Minecraft and Discord, on **TheySix Events**
(`play.theysix.tech`).

## Files

| File | Purpose |
|------|---------|
| `staff-chat.config.yml` | Staff/admin chat channel settings (**empty = defaults**) |
| `messages.config.yml` | Staff-chat message formats (**empty = defaults**) |
| `data/staff-chat.data.yml` | Runtime data — do not hand-edit |

## Chat channels

| Channel | Command | Who |
|---------|---------|-----|
| Staff chat | `/staffchat <message>` | staff team |
| Admin chat | `/adminchat <message>` | admins |

Both channels bridge to their own Discord channel; members can read and reply
from Discord.

## TAB scoreboard integration

The TAB `staffteam` scoreboard displays `%staffteam_chat%` and `%admin_chat%`
alongside ping and TPS. These are **not** TAB-native placeholders — they must be
provided by a PlaceholderAPI expansion from this plugin (or another chat
plugin). If they render literally, the expansion is not installed/loaded; see
[`../TAB/README.md`](../TAB/README.md).

## Notes

- Configure the Discord channel ids in `staff-chat.config.yml`, then reload
  DiscordSRV with `/discord reload`.
- Permission nodes follow DiscordSRV's usual `discordsrv.*` naming.
