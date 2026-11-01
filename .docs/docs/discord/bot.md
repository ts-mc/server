# TheySix Events — Discord Bot Knowledge Base

Reference document for a **Python Discord bot** that answers player and staff
questions in a channel. Ask a question (or run a slash command) and the bot
replies with the matching entry below.

This file is the bot's **single source of truth**. Every answer is derived from
the plugin `README.md` files in [`../../plugins/`](../../plugins/README.md) and
the docs in [`../../docs/`](../../docs/).

> **Keep this in sync.** When a plugin config or command changes, update the
> matching entry here so the bot does not answer with stale information.

---

## Bot design

Two ways to build the "ask a question, get an answer" behaviour. Pick one.

### Option A — Intent/FAQ matcher (recommended, no LLM)

Deterministic, cheap, and never invents an answer.

1. Each section below has a **`Triggers`** line and an **`Answer`** block.
2. Load this file at start-up; split it into sections by `## ` heading.
3. On a message in the `#bot` channel (or a `/ask` slash command), normalise the
   question to lowercase, then score each section by how many `Triggers`
   keywords it contains.
4. Reply with the highest-scoring section's `Answer`. If the best score is `0`,
   reply with the **Fallback** block.

### Option B — LLM + this file as context

Retrieval-augmented: embed each section, retrieve the top 3, and ask an LLM to
answer **only** from those sections. Always pass the retrieved text; never let
the model answer from memory, or it will hallucinate commands and IPs.

> With either option, **never** let the bot answer from the model's own
> knowledge. This server's commands (`/ts`, `/ba`) and values are specific.

### Minimal skeleton

```python
import discord, re, unicodedata

DOC = "docs/discord/bot.md"

def load_sections(path):
    text = open(path, encoding="utf-8").read()
    sections = []
    for block in re.split(r"\n(?=## )", text):
        head = block.splitlines()[0].lstrip("# ").strip()
        if head.lower() in {"bot design", "fallback", "server facts"}:
            continue
        triggers = re.search(r"\*\*Triggers:\*\s*(.+)", block)
        triggers = [t.strip().lower() for t in triggers.group(1).split(",")] if triggers else []
        sections.append({"title": head, "triggers": triggers, "body": block.strip()})
    return sections

SECTIONS = load_sections(DOC)

def answer(question: str) -> str:
    q = unicodedata.normalize("NFKC", question).lower()
    best, score = None, 0
    for s in SECTIONS:
        n = sum(1 for t in s["triggers"] if t and t in q)
        if n > score:
            best, score = s, n
    return best["body"] if best else FALLBACK
```

---

## Server facts

| Question | Answer |
|----------|--------|
| Server name | **TheySix Events** |
| Java | `play.theysix.tech` |
| Bedrock | `bedrock.theysix.tech:25832` |
| MOTD | `TheySix Events` |
| Version | Paper 1.21.11 (Java 21) |
| Website | <https://theysix.tech> (events: <https://events.theysix.tech>) |
| Discord invite | <https://discord.gg/theysix> |
| Business email | `theysixyt.business@gmail.com` |
| Arenas | 6 (`PG-01` … `PG-06`) |
| Minigames | 19 (see "Minigames") |

**Triggers:** server, ip, address, connect, join server, port, version, motd, website, discord invite

**Answer:**

```
**TheySix Events**
Java: `play.theysix.tech`
Bedrock: `bedrock.theysix.tech:25832`
Version: Paper 1.21.11 — supports 1.20.3+ via ViaVersion
Website: https://theysix.tech
Events: https://events.theysix.tech
Discord: https://discord.gg/theysix
```

---

## Minigames

The full list and rules are in [`../minigames-list.md`](../minigames-list.md).
There are **19 games across 6 arenas**.

| Category | Games |
|----------|-------|
| Movement / Parkour | Race · Bridge Race · Fast Zone · TNT Run · Traffic Light · Minefield |
| Elimination / Combat | All Against All · One In The Chamber · Snowball Fight · Knockback · Exploding Sheep · Run From The Beast |
| Party / Rhythm | Block Party · Chairs · Spleef · Splegg · Red Alert · TNT Tag · Water Well |

**Triggers:** minigame, minigames, games, game list, what can i play, rotation, modes, gamemodes

**Answer:**

