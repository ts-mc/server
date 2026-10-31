# TheySix Events — Announcement Templates

Bare skeletons with `{placeholders}`. Copy, fill, delete unused blocks, post.
For finished examples see `announcements.md`.

## Placeholder reference

| Placeholder | Example | Notes |
|-------------|---------|-------|
| `{DATE}` | `Saturday 20 September` | Human-readable, for the title |
| `{UNIX}` | `1758380400` | Unix seconds for `<t:{UNIX}:F>` |
| `{START_UNIX}` / `{END_UNIX}` | Use for maintenance windows |
| `{DEADLINE_UNIX}` | Sign-up / application deadlines |
| `{GAME}`, `{GAME_1}` … | `Spleef` | Display name, not the internal id |
| `{GAME_LIST}` | `Spleef, Splegg, Knockback` | Comma-separated |
| `{ARENA}` | `PG-02` | As players see it |
| `{FORMAT}` | `2v2` / `free-for-all` | |
| `{TEAM_FORMAT}` | `2v2, best of 3` | |
| `{JOIN_COMMAND}` | `/ba join PG-01` | Or `/ba quickjoin` |
| `{ONE_LINE_RULE}` | `Shovel the snow out from under rivals.` | Keep to one line |
| `{N}`, `{M}` | `19`, `6` | Numbers |
| `{PRIZE_1}` … | `500 credits + Champion role` | |
| `{EMOJI}` | `✅` | Sign-up reaction |
| `{ROLE}` | `Moderator` | Recruiting |
| `{HOURS}` | `5` | Weekly commitment |
| `{DURATION}` | `30` | Minutes of downtime |
| `{WHAT}` | `the plugin update` | Maintenance reason |
| `{CHANGES_SUMMARY}` | `New rotation, faster resets.` | Post-maintenance |
| `{SYMPTOM}` | `Chairs occasionally ends with no winner` | Bug reports |
| `{PLAYER_IMPACT}` | `The round ends early` | Bug reports |
| `{REASON}` | `Causing crashes on Bedrock clients` | Removal reason |

---

## Event night

```
# 🎮 Event Night — {DATE}

**When:** <t:{UNIX}:F> (<t:{UNIX}:R>)
**Where:** TheySix Events — Java `play.theysix.tech`; Bedrock `bedrock.theysix.tech:25832`
**Format:** {FORMAT}

## Tonight's lineup
1. `{GAME_1}` — {ONE_LINE_RULE}
2. `{GAME_2}` — {ONE_LINE_RULE}
3. `{GAME_3}` — {ONE_LINE_RULE}

## How to join
`{JOIN_COMMAND}` — be in the lobby 5 minutes early.

## Rules
- Keep it friendly — no harassment in `/msg`.
- Use `/ba leave` between rounds, not mid-game.
- Staff decisions are final.

**Vote with `/ba vote` once you're in the lobby.** 🗳️
```

## Rotation change

```
# 🔄 Rotation update — {DATE}

**Added:**
- `{GAME}` — {ONE_LINE_RULE}

**Removed:**
- `{GAME}` — {REASON}

**Now available:** {N} games across {M} arenas.

Vote with `/ba vote` in the lobby. Requests → #{SUGGESTIONS_CHANNEL}.
```

## Tournament

```
# 🏆 {EVENT_NAME} — Tournament

**When:** <t:{UNIX}:F> (<t:{UNIX}:R>)
**Format:** {TEAM_FORMAT}
**Arena:** `{ARENA}`
**Games:** {GAME_LIST}
**Slots:** {N} teams

## Sign up
1. React with {EMOJI} below.
2. Post your team name + roster in #{SIGNUP_CHANNEL}.
3. Closes <t:{DEADLINE_UNIX}:R>.

## Prizes
- 🥇 {PRIZE_1}
- 🥈 {PRIZE_2}
- 🥉 {PRIZE_3}

## Rules
- One entry per player. Alt accounts = disqualification.
- Check in 15 minutes before start. No-show = forfeit.
- Results are recorded automatically; staff review stats to settle disputes.

Questions → #{QUESTIONS_CHANNEL}.
```

## Maintenance — advance notice

```
# 🔧 Scheduled maintenance — {DATE}

**Downtime:** <t:{START_UNIX}:F> → <t:{END_UNIX}:F> (~{DURATION} min)

Updating {WHAT}.

## What this means
- Server is **offline** for the whole window.
- Matches in progress will be cut short — finish before <t:{START_UNIX}:R>.
- **Stats, credits and levels are saved.**

We'll post when it's back up. 🙏
```

## Maintenance — 1 hour warning

```
⏰ **Reminder:** maintenance starts <t:{START_UNIX}:R>. Finish your match and head out.
```

## Maintenance — back online

```
✅ **Back online.** Maintenance finished <t:{UNIX}:R>.

{CHANGES_SUMMARY}

`/ba quickjoin` to jump in. Broken? Post in #{BUG_REPORTS}.
```

## Results

```
# 🏁 {EVENT_NAME} — Results

**Winners:** {WINNER_LIST}
**Games:** {N_GAMES} · **Rounds:** {N_ROUNDS}

| # | Player / Team | Points |
|---|---------------|--------|
| 🥇 | {NAME} | {POINTS} |
| 🥈 | {NAME} | {POINTS} |
| 🥉 | {NAME} | {POINTS} |

Check yours with `/ba stats` · leaderboard via `/ba tops` · achievements via
`/ba achievements`. GG 👏
```

## Known issues

```
# 🐛 Known issues — {DATE}

## Currently affected
- **{SYMPTOM}** — {PLAYER_IMPACT}. {WORKAROUND}

## Reporting
Post in #{BUG_REPORTS} with:
1. Game + arena (e.g. "Spleef in PG-02")
2. When it happened, with your timezone
3. Expected vs. actual
4. Screenshot/video if possible

Please don't DM reports — keeping them in the channel is how we spot patterns.
```

## Recruiting

```
# 🛡️ We're recruiting — {ROLE}

## The role
- Moderate in-game and in Discord during event nights.
- Triage and reproduce bug reports.

## Looking for
- ~{HOURS} hours a week, reliably.
- Comfortable in voice chat.
- Knows the minigames.

## Apply
Open a ticket in #{APPLY_CHANNEL} with timezone + availability. Closes
<t:{DEADLINE_UNIX}:R>.
```

## Conduct reminder

```
# 📋 A reminder on conduct

**Fine:** trash talk in the spirit of the game, aggressive play, asking for help.

**Not fine:** slurs, harassment, team-killing as a joke, alt accounts for stats,
rage-quitting to dodge a loss.

Moderation ladder applies. Full rules → #{RULES_CHANNEL}.
```
