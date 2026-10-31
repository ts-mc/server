# BUILD PROMPT — TheySix Events Website

> **This file is a complete, self-contained prompt.** Copy everything below the
> rule into an LLM (Claude Code, ChatGPT, Cursor, etc.) and hand it over as-is.
> It needs no other context.
>
> Context: `website-prompt.md` (content facts), `docs/site/README.md` (brand),
> `docs/minigames-list.md` (games), `permissions.md` (ranks/colors),
> `docs/setup-guide.md` (server), `docs/discord/README.md` (announcement rules).

---

# PROMPT

Build the complete, production-ready marketing and information website for
**TheySix Events**, a Minecraft minigame server, to be deployed at
**https://events.theysix.tech**.

It is a **sibling site** to the main brand site at https://theysix.tech.
It must feel like the same company, but it is a different product: that site is a
farm-schematic database, this one is a minigame server hub. Reuse the brand
identity (logo, wordmark, footer shape, glassmorphism, dark canvas). Do **not**
reuse its farm content, its category filter UI, or its cyan-only accent scheme —
this site gets its own richer, multi-hue palette described below.

**The site does not exist yet.** There is currently no DNS record for
`events.theysix.tech`. Building the code is in scope; DNS/hosting setup is
not — but leave a one-paragraph deployment note at the end of your final report
describing what must happen outside the codebase (add the subdomain to the Vercel
project, confirm the `*.ro-mihaiu.xyz` wildcard or explicit record, set the
canonical origin).

---

## 1. Non-negotiable facts

These are authoritative and were verified against the live server configuration.
**Never invent, round, or "improve" any of them.** If a value is not listed here,
do not state it on the site.

| Item | Value |
|------|-------|
| Server display name | TheySix Events |
| Java address | `play.theysix.tech` |
| Bedrock address | `bedrock.theysix.tech` |
| Bedrock port | `25832` |
| Server software | Paper 1.21.11 |
| Java version required | Java 21 |
| Supported client versions | 1.9 and newer (via ViaVersion / ViaBackwards / Geyser) |
| Server capacity (`max-players`) | 100 |
| Per-arena capacity | 24 (hard limit — every game defines exactly 24 spawns) |
| Arenas | 6 — `PG-01` … `PG-06` |
| Minigames | 19 |
| Online mode | `true` (cracked clients cannot join) |
| Discord | https://discord.gg/theysix |
| Business email | theysixyt.business@gmail.com |
| Logo asset | `/logo.gif` — 400×400 animated GIF, **must be reused** |

**The MOTD currently reads:** `<b><gradient:#4cadd0:#b2f9ff>TheySix Events</gradient></b> §8| <color:#FFFFFF>play.theysix.tech</color>`

### Spelling traps — get these exactly right

These are real historical bugs in this project's config. Do not reintroduce them.

- Java is `play.theysix.tech`; Bedrock is `bedrock.theysix.tech:25832`.
- Use `theysix.tech` for general information and `events.theysix.tech` for
  event-specific content.
- Brand casing is `TheySix` (capital T, capital S). The events product is
  `TheySix Events`.

### Commands

Lead with the friendly alias `/ts`. `/ba` also works and players will see it in
older docs, so mention it once as the underlying command.

| Command | Who | Effect |
|---------|-----|--------|
| `/ts` or `/tse` | everyone | Opens the join/filter GUI (aliases `/ba`) |
| `/ts quickjoin` | everyone | Drops you into a random arena |
| `/ts join PG-01` | everyone | Joins one specific arena |
| `/ts leave` | everyone | Leaves the current arena |
| `/ts vote [game\|page <n>]` | everyone | Votes for the next minigame in the lobby |
| `/ts team [join <id>]` | everyone | Picks a team, team-format games only |
| `/ts stats [category] [alltime\|monthly\|yearly] [page]` | everyone | Player stats |
| `/ts tops [category]` | everyone | Leaderboards |
| `/ts achievements` | everyone | Achievement progress |
| `/ts level` | everyone | Level and XP |
| `/ts store` | everyone | Cosmetic store |
| `/ts party <list\|create\|invite\|join\|…>` | everyone | Party management |
| `/balance`, `/pay <player> <amount>` | everyone | Credits via EssentialsX |
| `/tags`, `/glow <colour>`, `/cosmetics` | everyone | Chat tags, glow outline, cosmetics |
| `/vote` | everyone | Server-list voting (see the caveat in §7) |
| `/tsa …` | staff only | Admin alias for `/baa` |

