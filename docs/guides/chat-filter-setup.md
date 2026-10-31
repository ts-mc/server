# Chat Filtering Setup — TheySix Events

This is a two-part setup:
1. **DiscordSRV alert** — sends a Discord embed to staff whenever a player's chat message contains a blacklisted word. **Already configured** in `plugins/DiscordSRV/alerts.yml`.
2. **Star-replacing filter (censor)** — swaps the bad word for `*` characters in-game. This needs a plugin (see below).

---

## Part 1 — DiscordSRV Alert (done, needs one edit)

The alert lives in `plugins/DiscordSRV/alerts.yml` under `Alerts:`. It triggers on Paper's `AsyncChatEvent` and regex-matches the message text.

### What YOU need to do

Open `plugins/DiscordSRV/alerts.yml`, find the profanity alert block, and change:

```yaml
Channel: staff
```

…to the **channel key** (not the channel name on Discord!) from your `plugins/DiscordSRV/channels.yml`. For example if you have:

```yaml
Channels:
  staff: "123456789012345678"   # channel ID
```

…then `Channel: staff` is already correct. If your key is something like `staff_alerts`, change the line to `Channel: staff_alerts`.

### Changing the word list

Edit the regex in the `Conditions:` line of the alert. Words are separated by `|`. Example — add "spam" and "scam":

```
'(bitch|bastard|shit|scam|spam)'
```

Keep the whole thing wrapped in single quotes and end with `')` — do not break the quotes.

---

## Part 2 — In-game star censor (needs a plugin)

DiscordSRV only *alerts* — it does not censor in-game. To swap profanity for stars (`he*l*o` style), you need a chat-filter plugin. Since we're not adding `.jar` files automatically, pick **one** of these yourself:

| Plugin | How it censors | Notes |
|---|---|---|
| **ChatRedactor** | `*` per letter | lightweight, config-based word list |
| **ProChat** (free version) | `*` or custom char | also handles caps/ads |
| **Chatty** | configurable replacement | also gives you chat channels/formats |
| **ChatGuard** | `*` per letter | has Discord webhook + staff alerts too |

**Recommended: ChatGuard** — it does BOTH the in-game star censor AND can push alerts, so you could even drop the DiscordSRV alert later if you want one plugin doing everything.

### Once installed

1. Drop the `.jar` into `plugins/`, restart the server.
2. Edit its generated config (`plugins/<PluginName>/config.yml`) and paste in the **same word list** you used in `alerts.yml` above (keep them in sync — one source of truth: this file's word list section).
3. Set the replacement character to `*`.

### Keeping the two lists in sync

Both the DiscordSRV alert and the censor plugin need the same words. Keep the master list here:

```
bitch, bastard, shit, crap, damn, dumbass, asshole, jackass,
motherfucker, fucker, fucking, fuck, retard, retarded, cunt,
whore, slut, nigger, faggot, dick, cock, penis, pussy, wtf, stfu
```

---

## Testing

1. Log in-game and type one of the blacklisted words.
2. In-game it should appear as `****`.
3. On Discord, your staff channel should get a red embed with the player name, world, and message.

If the Discord alert doesn't fire: check `logs/latest.log` for `[DiscordSRV]` errors — usually the `Channel:` name doesn't match `channels.yml`, or the bot lacks permission to post in that channel.
