# TheySix Events — Announcements

Ready-to-post announcements for the Discord server. Replace anything in
`{curly braces}` and the `<t:…>` timestamps before posting.

Timestamps: generate them at <https://discordtimestamp.com> or with the formula
`<t:UNIX_SECONDS:F>` (full date/time), `<t:UNIX_SECONDS:R>` (relative, e.g.
"in 2 hours"). Both render in each member's own timezone.

---

## 1. Launch announcement

> # 🎉 TheySix Events is live
>
> The new **TheySix Events** server is open — 19 minigames across 6 arenas,
> running on Paper 1.21.11.
>
> ## How to play
> - `/ba quickjoin` — drop into a random arena
> - `/ba join` — pick an arena yourself
> - `/ba vote` — vote for the next minigame
> - `/ba party` — bring your friends
>
> ## What's in the rotation
> **Movement** — Race · Bridge Race · Fast Zone · TNT Run · Traffic Light · Minefield
> **Combat** — All Against All · One In The Chamber · Snowball Fight · Knockback · Exploding Sheep · Run From The Beast
> **Party** — Block Party · Chairs · Spleef · Splegg · Red Alert · TNT Tag · Water Well
>
> \- **Java:** `play.theysix.tech` · **Bedrock:** `bedrock.theysix.tech:25832`
> \- **Need help in game?** `/ba` shows every player command
>
> See you in the arena. 🏁

---

## 2. Event night (scheduled session)

> # 🎮 Event Night — {DATE}
>
> **When:** <t:{UNIX}:F> (<t:{UNIX}:R>)
> **Where:** TheySix Events — Java `play.theysix.tech`; Bedrock `bedrock.theysix.tech:25832`
> **Format:** {FORMAT} — see below
>
> ## Tonight's lineup
> 1. `{GAME_1}` — {ONE_LINE_RULE}
> 2. `{GAME_2}` — {ONE_LINE_RULE}
> 3. `{GAME_3}` — {ONE_LINE_RULE}
>
> ## How to join
> `{JOIN_COMMAND}` — for example `/ba join PG-01` or `/ba quickjoin`.
> Be in the lobby **5 minutes early**, countdown is {COUNTDOWN} seconds.
>
> ## Rules
> - Play to win, but keep it friendly — no harassment in `/msg`.
> - Leaving mid-game leaves your team a player down. Use `/ba leave` between rounds.
> - Staff decisions are final.
>
> **Vote with `/ba vote` once you're in the lobby.** 🗳️

---

## 3. New games / rotation change

> # 🔄 Rotation update — {DATE}
>
> We've changed what you can vote for.
>
> **Added to the rotation:**
> - `{GAME}` — {ONE_LINE_RULE}
>
> **Removed:**
> - `{GAME}` — {REASON}
>
> **Now available:** {N} games across {M} arenas.
>
> Cast your vote with `/ba vote` in the lobby. Want a game back? Post it in
> #{SUGGESTIONS_CHANNEL}.

---

## 4. Tournament announcement

> # 🏆 {EVENT_NAME} — Tournament
>
> **When:** <t:{UNIX}:F> (<t:{UNIX}:R>)
> **Format:** {TEAM_FORMAT} — e.g. 2v2, best of {N} rounds
> **Arena:** `{ARENA}`
> **Games:** {GAME_LIST}
> **Slots:** {N} teams (first come, first served)
>
> ## Sign up
> 1. React with {EMOJI} below.
> 2. Post your team name and roster in #{SIGNUP_CHANNEL}.
> 3. Sign-ups close <t:{DEADLINE_UNIX}:R>.
>
> ## Prizes
> - 🥇 {PRIZE_1}
> - 🥈 {PRIZE_2}
> - 🥉 {PRIZE_3}
>
> ## Rules
> - One entry per player. Smurfing or alt accounts = disqualification.
> - Check in **15 minutes before** start — no-shows forfeit their slot.
> - Match results are recorded automatically by the server; staff may review
>   stats to settle disputes.
>
> Questions? Ask in #{QUESTIONS_CHANNEL}.

---