```
**19 minigames** across 6 arenas:

**Movement** — Race, Bridge Race, Fast Zone, TNT Run, Traffic Light, Minefield
**Combat** — All Against All, One In The Chamber, Snowball Fight, Knockback, Exploding Sheep, Run From The Beast
**Party** — Block Party, Chairs, Spleef, Splegg, Red Alert, TNT Tag, Water Well

Vote for the next one with `/ba vote` in the lobby.
Full list: https://github.com/…/blob/main/docs/minigames-list.md
```

---

## How to play / player commands

Player commands use `/ba`, or the friendly aliases `/ts` and `/tse`.

**Triggers:** how to play, play, join game, join, command, commands, quickjoin, vote, leave, party, stats, tops, achievements, level, store, credits, help

**Answer:**

```
**Playing TheySix Events**

- `/ba join [id|name]` — join a specific arena (or open the filter GUI)
- `/ba quickjoin` — drop into a random arena
- `/ba leave` — leave the current arena
- `/ba vote [minigame]` — vote for the next minigame
- `/ba team [join <team_id>]` — pick a team (team games only)
- `/ba party <list|create|invite|join|…>` — play with friends
- `/ba stats` / `/ba tops` — your stats and the leaderboards
- `/ba achievements` / `/ba level` — progression
- `/ba store` — cosmetics and effects

**Aliases:** type `/ts` or `/tse` instead of `/ba`.
Not sure? Run `/ba` and it lists every player command.
```

---

## Arenas

Six arenas exist: `PG-01` … `PG-06`. Each game has 24 spawns, so an arena holds
**max 24 players**.

**Triggers:** arena, arenas, pg-01, pg-02, pg-03, pg-04, pg-05, pg-06, map, maps, how many players, capacity, max players

**Answer:**

```
**Arenas** — 6 total, named `PG-01` … `PG-06`.

Each arena's games define 24 spawn points, so an arena can hold up to
**24 players**. Team size depends on how many players the arena has — it is
not fixed to 1v1, 2v2 or 5v5.

Join one with `/ba join PG-01` (or any `PG-0N`), or use `/ba quickjoin`.
```

---

## Ranks / permissions / staff

**Triggers:** rank, ranks, group, groups, permission, permissions, staff, staffteam, admin, mod, helper, vip, prefix, how to become staff, apply

**Answer:**

```
**Ranks:** owner, manager, admin, headmoderator, moderator, helper, staffteam, vip, default.

- Staff groups are managed in LuckPerms; `staffteam` is the staff tab group.
- Players start in `default`, which can already play, use stats, vote and
  manage a party.
- Want to join the team? Watch for a recruitment post in Discord — applications
  open periodically. Never DM staff to ask.
```

---

## Chat tags / glow / cosmetics

**Triggers:** cosmetic, cosmetics, hat, pet, particle, balloon, gadget

**Answer:**

```
**Cosmetics & flair**

- `/cosmetics` — browse hats, gadgets, pets, particles, balloons and more
- `/ba store` — the minigames store for event cosmetics and effects

Most cosmetics are unlocked via ranks or credits.
```

---

## Economy / credits / levels

**Triggers:** economy, money, balance, credits, pay, level, xp, reward, rewards, vote reward, store, buy, purchase

**Answer:**

```
**Credits & progression**

- `/balance` — check your credits
- `/pay <player> <amount>` — pay another player
- `/ba credits pay <player> <amount>` — pay credits in-game
- `/ba level` — your level and XP progress
- `/ba store` — spend credits on cosmetics and effects

You earn credits and XP from playing, voting and daily rewards. Stats, credits
and levels are **saved** — they are never reset.
```

---

## Voting

**Triggers:** vote, voting, choose game, pick game, next game

**Answer:**

```
Use `/ba vote` once you're in an arena lobby to vote for the next minigame.
The mode with the most votes wins. Some games are only available in certain
arenas.
```

---

## Parties / playing with friends

**Triggers:** party, friend, friends, invite, group up, play together, team up

**Answer:**

```
**Parties**

- `/ba party create` — start a party
- `/ba party invite <player>` — invite someone
- `/ba party join <player>` — join a party
- `/ba party list` — see who's in it

Party size limits depend on your rank.
```

---

## Punishments / bans / mutes

**Triggers:** ban, banned, mute, muted, kick, warn, warning, punishment, appeal, unban, why am i banned

**Answer:**