**Do not** publish `/tsa`, `/ba`, `/baa`, or any admin command in player-facing
UI. The underlying minigame plugin still exposes `/arcade` with the alias `/ba`
hardcoded inside its compiled jar, and the plugin's internal data folder is still
named `BlueArcade3` — that is deliberate, load-bearing plumbing. Players must never
see the words "BlueArcade", "Blueva", "Arcade", or "Party Games" anywhere on this
site. Those are upstream vendor names, not brand names.

---

## 2. Design direction — "dark but colourful"

This is the aesthetic brief. Read it as a set of decisions, not suggestions.

**Mood:** a neon-lit esports arena at night. Near-black canvas, so saturated
accents glow off it. High energy, high contrast, never childish. It should feel
like a live broadcast overlay, not a corporate SaaS landing page.

**Rule: dark base, saturated accents.** The background stays near-black
everywhere. Colour is spent on accents, gradients, glows, and category coding —
not on large filled areas. There is no light mode. Do not add a theme toggle; the
brand is dark-only. Do not use white or near-white page backgrounds anywhere.

### Colour tokens (define these as CSS custom properties and use them everywhere)

Base — dark:

| Token | Value | Use |
|-------|-------|-----|
| `--bg` | `#070A12` | deepest layer, page backdrop behind everything |
| `--bg-base` | `#0B0E14` | main page canvas (matches the farms site) |
| `--bg-elev` | `#111726` | raised sections, hero panel base |
| `--surface` | `rgba(19,23,34,0.85)` | glass panels, cards |
| `--surface-hover` | `rgba(24,29,42,0.95)` | glass panel hover |
| `--border` | `#1E293B` | hairline borders |
| `--border-hover` | `#334155` | hairline border on hover |
| `--text` | `#F1F5F9` | body copy |
| `--text-muted` | `#94A3B8` | secondary/meta copy |
| `--text-dim` | `#64748B` | tertiary, disabled |

Brand accents — this is the "colourful" part. These are the real brand colours
taken from the in-game name gradients, so the site and the server look identical:

| Token | Value | Meaning |
|-------|-------|---------|
| `--brand` | `#4CADD0` | primary ice-blue (brand gradient start) |
| `--brand-hi` | `#B2F9FF` | primary highlight (brand gradient end) |
| `--brand-2` | `#9FEBF6` | secondary gradient start |

Signature gradient (use for the H1 and primary CTAs — the brand's most recognisable
move):

```css
background-image: linear-gradient(100deg, #4CADD0 0%, #9FEBF6 45%, #B2F9FF 100%);
-webkit-background-clip: text;
background-clip: text;
color: transparent;
```

Extended spectrum — for category coding, hover states, arena identity, glow orbs,
and chart/leaderboard accents. Each must be used deliberately, not sprayed:

