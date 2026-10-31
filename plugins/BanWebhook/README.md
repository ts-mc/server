# BanWebhook — Ban Notification Guide

BanWebhook sends a Discord (or generic HTTP) webhook notification whenever a
player is banned on **TheySix Events** (`play.theysix.tech`).

## Files

| File | Purpose |
|------|---------|
| `config.yml` | Webhook URL, message format and trigger settings (**empty = defaults**) |

## Configuration

Typical fields to set:

| Field | Meaning |
|-------|---------|
| webhook URL | the Discord webhook to post to |
| message format | text sent, usually including player, staff and reason |
| enabled | master on/off toggle |

## Notes

- The shipped jar is `ban-webhook-1.0.0.jar`.
- Never commit a live webhook URL to this repository — treat it as a secret.
- If bans go through AdvancedBan/AdvancedBanZ, make sure BanWebhook listens to
  that plugin's events.