## 5. Maintenance / downtime

> # 🔧 Scheduled maintenance — {DATE}
>
> **Downtime:** <t:{START_UNIX}:F> → <t:{END_UNIX}:F>
> (roughly {DURATION} minutes)
>
> We're updating {WHAT} — for example the plugin, arena schematics or the map
> rotation.
>
> ## What this means for you
> - The server will be **offline** for the whole window.
> - Matches in progress will be cut short. Please finish before
>   <t:{START_UNIX}:R>.
> - **Stats, credits and levels are saved** and will not be reset.
>
> We'll post here as soon as the server is back up. Thanks for your patience. 🙏

### Maintenance — 1 hour warning

> ⏰ **Reminder:** maintenance starts <t:{START_UNIX}:R>. Finish your current match
> and head out when the countdown hits — see you on the other side.

### Maintenance — back online

> ✅ **Server is back online.** Maintenance finished at <t:{UNIX}:R>.
>
> {CHANGES_SUMMARY}
>
> Jump back in with `/ba quickjoin`. Report anything broken in #{BUG_REPORTS}.

---

## 6. Game results / leaderboard

> # 🏁 {EVENT_NAME} — Results
>
> **Winners:** {WINNER_LIST}
> **Games played:** {N_GAMES}
> **Rounds:** {N_ROUNDS}
>
> ## Standings
> | # | Player / Team | Points |
> |---|---------------|--------|
> | 🥇 | {NAME} | {POINTS} |
> | 🥈 | {NAME} | {POINTS} |
> | 🥉 | {NAME} | {POINTS} |
>
> ## Career totals
> Check your own numbers with `/ba stats`, or browse the leaderboard with
> `/ba tops`. Levels progress through `/ba level`, and there are achievements to
> hunt with `/ba achievements`.
>
> GG to everyone who played. 👏

---

## 7. Bug reports / known issues

> # 🐛 Known issues — {DATE}
>
> ## Currently affected
> - **{SYMPTOM}** — {PLAYER_IMPACT}. {WORKAROUND_OR_"No workaround yet"}.
> - **{SYMPTOM}** — {PLAYER_IMPACT}.
>
> ## Reporting
> Post in #{BUG_REPORTS} with:
> 1. The game and arena (e.g. "Spleef in PG-02").
> 2. Roughly when it happened, with your timezone.
> 3. What you expected vs. what happened.
> 4. A screenshot or video if you have one.
>
> Please **don't** DM staff reports — keeping them in the channel is how we spot
> patterns. 🙌

---

## 8. Looking for staff

> # 🛡️ We're recruiting — {ROLE}
>
> TheySix Events is growing and we need help.
>
> ## The role
> - Moderate in-game and in Discord during event nights.
> - Triage bug reports and reproduce them.
> - Keep an eye on chat during match nights.
>
> ## What we're looking for
> - Reliable availability of roughly {HOURS} hours a week.
> - Comfortable in voice chat; calm under pressure.
> - Knows the minigames — you can explain `/ba vote` without looking it up.
>
> ## How to apply
> Open a ticket in #{APPLY_CHANNEL} with your timezone, availability and why you
> want the role. Applications close <t:{DEADLINE_UNIX}:R>.
>
> We read every application, but we can't reply to all of them.

---

## 9. Rules / conduct reminder

> # 📋 A reminder on conduct
>
> A few reports came in over the last event. Quick refresher:
>
> **Fine**
> - Trash talk in the spirit of the game — "gg", "you got me on that one".
> - Playing aggressively. It's a minigame server.
> - Asking for help in #{HELP_CHANNEL}.
>
> **Not fine**
> - Slurs, harassment, or anything aimed at a person rather than a play.
> - Team-killing or sabotaging your own side as a joke.
> - Alt accounts to farm stats or credits.
> - Rage-quitting every round then rejoining to dodge a loss.
>
> Violations are handled by the existing moderation ladder — warnings first,
> then bans. Full rules: #{RULES_CHANNEL}.
>
> Reminder: stats reset is **not** a thing, so credit farming just wastes
> everyone's time. 🚫