| Token | Value | Role |
|-------|-------|------|
| `--lime` | `#9FFF99` | success, "free", confirmation (the farms site's CTA colour — keep it for success states only) |
| `--emerald` | `#4CAF50` | positive stats, voter rank |
| `--amber` | `#FBBF24` | warnings, timers, second place, warm accent |
| `--orange` | `#FF8A3D` | fire/intensity, TNT and explosion games |
| `--rose` | `#FB7185` | combat/danger |
| `--crimson` | `#FF3B3B` | elimination, bans, hardcore flags |
| `--magenta` | `#F472B6` | VIP, party/fun |
| `--violet` | `#A855F7` | rhythm/music, mysterious |
| `--indigo` | `#818CF8` | headmoderator, arcane |
| `--sky` | `#38BDF8` | movement, speed |
| `--teal` | `#2DD4BF` | utility, secondary motion |
| `--blurple` | `#5865F2` | Discord actions, links to the server's Discord |

### Category colour coding (must be consistent everywhere a game appears)

- **Movement / Parkour** → `--sky`
- **Elimination / Combat** → `--rose`
- **Party / Rhythm** → `--magenta` / `--violet`

Give each of the 6 arenas its own distinct hue so `PG-01` … `PG-06` are visually
distinguishable at a glance in the arena grid, the join buttons, and the command
examples: use `--sky`, `--brand`, `--violet`, `--orange`, `--emerald`, `--crimson`
in that order.

### Glow, glass, depth

- Glassmorphism is the primary surface treatment: translucent panels,
  `backdrop-filter: blur(12px)`, `1px solid rgba(255,255,255,0.08)` borders.
- Colour glow is done with **large blurred radial orbs** behind hero sections —
  e.g. a 384×384 circle at 10% opacity of a category colour, `blur(64px)`,
  positioned partially off-canvas. Use two or three per hero, never more.
- Card hover: lift `-2px`, border brightens to a colour-tinted border (40% alpha
  of that game's category colour), and a coloured shadow appears
  (`0 10px 40px -12px` at 30% of the category colour). Cards must feel physical.
- Focus rings are always visible and always `--brand`. Never remove them.
- Text selection: `background: var(--brand); color: #070A12`.
- Corner radii: `28px` for large panels, `20px` for cards, `12px` for inputs and
  buttons, `999px` for pills. Consistent scale, no arbitrary values.

### Typography

Two families, and **you must actually load them** (see §9, acceptance criterion):

- **Display / UI:** `Inter` (variable weight). Headings `800`, tight tracking
  (`-0.03em`). Body `400/500`.
- **Technical / labels:** `JetBrains Mono`. This is the brand's "system voice" and
  is a load-bearing convention: **every** metadata label, stat caption, badge,
  arena ID, command, version string, and table header is set in JetBrains Mono,
  uppercase, `font-weight: 700`, `letter-spacing: 0.08em`, small size. Think
  server status readouts and esports broadcast lower-thirds.

Type scale (mobile-first): hero H1 `clamp(2.25rem, 7vw, 4.5rem)` / section H2
`clamp(1.75rem, 4vw, 2.5rem)` / card H3 `1.125rem` / body `1rem` / mono label
`0.6875rem`.

### Motion

Restrained, purposeful, and fully reversible.

- Scroll reveal: sections fade up 16px and fade in over 500ms with
  `cubic-bezier(0.16, 1, 0.3, 1)`, staggered ~60ms per child, triggered once at
  15% intersection. Respect `prefers-reduced-motion` — disable the transform and
  keep content visible.
- Hover: `transition: all 200ms` on transform/border/shadow.
- The live server-status dot pulses slowly (3s). When the server is offline, the
  pulse stops and the dot turns `--crimson` — the state change must be obvious.
- A subtle animated gradient sweep on the primary CTA only.
- No parallax, no scroll-jacking, no carousels, no auto-advancing sliders.

---

## 3. Pages to build

Seven routes. All must be responsive, share one shell, and be individually
`title`-tagged and meta-described.

### `/` — Home

1. **Sticky header.** Logo lockup: `/logo.gif` in a rounded tile, `TheySix`
   wordmark, and an `EVENTS` pill beside it in JetBrains Mono (cyan pill on a
   deep-cyan background, matching the farms site's pill treatment). Nav: `Games`,
   `Arenas`, `How to Play`, `Ranks`, `Server`, plus a persistent **Copy IP** button
   as the primary CTA. Mobile: hamburger → stacked panel.
2. **Hero.** Badge in mono uppercase (e.g. `● SERVER ONLINE · PAPER 1.21.11`),
   gradient H1 (`19 MINIGAMES` / `6 ARENAS` / `ONE SERVER` — pick the strongest
   combination, or write a proper headline in the gradient and put the counts in
   the stat strip), one sentence of positioning, then CTAs: **Copy
   `play.theysix.tech`**, **Join the Discord**, **Browse the games**. Behind
   it: two or three large blurred colour orbs.
3. **Live stat strip.** Four tiles, separated by a top border, each with a
   coloured icon and a mono label: players online (live), 19 minigames, 6 arenas,
   100 max players. The online count must be live, not hardcoded (§8).
4. **Games grid.** All 19 games, searchable and filterable by the three
   categories. Each card: game name, category chip (colour-coded), one-line
   description, round length in seconds, win condition, and the copyable command.
   This is the centrepiece of the page — make it genuinely browsable, with live
   text filtering and category tabs that keep the URL shareable
   (`?category=movement#games`).
5. **Arenas.** Six cards for `PG-01` … `PG-06`, each with its distinct hue, its
   format label, and its per-arena command (`/ts join PG-0N`).
6. **How to play.** Numbered steps: add the server, join with 1.20.3+, follow the
   lobby instructions, `/ts quickjoin` or `/ts join PG-0N`, vote for the next game.
7. **Ranks teaser.** The full rank ladder with its real colours (§6).
8. **Rules.** Six rules, honest and short.
9. **Footer.** Four columns (brand + email, Server, Community, Legal), the
   non-affiliation disclaimer, and the Mojang disclaimer.

### `/games` — Full minigame index

All 19 games, each on its own expandable card or detail block with: description,
round length, win condition, typical arena, and the join command. Grouped by
category. This page is the reference — make it scannable, add a "copy command"
affordance per game, and include a compact table view toggle.

### `/arenas` — Arena guide

Six arena sections. For each: arena ID, hue, intended format, capacity (24),
player count range, and which games suit it. State the honest, important caveat
that team size is **not** fixed — it is whatever the arena's player count divides
into, so "5v5" really means "a 10-player arena".

### `/how-to-play` — Full guide

Getting connected (Java and Bedrock), the lobby, arenas, voting, commands table
(the full §1 table), parties, stats and leaderboards, levels and achievements,
cosmetics, credits, and voting. Include a copy-IP block that works on every
command table row.

### `/ranks` — Ranks and progression

The complete ladder with the real gradient colours from §6, what each rank grants,
and how to obtain it. Include the staff-application note: applications open
periodically via a Discord ticket — **never DM staff**.

### `/server` — Technical & server info

Version, Java requirement, supported client range, cross-play support, capacity,
MOTD, online mode, the feature list (cosmetics, holograms, NPCs, Discord chat
bridge, anti-cheat, per-arena region protection), and the DiscordSRV note that
in-game chat is bridged to the Discord `#global` channel.

### Legal pages — `/cookies`, `/tos`, `/privacy`

Real, readable pages — not lorem ipsum, not a single shared blob. The cookie
policy must match the site's actual behaviour (see §9). Both `/tos` and `/privacy`
must carry the mandatory disclaimer, verbatim:

> This server and website are **not** an official Minecraft product. They are
> **not** approved by or associated with Mojang Studios or Microsoft.

Also include a short, plain statement that downloadable or in-game files are
offered as-is for personal, educational, non-commercial Minecraft use.

---

## 4. The 19 minigames — copy these exactly

Descriptions are the player-facing text. Categories and colours per §2.

### Movement / Parkour — `--sky`

| Game | ID | Round | Win condition | Description |
|------|-----|-------|---------------|-------------|
| Race | `race` | 120s | Last standing | Sprint to the finish line. Avoid death blocks and obstacles. First to finish wins. |
| Bridge Race | `bridge_race` | 120s | Last standing | Build a bridge of bricks to the finish line. First to cross wins. |
| Fast Zone | `fast_zone` | 120s | Last standing | Sprint at high speed toward the goal. Dodge walls — one crash ends your run. |
| TNT Run | `tnt_run` | 300s | Last standing | Run across layered sand and TNT floors. Every step collapses the ground behind you. |
| Traffic Light | `traffic_light` | 120s | Last standing | Advance on green, freeze on red. Reach the goal without moving on a red. |
| Minefield | `minefield` | 120s | Last standing | Dash to the safe zone. Pressure plates knock you back instead of killing you. |

### Elimination / Combat — `--rose`

| Game | ID | Round | Win condition | Description |
|------|-----|-------|---------------|-------------|
| All Against All | `all_against_all` | 300s | Last standing | Swords, bows and blocks. Build, break and outlast everyone. |
| One In The Chamber | `one_in_the_chamber` | 120s | Most kills | One bow, one arrow per life. One hit is one elimination. Keep a wooden sword for close range. |
| Snowball Fight | `snowball_fight` | 120s | Most kills | Snowball your way to the last player standing. |
| Knockback | `knockback` | 120s | Most kills | Use the knockback stick to send rivals into the void. |
| Exploding Sheep | `exploding_sheep` | 180s | Last standing | Shear the sheep before their visible fuse runs out — or the floor disappears. |
| Run From The Beast | `run_from_the_beast` | 300s | Last standing | Runners loot chests and cooperate to defeat the beast. One beast against N runners. |

### Party / Rhythm — `--magenta` / `--violet`

| Game | ID | Round | Win condition | Description |
|------|-----|-------|---------------|-------------|
| Block Party | `block_party` | 300s | Last standing | Music plays, then a colour is revealed. Stand on the chosen block before the floor falls. Power-ups spawn. |
| Chairs | `chairs` | 600s | Last standing | Musical chairs. Sit before the timer ends — no seat means elimination. |
| Spleef | `spleef` | 120s | Last standing | Shovel the snow out from under your rivals. Last survivor wins. |
| Splegg | `splegg` | 180s | Last standing | Shoot eggs to destroy the floor beneath your opponents. |
| Red Alert | `red_alert` | 300s | Last standing | Tiles overheat — randomly in chaos mode, or wherever players step in trail mode. |
| TNT Tag | `tnt_tag` | 900s | Last standing | Random players get TNT each round. Pass it on before the fuse hits zero. |
| Water Well | `water_well` | 60s | Last standing | Jump from above into the water well. Land in the water for points. |

The 19 IDs are used by the join commands and are stable — do not invent, re-spell,
or pluralise them.

---

## 5. Arena formats

Six arenas. The format labels below are the **intended** player-facing identity
of each arena.

| Arena | Format | Hue |
|-------|--------|-----|
| `PG-01` | 1v1 Duels | `--sky` |
| `PG-02` | 2v2 | `--brand` |
| `PG-03` | 5v5 | `--violet` |
| `PG-04` | 5v5 Chaos | `--orange` |
| `PG-05` | 10v10 | `--emerald` |
| `PG-06` | Free-For-All | `--crimson` |

Every arena holds up to **24** players. On the `/arenas` page, state plainly that
these formats describe the arena's typical player count and that team size is
worked out at match time from how many players are in the arena — it is not a
hard rule.

---

## 6. Ranks — use these exact gradient colours

These are the live in-game LuckPerms prefixes. Render each rank name as a gradient
span. Preserve the exact hues; they are brand.

| Rank | Colour(s) | How to obtain |
|------|-----------|---------------|
| Owner | `#D78C39` → `#FEF801` (gold) | Staff team |
| Manager | `#F858AD` → `#FE030B` | Staff team |
| Admin | `#FF0000` | Staff team |
| Head Moderator | `#7F3AFF` (violet) | Staff team |
| Moderator | `#6186EB` → `#B2F9FF` (blue) | Staff team |
| Helper | `#4CADB0` → `#B2F9FF` (cyan) | Staff team |
| Staff Team | `#FF0000` | Staff team |
| VIP | `#F47FF7` (magenta) | Booster / perks, apply or purchase |
| Voter | `#4CAF50` (green) | Vote for the server 4+ times in 24 hours |
| Default | `#888888` (grey) | Everyone |

Show them as a ladder, highest rank at the top, each with its permission summary.
Note that `default` already includes playing, stats, voting, leaderboards and
parties — earning a rank adds perks on top, it is not a paywall.

---

## 7. Accuracy and honesty constraints

This is a live community site. Getting a detail wrong sends players to the wrong
command or breaks trust. These constraints are hard requirements.

1. **Never leak internals.** No jar filenames, no plugin folder names, no config
   keys, no permission nodes, no arena ids used as internal database keys, no
   database or file paths. Everything is described in player terms: "join with
   `/ts quickjoin`", not "the arena must be registered via `/baa`".
2. **Never mention BlueArcade, Blueva, Arcade, or Party Games.** See §1.
3. **Voting is only partly live.** Server-list voting (NuVotifier + VotingPlugin)
   is still being configured — the vote listener port has not been verified open
   and no reward site is active yet. Present `/vote` as a thing being rolled out
   and show the Voter rank as an existing reward, but **do not promise specific
   vote rewards, streaks, or reward amounts anywhere.** Do not link to a vote URL.
4. **Do not publish a live events calendar.** There is no reliable event schedule
   yet. If you include an events section, ship it as an honest "no sessions
   scheduled right now — check Discord for announcements" state with a Discord
   CTA. Do not fabricate dates, prize pools, tournament brackets, or sign-up
   deadlines.
5. **Do not invent staff names, player names, testimonials, or statistics.** No
   "10,000 players served" claims. No fake Discord member counts. No review
   quotes.
6. **Distinguish two different things that share the word "vote".** `/ts vote` is
   an in-arena vote for the next minigame and is always available. `/vote` is
   server-list voting on a Minecraft server directory and is being rolled out.
   Never conflate them.
7. **Never claim the server is permanently online.** Live status is queried at
   runtime (§8) and the UI must degrade honestly when the query fails.
8. **No paid service, no checkout, no store page.** The server may be monetised in
   game at some point, but the website must have no payment flow and no pricing
   page.
9. **Online mode is on.** Say so plainly — it explains why some clients get
   rejected, and it is better to be upfront than to have players file bug reports.

---

## 8. Live server status

The header and hero must show **real** player counts, not a hardcoded number.

Implement a status endpoint that queries the Minecraft server at
`play.theysix.tech` using the Server List Ping protocol (handshake
status request → status response, VarInt-prefixed). Requirements:

- **Never called from the browser.** Minecraft server pings do not allow CORS, and
  exposing the query in client JS leaks to anyone. It must be a server-side
  route handler / serverless function, or a build-time fetched JSON file.
- Cache aggressively — 30 to 60 seconds is plenty. A player-count number does not
  need to be live to the second, and pinging on every page view will get you
  rate-limited or blocked.
- On failure or timeout, return a degraded payload. **The UI must show an honest
  unknown state** ("Status unavailable"), never `0` and never a stale number
  presented as live.
- Expose `online`, `max`, and `version` so the stat strip and the status dot can
  both be driven by it.

---

## 9. Technical requirements

**Stack:** Next.js 15 (App Router) + TypeScript + Tailwind CSS. This matches the
sibling farms site, so shared components and brand assets port cleanly. Client
components only where interactivity is needed (game filter, copy-IP button, mobile
nav, cookie banner).

**Fonts — this is the single most important technical detail.** The sibling site
declares `Inter` and `JetBrains Mono` in its Tailwind theme but **never loads
them**, so they silently fall back to `system-ui` and the intended look is lost.
Load them properly with `next/font/google`, expose them as CSS variables, and wire
them into the Tailwind theme. Verify in devtools that the computed `font-family`
is actually Inter and JetBrains Mono, not the fallback. Also ship `font-display:
swap`.

**Assets.** Reuse `/logo.gif` from the sibling site. Icons: `lucide-react` (that
is what the sibling uses). No stock photography, no external image services, no
Unsplash. If a visual is needed, prefer a CSS/SVG composition. Add `width`/`height`
to every image and mark decorative ones `aria-hidden`.

**Meta and SEO — do better than the sibling site, which got these wrong:**
- Per-route `<title>` and meta description.
- `<meta name="theme-color" content="#070A12">`.
- Open Graph + Twitter cards. **Do not use `logo.gif` as the OG image** — crawlers
  generally do not render animated GIFs. Generate a static 1200×630 PNG (the
  wordmark plus the gradient) and use that for OG/Twitter instead.
- `og:site_name` `TheySix Events`, canonical `https://events.theysix.tech`
  on every route.
- Valid JSON-LD. At minimum `WebSite`, and `VideoGame` or `SoftwareApplication`
  for the server. Consider `FAQPage` built from the how-to-play content.
- `sitemap.xml` and `robots.txt`.
- Favicon set: use `/logo.gif` for `icon`, plus a static 512×512 PNG for
  `apple-touch-icon`.

**Cookies.** The site has one cookie and one only: the cookie-banner consent flag
in `localStorage`. Say so accurately in `/cookies`. Do not write a cookie policy
implying analytics or ad tracking you have not built. If you add any analytics,
it must be consent-gated and the policy updated to match — but the default and
recommended choice is none.

**Accessibility — non-negotiable:**
- Semantic landmarks, one `h1` per page, correct heading order.
- Every interactive element reachable and operable by keyboard; the nav dropdown
  must support `group-focus-within`, not hover only.
- Visible focus rings on a `--brand` background.
- The game filter's result count must update an `aria-live="polite"` region.
- Body copy at least 4.5:1 contrast against its background; large display text at
  least 3:1.
- `prefers-reduced-motion` fully honoured.
- Colour must never be the *only* signal — category coding is always paired with
  a text label.

**Performance:** static generation wherever possible, `next/image` for raster
assets, route-level code splitting, and no layout shift from late-loading fonts or
images.

**Copy button.** The IP is the site's single most important conversion action. It
must appear in the header, the hero, and the how-to-play page; copy
`play.theysix.tech`; show a brief confirmation; and degrade to a
selectable fallback if the Clipboard API is unavailable or blocked.

---

## 10. Deliverables

1. The complete Next.js project, type-checking clean, production build passing.
2. All seven routes implemented as specified.
3. The status endpoint, with caching and honest failure handling.
4. `/public` assets, including the static OG image.
5. A `.env.example` if any environment variables are needed.
6. A short `README.md` explaining how to run it, where to put `/logo.gif`, and
   what has to happen outside the codebase to bring the domain up.

## 11. Acceptance checklist

Before reporting done, verify each of these:

- [ ] `npm run build` succeeds with no type errors.
- [ ] DevTools computed `font-family` for body text is Inter and for mono labels
      is JetBrains Mono — not a system fallback.
- [ ] Zero occurrences of "BlueArcade", "Blueva", "Arcade", or "Party Games" in
      the rendered output or the bundle.
- [ ] Zero occurrences of `sparked.host`, `sarked`, or `theysix.ro-mihaiu.xyz` used
      as the events site address.
- [ ] All 19 games present, with correct IDs, round lengths, and categories.
- [ ] All 6 arenas present with their format labels.
- [ ] All 10 ranks present with the exact gradient colours from §6.
- [ ] Copy-IP works in the header, hero, and how-to-play.
- [ ] Status endpoint is server-side, cached, and shows an honest degraded state
      when the ping fails.
- [ ] Every page has a unique title, meta description, and canonical.
- [ ] Static (non-GIF) OG image exists and is referenced.
- [ ] JSON-LD validates.
- [ ] Keyboard-only pass: tab through the header, dropdown, game filter, and
      every copy button. Everything is reachable and visible.
- [ ] Reduced-motion pass: with `prefers-reduced-motion: reduce` emulated, no
      transform animations run and no content is left invisible.
- [ ] Responsive pass at 375px, 768px, 1280px, and 1920px.
- [ ] Contrast check on body text, muted text, and all accent-on-dark
      combinations.
- [ ] No invented statistics, testimonials, staff names, events, or vote rewards
      anywhere in the copy.

In your final report, state what you built, list anything you deliberately left
out, and flag any place where you had to interpret rather than follow the brief.
