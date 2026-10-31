# LuckPerms — Permissions Setup

How the LuckPerms permissions were configured for the TheySix Events server.

## Where the data lives

| File | Purpose |
|------|---------|
| `plugins/LuckPerms/config.yml` | LuckPerms config (weights, default group) |
| `plugins/LuckPerms/luckperms-h2-v2.mv.db` | H2 database — groups, players, tracks, permissions |
| `plugins/LuckPerms/paper-backup.json.gz.json.gz` | JSON backup of the original data (source of truth) |
| `plugins/LuckPerms/paper-backup.json.gz.json.gz.bak` | untouched original backup |

## How the database was built

The original H2 databases in both the archive and the Party Games setup were
**empty** — all real data lived in the `paper-backup.json.gz.json.gz` JSON
dump. A Python script (`/tmp/build_lp_db.py`) was used to:

1. Create the LuckPerms H2 schema (`luckperms_groups`,
   `luckperms_group_permissions`, `luckperms_players`,
   `luckperms_user_permissions`, `luckperms_tracks`, `luckperms_actions`).
2. Re-insert **30 groups**, **1,506 group permissions**, **35 players** and
   **3 tracks** (`levels`, `rank`, `staff`) into a fresh
   `luckperms-h2-v2.mv.db`.

## Group weights (set in `config.yml`)

| Group | Weight | Prefix (MiniMessage) |
|-------|--------|----------------------|
| owner | 1000 | `<gradient:#d78c39:#fef801>TheySix</gradient>` |
| manager | 950 | `<gradient:#f858ad:#fe030b>Manager</gradient>` |
| admin | 900 | `<gradient:#ff0000:#ffa3cb>Admin</gradient>` |
| headmoderator | 800 | `<gradient:#7f3aff:#f763f8>Head Moderator</gradient>` |
| mod | 700 | `<gradient:#6186eb:#b2f9ff>Moderator</gradient>` |
| helper | 600 | `<gradient:#4cadd0:#b2f9ff>Helper</gradient>` |
| staffteam | 550 | `<color:#ff0000>Staff Team</color>` |
| partner | 500 | `<gradient:#f82bc5:#f3a9c3>Partner</gradient>` |
| vip | 400 | `<gradient:#f47fff:#b44cff>VIP</gradient>` |
| contentcreator | 350 | `<color:#ff0000>Content Creator</color>` |
| beta | 300 | `<gradient:#d43d3d:#ffec00>Beta</gradient>` |
| default | 10 | `<color:#ff0000>Member</color>` |

`default-group: default` and `primary-group-calculation: parents-by-weight`.

## TheySix Events rank ladder (donutsMP-style)

The rank prefixes use **MiniMessage** gradient tags so they render as small
coloured labels in chat / TAB / scoreboard (any LuckPerms frontend that supports
MiniMessage, e.g. TAB, DeluxeChat, or a LuckPerms-aware chat plugin).

Each rank is a **separate group** with its own `prefix.{weight}.{value}` node,
`weight.{n}` node and permission set. Ranks do **not** inherit permissions from
each other — a player is assigned to exactly one rank group, so permissions do
not bleed between ranks.

### Tracks

| Track | Groups (in order) |
|-------|-------------------|
| `staff` | helper, staffteam, mod, headmoderator, admin, manager, owner |
| `rank` | beta, contentcreator, vip, partner, famous, ultra, elite, mythic, immortal |
| `levels` | level1 … level20 (kept from the original setup) |

### Player migration

Players were migrated from the old groups to the new ladder:

| Old primaryGroup | New rank |
|------------------|----------|
| owner | owner |
| admin | admin |
| sheriff | headmoderator |
| media | staffteam |
| mod | mod |
| famous | partner |
| ultra | contentcreator |
| elite | beta |
| vip | vip |
| default | default |
| (none) | default |

## TheySix Events permission nodes added

TheySix Events-specific permissions were added to the H2 database
(`/tmp/add_te_perms.py`):

