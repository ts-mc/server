# LuckPerms — in-game commands for gradient staff prefixes

Run these in-game/in-console as an operator (or console). Console: drop the
leading `/`. These recreate the staff ladder from `/docs/luckperms-setup.md`
without touching the H2 database.

## 1. Create the staff groups (idempotent)

```
/lp creategroup owner
/lp creategroup manager
/lp creategroup admin
/lp creategroup headmoderator
/lp creategroup mod
/lp creategroup helper
/lp creategroup staffteam
```

## 2. Set weights and gradient prefixes

Prefix priority (the first number) is the weight so the highest-ranked prefix
wins under `meta-formatting.prefix.format: highest`.

```
/lp group owner      setweight 1000
/lp group manager    setweight 950
/lp group admin      setweight 900
/lp group headmoderator setweight 850
/lp group mod        setweight 800
/lp group helper     setweight 750
/lp group staffteam  setweight 700

/lp group owner meta setprefix 1000 "<gradient:#d78c39:#fef801>TheySix</gradient> | "
/lp group manager meta setprefix 950 "<gradient:#f858ad:#fe030b>Manager</gradient> | "
/lp group admin meta setprefix 900 "<gradient:#ff0000:#ffa3cb>Admin</gradient> | "
/lp group headmoderator meta setprefix 850 "<gradient:#7f3aff:#f763f8>Head Moderator</gradient> | "
/lp group moderator meta setprefix 800 "<gradient:#6186eb:#b2f9ff>Moderator</gradient> | "
/lp group helper meta setprefix 750 "<gradient:#4cadd0:#b2f9ff>Helper</gradient> | "
/lp group staffteam meta setprefix 700 "<color:#ff0000>Staff Team</color> | "
```

## 3. Make `default` the default group and pick weight-based primary group

```
/lp setdefaultgroup default
/lp setprimarygroupcalculation parents-by-weight
```

(`default-group: default` and `primary-group-calculation: parents-by-weight`
are already correct in `config.yml`, but these guarantee it in the live data.)

## 4. Assign a player to a staff rank

```
/lp user <player> parent set <group>
```

e.g. `/lp user kilo parent set mod`

## 5. Build the `staff` track (promotion ladder)

```
/lp createtrack staff
/lp track staff append helper
/lp track staff append staffteam
/lp track staff append moderator
/lp track staff append headmoderator
/lp track staff append admin
/lp track staff append manager
/lp track staff append owner
```

Use it with promotion:
```
/lp user <player> track staff promote
/lp user <player> track staff demote
```

## 6. Apply / reload

```
/lp sync      (in-game: `/lp sync`)
```

`/lp sync` pushes the changes to all LuckPerms-supported plugins (TAB, chat,
scoreboard) immediately. If TAB or your chat plugin is not picking up the
prefix, also confirm its config pulls LuckPerms meta (see setup-guide notes on
TAB). After running, verify with:

```
/lp group mod info        -> should show "Prefix: <gradient...>Moderator</gradient> "
/lp listgroups            -> lists all groups
/lp user <player> info    -> shows parents/weight/prefix
```

## Notes
- Prefixes use MiniMessage, so `<gradient:#a:#b>text</gradient>` renders as a
  colored gradient in TAB/chat **only if** the frontend plugin (TAB, DeluxeChat,
  etc.) supports MiniMessage. LuckPerms just stores the meta.
- These commands modify live data (same place the DB write was going), so once
  `/lp sync` succeeds the prefixes should show for staff immediately.