```
**Punishments** are handled by AdvancedBan: bans, tempbans, mutes, kicks and
warnings.

- See the full decision on the ban screen when you try to join.
- **Appeals:** post in the appeals channel with your username and the reason
  shown on screen. Do not DM staff.
- Ban evading with an alt account makes the punishment longer.
```

---

## Reporting a player / bug

**Triggers:** report, reporting, report player, report bug, bug, bug report, hacker, cheating, exploit, issue

**Answer:**

```
**Report a player or a bug**

Post in the bug-report / report channel with:
1. The game and arena (e.g. "Spleef in PG-02").
2. Roughly when it happened, with your timezone.
3. What you expected vs. what happened.
4. A screenshot or video if you have one.

For cheaters, a clip is the fastest way for staff to act.
Please **don't** DM reports — keeping them in the channel is how patterns are
spotted.
```

---

## Maintenance / downtime / server offline

**Triggers:** offline, down, maintenance, downtime, restart, server not working, cant connect, connection refused, lag, lagging, tps

**Answer:**

```
**Server status**

- If the server is offline, check the announcements channel first — scheduled
  maintenance is always announced in advance.
- Maintenance is posted **24 hours ahead**, **1 hour ahead** and again when the
  server is back online.
- Stats, credits and levels are **not** reset by maintenance.
- Still can't connect and there's no announcement? Post in the help channel.
```

---

## Disguises / skins / NPCs

**Triggers:** disguise, morph, skin, skins, npc, hologram

**Answer:**

```
- `/disguise <entity>` — disguise as a mob (staff/permission only)
- `/skin set <name>` — change your Minecraft skin
- `/cosmetics` — in-game cosmetic disguises and morphs for players
```

---

## Rules / conduct

**Triggers:** rules, conduct, allowed, not allowed, toxic, harassment, team kill, cheating, alts

**Answer:**

```
**Conduct**

Fine — trash talk in the spirit of the game, aggressive play, asking for help.

Not fine — slurs or harassment, team-killing as a joke, alt accounts to farm stats, rage-quitting to dodge a loss.

The moderation ladder applies (warnings first, then bans).
```

---

## Staff internal — quick tips

**Triggers:** internal, staff only, how do i, admin command, set up arena, arena wiring, setmainlobby

**Answer:**

```
**Staff quick reference** (not for public channels)

- `/ba setmainlobby` — set the minigames main lobby (required before players
  can join; without it they get "There is no spawn configured").
- `/baa help` — admin help.
- `/baa arena <id> info` — inspect an arena.
- `/baa arena <id> setlobby|setname|…` — configure an arena.
- Arenas are **not** auto-discovered — each must be registered once in game.
- `/tsa` is the admin alias for `/baa`.
- Full instructions: `docs/setup-guide.md` → *Arena wiring*.
```

---

## Fallback

When no section matches, reply with:

```
I don't have an answer for that yet. 🤔

Try asking about: **IP / connect · minigames · commands · arenas · ranks ·
cosmetics · credits · voting · parties · bans · reports · maintenance**

Still stuck? A staff member can help in the help channel.
```

---

## DiscordSRV channels

The bot and DiscordSRV share this server. Keep them separate:

| Channel | ID | Purpose |
|---------|-----|---------|
| `global` | `1540386719176859701` | in-game chat ↔ Discord chat — **players see this** |
| console | `1540706810862772254` | server console output |

**Triggers:** channels, channel ids, discordsrv, global chat, staff chat

**Answer:**

```
The Discord ↔ Minecraft bridge uses two channels:
- **global chat** — relays in-game chat both ways.
- **console** — server output, staff-eyes only.

Note: anything posted in the global chat channel is visible to players in game.
```

---

## Related docs

| Doc | Use for |
|-----|---------|
| [`README.md`](README.md) | Announcements hub + pre-flight checklist |
| [`announcements.md`](announcements.md) | Ready-to-post announcement examples |
| [`templates.md`](templates.md) | Bare `{placeholder}` templates |
| [`../../docs/minigames-list.md`](../../docs/minigames-list.md) | Every game, id, mode and rules |
| [`../../docs/setup-guide.md`](../../docs/setup-guide.md) | Arena wiring and staff setup |
| [`../../docs/luckperms-setup.md`](../../docs/luckperms-setup.md) | Ranks and permission nodes |
| [`../../plugins/README.md`](../../plugins/README.md) | Plugin index — every plugin guide |