| Group | Nodes |
|-------|-------|
| owner | `theysixevents.*`, `theysixevents.admin.*`, all admin sub-nodes, `bluearcade.admin` |
| admin | `theysixevents.admin.*`, all admin sub-nodes, `bluearcade.admin` |
| mod | `theysixevents.admin.kick`, `.stop`, `.setspawn`, `bluearcade.admin` |
| default | `theysixevents.play`, `.stats`, `.achievements`, `.level`, `.store`, `.party.*`, `.vote`, `.join`, `.quickjoin`, `.leave` |
| vip | `theysixevents.party.create`, `.party.max.15` |
| elite | `theysixevents.party.create`, `.party.max.25` |
| ultra | `theysixevents.party.create`, `.party.max.30` |
| famous | `theysixevents.party.create`, `.party.max.30` |
| media | `theysixevents.party.create`, `.party.max.25` |

## Important caveat

The core TheySix Events jar still checks the **legacy** permission node
`bluearcade.admin` for the in-game command bypass, so that node is granted to
owner/admin/mod alongside the new `theysixevents.*` tree. When the core jar is
recompiled/rebranded, the `bluearcade.admin` grants can be removed.

## Regenerating the database

If you need to rebuild the H2 DB from the JSON backup:

```bash
python3 /tmp/build_lp_db.py            # writes /tmp/lp_schema_and_data.sql
python3 /tmp/add_te_perms.py           # writes /tmp/te_perms.sql
python3 /tmp/add_rank_prefixes.py      # writes /tmp/rank_prefixes.sql (rank ladder + migration)
rm -f plugins/LuckPerms/luckperms-h2-v2.mv.db*
cat /tmp/lp_schema_and_data.sql /tmp/te_perms.sql /tmp/rank_prefixes.sql | \
  java -cp plugins/LuckPerms/libs/h2-driver-2.1.214.jar \
    org.h2.tools.Shell -url "jdbc:h2:file:plugins/LuckPerms/luckperms-h2-v2.mv.db" \
    -user root -password ""
```

Requires the Zulu JRE at
`/home/mihu/.lunarclient/jre/.../zulu21.../bin/java`.

## Rank ladder (donutsMP-style)

Each rank is a separate LuckPerms group with a **MiniMessage** gradient prefix
(`prefix.{weight}.{value}`), a `weight.{n}` node and its own permission set.
Ranks do **not** inherit permissions from each other — a player is assigned to
exactly one rank group.

| Rank | Weight | Prefix (MiniMessage) |
|------|--------|----------------------|
| owner | 100 | `<gradient:#d78c39:#fef801>TheySix</gradient>` |
| manager | 95 | `<gradient:#f858ad:#fe030b>Manager</gradient>` |
| admin | 90 | `<gradient:#ff0000:#ffa3cb>Admin</gradient>` |
| headmoderator | 80 | `<gradient:#7f3aff:#f763f8>Head Moderator</gradient>` |
| mod | 70 | `<gradient:#6186eb:#b2f9ff>Moderator</gradient>` |
| helper | 60 | `<gradient:#4cadd0:#b2f9ff>Helper</gradient>` |
| staffteam | 55 | `<color:#ff0000>Staff Team</color>` |
| partner | 50 | `<gradient:#f82bc5:#f3a9c3>Partner</gradient>` |
| vip | 40 | `<gradient:#f47fff:#b44cff>VIP</gradient>` |
| contentcreator | 35 | `<color:#ff0000>Content Creator</color>` |
| beta | 30 | `<gradient:#d43d3d:#ffec00>Beta</gradient>` |
| default | 10 | `<color:#ff0000>Member</color>` |

### Tracks

| Track | Groups (in order) |
|-------|-------------------|
| `staff` | helper, staffteam, mod, headmoderator, admin, manager, owner |
| `rank` | beta, contentcreator, vip, partner, famous, ultra, elite, mythic, immortal |
| `levels` | level1 … level20 (kept from the original setup) |

### Player migration

Players were migrated from the old groups to the new ladder:

| Old primaryGroup | New rank |
|------------------|----------|
| owner | owner |
| admin | admin |
| sheriff | headmoderator |
| media | staffteam |
| mod | mod |
| famous | partner |
| ultra | contentcreator |
| elite | beta |
| vip | vip |
| default | default |
| (none) | default |