# TheySix Events — Discord Announcements

Copy-paste-ready announcements for the TheySix Events Discord server.

## Server details

| Item | Value |
|------|-------|
| Invite | https://discord.gg/theysix |
| Invite (alt) | https://discord.ro-mihaiu.xyz |
| Website | https://theysix.tech (events: https://events.theysix.tech) |
| Minecraft server | Java `play.theysix.tech`; Bedrock `bedrock.theysix.tech:25832` (MOTD: `TheySix Events`) |
| DiscordSRV channels | `global` → `1540386719176859701` (chat), console → `1540706810862772254` |
| Business email | `theysixyt.business@gmail.com` |

> The DiscordSRV bot relays in-game chat to the `global` channel. **Anything you
> post in that channel can go to players in game** — check the target channel
> before posting an internal note.

## Files

| File | Use for |
|------|---------|
| `announcements.md` | Event announcements: launch, new games, tournaments, maintenance, results |
| `templates.md` | Reusable skeletons with `{placeholders}` for recurring posts |
| `bot.md` | Knowledge base for the **Python Discord bot** — ask a question in a channel, get the matching answer |

## Posting rules

1. **One event, one post.** Edit the original message for updates rather than
   posting a follow-up — the `global` channel already carries live chat.
2. **Times include a timezone.** Use `19:00 UTC` and add a Discord timestamp
   (`<t:1730000:F>`) so every member sees it in their own local time.
3. **Use `@here`, never `@everyone`.** `@everyone` pings offline members too and
   is reserved for a same-day emergency (unscheduled downtime).
4. **Announce the real state of the server.** Until the core plugin jar and arena
   wiring are done, events cannot run — do not announce a date before
   `docs/setup-guide.md` → *Arena wiring* is complete.
5. **Gender-neutral, no leaked internals.** Announcements describe what a player
   does (`/ba join`), not jar names, arena ids or file paths.

## Pre-flight checklist (before announcing any event)

- [ ] Every game in the rotation has been played once end-to-end, and its arena
      reset cleanly afterwards.
- [ ] `/ba setmainlobby` is set — otherwise joiners get
      `There is no spawn configured for TheySix Events`.
- [ ] Each arena has real waiting-lobby coordinates (they currently all share one
      position — correct only if the arena worlds are mirrored builds).
- [ ] `max_players` per arena does not exceed its 24 spawns.
- [ ] The date/time is written with an explicit timezone **and** a `<t:…>`
      timestamp.
- [ ] A staff member is available to moderate during the whole event window.

## Maintenance window wording

Use the maintenance template in `templates.md`. Announce downtime **at least
24 hours ahead**, again **1 hour ahead**, and post a **back-up confirmation** when
the server is live again — three posts, not one.
