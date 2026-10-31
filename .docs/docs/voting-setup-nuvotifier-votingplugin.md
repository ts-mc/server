# Voting Setup — NuVotifier + VotingPlugin

A hands-on guide for getting **server-list voting** working on **TheySix Events**
(`play.theysix.tech`), from an empty config to verified votes with
rewards.

> **This guide also flags two problems in the current live config.** Read
> [section 0](#0-two-problems-in-the-current-config-read-first) before you start
> — one of them is why voting does not work right now.

---

## What the two plugins actually do

They are two halves of one system, and mixing them up is the source of most
"voting is broken" confusion.

| Plugin | Role | Think of it as |
|--------|------|----------------|
| **NuVotifier** (`plugins/Votifier/`) | The **listener**. Opens a port, accepts a TCP connection from a voting website, verifies the signature, and fires a `VotifierEvent`. It does **not** give rewards and has no concept of a "vote site". | The postbox |
| **VotingPlugin** (`plugins/VotingPlugin/`) | The **vote listener**: it consumes that `VotifierEvent`, decides which configured site the vote belongs to, applies the vote delay, runs the rewards, and stores the totals/streaks/points. | The person who reads the post |

**NuVotifier alone does nothing visible.** VotingPlugin alone receives nothing.
Both must be installed, and VotingPlugin will not do anything until NuVotifier
hands it an event.

<details>
<summary>Do not confuse this with BlueArcade's <code>/ba vote</code></summary>

BlueArcade has its own in-arena map/mode vote (`plugins/BlueArcade3/items.yml` →
`waiting_room_items.vote`). That is a **different feature** — it only works while
you are in an arena waiting room. It has nothing to do with server-list voting
and needs neither plugin. See `.docs/docs/fix-log-2026-09-22.md` §2.
</details>

### How a vote travels

```
   ┌─────────────┐   TCP + RSA-signed payload   ┌──────────────┐
   │ voting site │ ───────────────────────────► │  NuVotifier  │  port 8192
   └─────────────┘                              └──────┬───────┘
                                                       │ VotifierEvent
                                                       ▼
                                                ┌──────────────┐
                                                │ VotingPlugin │
                                                └──────┬───────┘
                                                       │ match ServiceSite
                                                       ▼
                              VoteSites.yml entry → delay check → rewards
```

---

## 0. Two problems in the current config (read first)

### 0a. `host:` is a domain that does not exist — this is why voting fails

`plugins/Votifier/config.yml`:

```yaml
host: wrong-public-host.example  # ← WRONG
```

The server's public Java domain is **`play.theysix.tech`**. This is the same class of typo that
`.docs/docs/rebrand-notes.md` records being fixed elsewhere — this file was
missed.

```
$ getent hosts wrong-public-host.example
(nothing — does not resolve)
```

NuVotifier calls `InetAddress.getByName(host)` to decide which interface to bind
to. A name that does not resolve throws during bind, so the listening socket is
never created and **votes cannot arrive at all**. It can also log
`Address already in use` / `UnknownHostException` at startup — check the boot
lines for `[Votifier]` if you want to confirm.

**Fix:**

```yaml
host: 0.0.0.0
```

You almost certainly want `0.0.0.0` (listen on every interface) rather than the
domain name. This is what NuVotifier's own documentation recommends — *"usually
the default of 0.0.0.0 works"*
([NuVotifier Setup Guide](https://github.com/NuVotifier/NuVotifier/wiki/Setup-Guide)) —
and it is the correct choice on a shared host, where the container's IP can
change between restarts. Binding to the public domain name only works if that
name resolves to an IP the machine actually owns.

> If the host only allows inbound traffic on an allocated port, `0.0.0.0` is
> still right: the routing happens outside the container.

### 0b. The same typo is in the Discord `!ip` canned response

`plugins/DiscordSRV/config.yml:279`:

```yaml
DiscordCannedResponses: {"!ip": "play.theysix.tech", "!site": "https://theysix.tech"}
```

Anyone typing `!ip` in Discord is told to connect to a hostname that does not
resolve. Fix it in the same pass:

```yaml
DiscordCannedResponses: {"!ip": "play.theysix.tech", "!site": "https://theysix.tech"}
```

**Note:** these two files are the *only* surviving occurrences — grep confirms
the rest of the repo already uses `play.theysix.tech`.

```bash
grep -rn "theysix\.sparked\.host" . --include="*.yml" --include="*.conf" \
     --include="*.properties" --include="*.json" | grep -v '^./logs/'
```

---

## 1. Prerequisites

| Requirement | On this server | Notes |
|-------------|----------------|-------|
| Paper/Spigot 1.21.11 | ✅ | |
| NuVotifier jar in `plugins/` | ✅ `plugins/Votifier/` exists with generated RSA keys | The data folder is `Votifier/`, the jar is NuVotifier |
| VotingPlugin jar in `plugins/` | ✅ `plugins/VotingPlugin/` | |
| PlaceholderAPI | ✅ `plugins/PlaceholderAPI/` | For scoreboard/item placeholders |
| **PlaceholderAPI → VotingPlugin expansion** | ❌ **missing** | Only `player`, `server`, `vault`, `worldguard` are installed |
| An open TCP port for votes | ❌ **`8192` must be opened with your host** | See [step 3](#3-open-the-vote-port-with-your-host) |

### The missing expansion

`plugins/PlaceholderAPI/expansions/` currently holds only four expansions. Every
`%votingplugin_…%` placeholder will render as **literal text** until the
expansion is downloaded — including the ones on the hotbar vote item
(`plugins/ItemJoin/items.yml` → `vote-menu`) and the TAB scoreboard.

```
/papi ecloud download VotingPlugin
/papi reload
/papi parse me %votingplugin_total%      # must return a number, not the literal text
```

---

## 2. Step-by-step setup

### Step 1 — NuVotifier config

`plugins/Votifier/config.yml`:

```yaml
# Listen on all interfaces. Do NOT put a domain name here.
host: 0.0.0.0

# The port voting sites will connect to. Must differ from the game port (25625).
port: 8192

# Keep false unless you are on a proxy and have read the forwarding docs.
disable-v1-protocol: false

tokens:
  # New-style short token. Voting sites that support NuVotifier use this.
  default: 3bcuibblc7s6t7dqs6jkr9aoof

forwarding:
  # 'none' is correct for a single, standalone server like this one.
  method: none
  pluginMessaging:
    channel: nuvotifier:votes
```

Two things to understand here, because they are the two different credentials a
voting site may ask for ([NuVotifier: Key vs Token](https://github.com/NuVotifier/NuVotifier/wiki/Setup-Guide)):

| Credential | File | Who needs it | Safe to share? |
|------------|------|--------------|----------------|
| **RSA public key** | `plugins/Votifier/rsa/public.key` | Older voting sites, and NuVotifier's own test tooling | ✅ Yes — it is the *public* half |
| **Token** | `tokens.default` in `config.yml` | Sites that support the modern NuVotifier protocol | ⚠️ Treat as a secret |

- **`private.key` must never leave the server.** Never paste it into a panel,
  a Discord channel, or a support ticket.
- `public.key` is **one long line with no trailing newline**. When a site gives
  you a text box, paste that single line exactly as-is; most sites accept it
  without the `BEGIN/END PUBLIC KEY` wrapper. If a site demands the wrapped
  form, add `-----BEGIN PUBLIC KEY-----` before and
  `-----END PUBLIC KEY-----` after, on their own lines.
- If you ever regenerate the keys (delete `plugins/Votifier/rsa/` and restart),
  **every voting site must be updated with the new public key** or all votes get
  rejected as unverifiable. Do not regenerate casually.

> ⚠️ **The token in this repo is a real secret.** `default: YOUR_TOKEN`
> is the live value. Anyone with it plus your IP can forge votes. Consider
> rotating it (change the value, restart, update any site using it) and keeping
> it out of screenshots and public repos.

### Step 2 — Confirm NuVotifier is listening

Start the server and look for the Votifier boot line:

```
[Votifier] Votifier enabled.
```

You can verify the socket is actually open from the machine itself:

```bash
# Should print "Connection refused" / "succeeded" - either proves something answered on the port.
# "succeeded" = Votifier is listening and reachable locally.
nc -vz 127.0.0.1 8192
```

You can also reload Votifier's config **from the server console only** (it
deliberately does not work in-game):

```
/nvreload
```

### Step 3 — Open the vote port with your host

This is the step people forget, and it is **outside this repository** — it is a
hosting-panel/firewall action, not a file.

The voting website connects to `your-ip:8192`. If that port is firewalled:

- NuVotifier never sees a connection,
- VotingPlugin logs nothing at all,
- and the site's "test vote" page reports a failure or times out.

So:

1. Open **TCP 8192** inbound in your host's firewall / port allocation panel.
2. If your host uses a NAT/port-forward scheme, forward external TCP `8192` to
   this container's `8192`.
3. Re-check after any server migration — allocated ports often change.

> The game port (`25625`) and the vote port (`8192`) are **separate**. Opening
> one does not open the other.

### Step 4 — VotingPlugin: pick a service site

A **`ServiceSite`** is the identifier the voting website sends with the vote. It
is **not** a value you invent — it is whatever the site puts in its payload,
and VotingPlugin matches it against `VoteSites.yml`. Dots in it become
underscores for the internal key, and matching is case-insensitive otherwise
([VotingPlugin: Service Sites](https://github.com/BenCodez/VotingPlugin/wiki/Service-Sites)).

**Do not guess it from the vote URL.** Find the real value:

1. Send a real vote from the site, or a test vote: `/testvote` (Votifier).
2. Read the console:
   ```
   [VotingPlugin] Received a vote from service site 'SERVICESITEHERE' by player 'SomePlayer'!
   ```
3. Or ask the plugin what it has seen:
   ```
   /av servicesites
   ```

**Before any site is configured, votes still arrive but are not credited to a
site.** If you enable `AutoCreateVoteSites: true` in `plugin_settings`/`Config.yml`,
VotingPlugin will create the entry for you automatically
([Service Sites](https://github.com/BenCodez/VotingPlugin/wiki/Service-Sites)) —
handy, but it silently produces entries named after whatever the site sent, so
configuring by hand gives you nicer names and controlled rewards.

### Step 5 — Configure the vote site

`plugins/VotingPlugin/VoteSites.yml` currently ships **only the two disabled
example entries** (`ExampleVoteSite`, `Site2`) and an empty `EverySiteReward`.
Nothing real is configured yet, so **no vote can be credited to a site right
now** even once the port is open. Replace the examples with the real thing:

```yaml
VoteSites:
  # Internal key: short, no spaces, no dots. Free choice - you reference it in
  # commands and placeholders, nothing in it has to match the website.
  PlanetMinecraft:
    Enabled: true                      # ← required, examples ship as false
    Name: 'PlanetMinecraft'            # shown by /vote
    Priority: 5                        # higher = nearer the top of the list
    Hidden: false
    # MUST match the value the site sends - get it from the console or
    # /av servicesites, not from the URL.
    ServiceSite: 'PlanetMinecraft.com'
    VoteURL: 'https://www.planetminecraft.com/server/theysix-events/vote/'
    VoteDelay: 24h                     # used by /vote next
    # false = an early vote still counts and pays out. Leave false unless the
    # site's real interval matches VoteDelay, or legitimate votes get discarded.
    WaitUntilVoteDelay: false
    # true = pay the reward even when the player is offline (stacks up, paid on
    # next join). Convenient for a minigame server; keep an eye on the
    # %votingplugin_*% totals if you enable it.
    ForceOffline: false
    DisplayItem:
      Material: 'DIAMOND'              # required-ish; some GUIs error without it
      Amount: 1
    # INLINE rewards - no reward files needed for the common case.
    Rewards:
      Commands:
      # {player} is substituted by VotingPlugin. Run as console, so no leading '/'.
      - 'baa xp give -s {player} 35'
      - 'excellenteconomy give money {player} 25 -s'
      Messages:
        Player: '&bThanks for voting on &f%ServiceSite%&b!'
```

Repeat the block for each additional site (one entry per site, each with its own
`ServiceSite`).

**Field gotchas that cause silent failures:**

| Field | Trap |
|-------|------|
| `Enabled` | The shipped examples are `false`. Forgetting this is the single most common "it doesn't work" — the site is simply not loaded. |
| `ServiceSite` | Must equal what the site sends. A near-miss (`PlanetMinecraft` vs `PlanetMinecraft.com`) fails silently apart from a console line. |
| `VoteDelay` / `WaitUntilVoteDelay` | `VoteDelay` alone only drives `/vote next` (cosmetic). Only `WaitUntilVoteDelay: true` makes an early vote non-counting. |
| `ForceOffline` | Per-site. `false` means an offline player's reward waits for the next join (the usual choice). |

### Step 6 — Rewards: inline vs. files

Both work simultaneously; use inline for per-site rewards and files when a
reward set is reused.

- **Inline** (above) lives in the site's `Rewards:` block. **Nothing else is
  needed** — `plugins/VotingPlugin/Rewards/` is optional organisation only.
- **Files** go in `plugins/VotingPlugin/Rewards/`. This folder currently holds
  `ExampleBasic.yml` and `ExampleAdvanced.yml` — **both are documentation-only
  samples and are not executed.**

A file reward is shaped like:

```yaml
# plugins/VotingPlugin/Rewards/ExampleBasic.yml (structure)
Reward:
  Commands:
  - 'say {player} was rewarded'
  Messages:
    Player: '&aReward!'
```

Reference it from a site or special reward. For the bigger systems — vote
milestones, vote party, top-voter-of-the-month and cumulative rewards — that is
**`SpecialRewards.yml`**, not a reward file
([Rewards Overview](https://github.com/BenCodez/VotingPlugin/wiki/Rewards)).

**`{player}` vs `%player%`** — this trips people up:

- `{player}` is VotingPlugin's **internal** substitution and works in reward
  commands. This is the one to use in `Rewards.Commands`.
- `%player%` is a **PlaceholderAPI** placeholder and only resolves where a
  PlaceholderAPI pass has run.

Prefer `{player}` in reward commands so a missing PAPI expansion cannot leave a
literal `%player%` in your command.

### Step 7 — Verify

```
/av servicesites         # service names VotingPlugin has seen
/av vote <player> <site>  # simulate a vote for a configured site
/av reload                # reload VotingPlugin config
/nvreload                 # reload NuVotifier (console only)
```

Use `/av vote` **first** to prove the VotingPlugin half (site match → delay →
reward). Then do a **real** vote to prove the NuVotifier half (port → signature →
event). Doing it in that order tells you which half is broken.

---

## 3. Testing checklist

Work down this list; each row isolates one link in the chain.

| # | Check | Command / observation | If it fails |
|---|-------|----------------------|-------------|
| 1 | NuVotifier started | `[Votifier] Votifier enabled.` in boot log | `host:` is a name that does not resolve (see §0a) |
| 2 | Port open locally | `nc -vz 127.0.0.1 8192` | Port mismatched, or another process holds 8192 |
| 3 | Port open publicly | A site's "test vote" reaches the server | Hosting firewall — see [step 3](#3-open-the-vote-port-with-your-host) |
| 4 | Vote arrives | `/testvote`, or a real site vote → console line | If nothing logs: problem is NuVotifier/network, **not** VotingPlugin |
| 5 | Service site recognised | `/av servicesites` | Wrong `ServiceSite:` value — copy the exact string |
| 6 | Site enabled | `/av sites` lists it | `Enabled: false` |
| 7 | Reward fires | `/av vote <player> <site>` | Check the command exists; check `Rewards:` indentation |
| 8 | Placeholders resolve | `/papi parse me %votingplugin_total%` | Expansion not downloaded — see [prerequisites](#the-missing-expansion) |
| 9 | Hotbar / scoreboard | Vote item lore + TAB scoreboard show numbers | Steps 8, then re-upload `items.yml` / `TAB config.yml` |

### Reading the two key log lines

```
[Votifier] ...                        ← NuVotifier: the network half
[VotingPlugin] Received a vote from service site 'X' by player 'Y'!
```

- **No `[Votifier]` line at all** → the vote never reached the server.
  Networking/firewall/port. Do not go looking in VotingPlugin.
- **`[Votifier]` line but no `[VotingPlugin]` line** → NuVotifier received and
  verified it, but VotingPlugin is not listening. Both plugins must be loaded.
- **Both lines, but "service site not found"** → the vote arrived fine and the
  only problem is the `ServiceSite` string not matching `VoteSites.yml`.

---

## 4. Common mistakes

| Symptom | Likely cause |
|---------|--------------|
| Nothing in the log when voting | Port not open, or `host:` unresolvable / wrong interface |
| `Address already in use` at boot | `port:` collides with another service, or a second Votifier is running |
| Vote arrives, no reward | Site has `Enabled: false`, or `ServiceSite` mismatch |
| Reward pays every vote even inside 24h | Expected unless `WaitUntilVoteDelay: true` |
| Player got no reward, was online | `ServiceSite` not matched → vote credited to nobody |
| `%votingplugin_total%` shows as literal text | PAPI expansion missing (`/papi ecloud download VotingPlugin`) |
| Placeholders return `..` or `.` | Not cached yet; it resolves after the next cache cycle ([Placeholders](https://github.com/BenCodez/VotingPlugin/wiki/PlaceholderAPI-Placeholders)) |
| Duplicate reward / broadcast | More than one VoteSites entry shares the same `ServiceSite` |
| Vote "lost" after a site changes IP | Public key was regenerated — re-paste the public key at every site |
| Works for Java, not Bedrock | Check the Bedrock note in *Minecraft Server Lists*; test with a Java account first |

---

## 5. Player-facing surface on this server

Once it works, three things reference voting:

| Surface | File | Placeholders |
|---------|------|--------------|
| Hotbar vote item (diamond, slot 8) | `plugins/ItemJoin/items.yml` → `vote-menu` | `%votingplugin_total%`, `%votingplugin_total_alltime%`, `%votingplugin_dailyvotestreak%`, `%votingplugin_points%`, `%votingplugin_top_all_1%` |
| TAB scoreboard | `plugins/TAB/config.yml` | `%votingplugin_total%`, `%votingplugin_dailyvotestreak%` |
| Vote GUI | `plugins/VotingPlugin/GUI.yml` | rendered by VotingPlugin itself |

The two from `%votingplugin_total%` are confirmed spellings: it is the **month**
total, and `%votingplugin_total_alltime%` is the all-time total — these are not
interchangeable, even though both look like "total"
([PlaceholderAPI Placeholders](https://github.com/BenCodez/VotingPlugin/wiki/PlaceholderAPI-Placeholders)).

All of them need the PAPI expansion from the prerequisites, or the vote item and
scoreboard show literal `%votingplugin_…%` text.

Permissions: `VotingPlugin.Player` covers the normal player commands including
`/vote`; `VotingPlugin.Admin` covers `/av…`
([Commands & Permissions](https://github.com/BenCodez/VotingPlugin/wiki/Commands-&-Permissions)).
Grant `VotingPlugin.Player` to your default group in LuckPerms or `/vote` will
be denied.

---

## 6. Do-not-touch list

| Thing | Why |
|-------|-----|
| `plugins/Votifier/rsa/private.key` | Server's secret half. Leaking it lets anyone forge votes. Never commit, screenshot, or send it |
| `plugins/Votifier/rsa/public.key` | Regenerating it invalidates every site's config — all votes then fail signature checks |
| `tokens.default` | A shared secret, same reasoning as above |
| `plugins/VotingPlugin/Users.db` | Live vote/points/streak data (SQLite). Never overwrite it with a repo copy |
| `plugins/VotingPlugin/Backups/*.zip` | VotingPlugin's own dated backups — it writes these at day change |
| Vote port choice | Changing `port:` means re-configuring every voting site |

---

## 7. Minimal working config (copy-paste)

Two files, assuming the PAPI expansion is downloaded and TCP 8192 is open.

**`plugins/Votifier/config.yml`**

```yaml
host: 0.0.0.0
port: 8192
disable-v1-protocol: false
tokens:
  default: 3bcuibblc7s6t7dqs6jkr9aoof
forwarding:
  method: none
  pluginMessaging:
    channel: nuvotifier:votes
```

**`plugins/VotingPlugin/VoteSites.yml`** (replaces the disabled examples)

```yaml
VoteSites:
  PlanetMinecraft:
    Enabled: true
    Name: 'PlanetMinecraft'
    Priority: 5
    Hidden: false
    ServiceSite: 'PlanetMinecraft.com'   # ← replace with the value from /av servicesites
    VoteURL: 'https://www.planetminecraft.com/server/theysix-events/vote/'
    VoteDelay: 24h
    WaitUntilVoteDelay: false
    ForceOffline: false
    DisplayItem:
      Material: 'DIAMOND'
      Amount: 1
    Rewards:
      Commands:
      - 'baa xp give -s {player} 35'
      - 'excellenteconomy give money {player} 25 -s'
      Messages:
        Player: '&bThanks for voting on &f%ServiceSite%&b!'

EverySiteReward: {}
```

Then: `/av reload` → `/av vote <player> PlanetMinecraft` → real vote from the
site → confirm both log lines.

---

## 8. References

- [NuVotifier Setup Guide](https://github.com/NuVotifier/NuVotifier/wiki/Setup-Guide) — keys vs tokens, `0.0.0.0`, port rules, `/nvreload`, `/testvote`
- [VotingPlugin: Setup](https://github.com/BenCodez/VotingPlugin/wiki/Setup) — vote sites, rewards, `VotingPlugin.Player`, testing
- [VotingPlugin: Service Sites](https://github.com/BenCodez/VotingPlugin/wiki/Service-Sites) — how to find the real service-site string, `WaitUntilVoteDelay`, `AutoCreateVoteSites`
- [VotingPlugin: Commands & Permissions](https://github.com/BenCodez/VotingPlugin/wiki/Commands-&-Permissions) — full `/vote` and `/av` reference
- [VotingPlugin: PlaceholderAPI Placeholders](https://github.com/BenCodez/VotingPlugin/wiki/PlaceholderAPI-Placeholders) — every placeholder, plus `..` / `.` meaning
- [VotingPlugin: Rewards Overview](https://github.com/BenCodez/VotingPlugin/wiki/Rewards) — inline vs file vs special rewards
- Repo history: `.docs/docs/fix-log-2026-09-22.md` §3 (why `/vote` was chosen over `/ba vote`), §6 (the `items-Overwrite` follow-up)

---

## 9. Open items for this server

| Item | Where | Action |
|------|-------|--------|
| `host:` is a public hostname (does not resolve/bind) | `plugins/Votifier/config.yml:3` | Set to `0.0.0.0` |
| Wrong value in the Discord `!ip` response | `plugins/DiscordSRV/config.yml:279` | Set to `play.theysix.tech` |
| No real vote site configured | `plugins/VotingPlugin/VoteSites.yml` | Both entries are `Enabled: false` — votes cannot be credited to a site |
| PAPI VotingPlugin expansion missing | `plugins/PlaceholderAPI/expansions/` | `/papi ecloud download VotingPlugin` + `/papi reload` |
| TCP 8192 not verified open | Hosting panel | Open/forward TCP 8192; not a file change |
| `Server: PleaseSet` | `plugins/VotingPlugin/BungeeSettings.yml` | Cosmetic while `UseBungeecord: false`, but set it to e.g. `theysix` if proxy voting is ever enabled |
