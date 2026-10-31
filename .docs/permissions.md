# TheySix Events — Rank Permissions (bulk commands)

Run these in-game as an operator or in console to create all groups, assign permissions, and set up the track. Console: drop the leading `/`.

> Based on the existing LuckPerms setup. Prefixes use `&#RRGGBB` hex colors with `&7| &r` separator — matching the TheySix Events branding style.

## 1. Create groups

```
lp creategroup owner
lp creategroup manager
lp creategroup admin
lp creategroup headmoderator
lp creategroup moderator
lp creategroup helper
lp creategroup staffteam
lp creategroup vip
lp creategroup voter
lp creategroup default
lp creategroup votelb-daily
lp creategroup votelb-weekly
lp creategroup votelb-monthly
```

## 2. Set weights and prefixes

```
lp group owner setweight 1000
lp group manager setweight 950
lp group admin setweight 900
lp group headmoderator setweight 850
lp group moderator setweight 800
lp group helper setweight 750
lp group staffteam setweight 700
lp group vip setweight 400
lp group voter setweight 300
lp group default setweight 10
lp group votelb-daily setweight 1
lp group votelb-weekly setweight 1
lp group votelb-monthly setweight 1

lp group owner meta setprefix 1000 "&#D78C39T&#DE9E30h&#E4B026e&#EBC21Dy&#F1D414S&#F8E60Ai&#FEF801x &7| &r"
lp group manager meta setprefix 950 "&#F858ADM&#FE030Ba&#F85882n&#FE0308a&#F858ADg&#FE0308e&#F85882r &7| &r"
lp group admin meta setprefix 900 "&#FF0000A&#FF0000d&#FF0000m&#FF0000i&#FF0000n &7| &r"
lp group headmoderator meta setprefix 850 "&#7F3AFFH&#7F3AFFe&#7F3AFFa&#7F3AFFd&#7F3AFFM&#7F3AFFo&#7F3AFFd&#7F3AFFe&#7F3AFFr&#7F3AFFa&#7F3AFFt&#7F3AFFo&#7F3AFFr &7| &r"
lp group moderator meta setprefix 800 "&#6186EBM&#6B94FEo&#75A3F0d&#7FB1F3e&#8ABEF6r&#94CDF8a&#9EDBFBt&#A8E9FDo&#B2F9FFr &7| &r"
lp group helper meta setprefix 750 "&#4CADB0H&#60BCC0e&#74CBD0l&#89DAE0p&#9DE9F0e&#B2F9FFr &7| &r"
lp group staffteam meta setprefix 700 "&#FF0000S&#FF0000t&#FF0000a&#FF0000f&#FF0000f&#FF0000T&#FF0000e&#FF0000a&#FF0000m &7| &r"
lp group vip meta setprefix 400 "&#F47FF7V&#F47FF7I&#F47FF7P &7| &r"
lp group voter meta setprefix 300 "&#4CAF50V&#4CAF50o&#4CAF50t&#4CAF50e&#4CAF50r &7| &r"
lp group default meta setprefix 10 "&#888888M&#888888e&#888888m&#888888b&#888888e&#888888r &7| &r"
```

## 3. Set default group

```
lp setdefaultgroup default
lp setprimarygroupcalculation parents-by-weight
```

## 4. Assign permissions

### owner

```
lp group owner permission set theysixevents.* true
lp group owner permission set theysixevents.admin.* true
lp group owner permission set bluearcade.admin true
lp group owner permission set bukkit.command.* true
```

### manager

```
lp group manager permission set theysixevents.admin.* true
lp group manager permission set bluearcade.admin true
lp group manager permission set bukkit.command.kick true
lp group manager permission set bukkit.command.warn true
lp group manager permission set bukkit.command.time true
lp group manager permission set bukkit.command.weather true
lp group manager permission set bukkit.command.tp true
lp group manager permission set bukkit.command.tp.player true
lp group manager permission set bukkit.command.location true
lp group manager permission set bukkit.command.gamemode true
lp group manager permission set bukkit.command.gamerule true
lp group manager permission set bukkit.command.world true
lp group manager permission set bukkit.command.op true
lp group manager permission set bukkit.command.deop true
lp group manager permission set bukkit.command.whitelist true
lp group manager permission set bukkit.command.ban true
lp group manager permission set bukkit.command.pardon true
lp group manager permission set bukkit.command.effect true
lp group manager permission set bukkit.command.give true
lp group manager permission set bukkit.command.enchant true
lp group manager permission set bukkit.command.experience true
lp group manager permission set bukkit.command.op.give true
lp group manager permission set bukkit.command.op.take true
lp group manager permission set bukkit.command.duplicate true
lp group manager permission set bukkit.command.whitelist.add true
lp group manager permission set bukkit.command.whitelist.remove true
lp group manager permission set bukkit.command.whitelist.on true
lp group manager permission set bukkit.command.whitelist.off true
lp group manager permission set bukkit.command.fill true
lp group manager permission set bukkit.command.replaceitem true
lp group manager permission set bukkit.command.event true
lp group manager permission set bukkit.command.publish true
lp group manager permission set bukkit.command.stop true
lp group manager permission set bukkit.command.reload true
lp group manager permission set bukkit.command.debug true
lp group manager permission set bukkit.command.tps true
```

### admin

```
lp group admin permission set theysixevents.admin.* true
lp group admin permission set bluearcade.admin true
lp group admin permission set bukkit.command.kick true
lp group admin permission set bukkit.command.warn true
lp group admin permission set bukkit.command.time true
lp group admin permission set bukkit.command.weather true
lp group admin permission set bukkit.command.tp true
lp group admin permission set bukkit.command.tp.player true
lp group admin permission set bukkit.command.location true
lp group admin permission set bukkit.command.gamemode true
lp group admin permission set bukkit.command.gamerule true
lp group admin permission set bukkit.command.world true
lp group admin permission set bukkit.command.op true
lp group admin permission set bukkit.command.deop true
lp group admin permission set bukkit.command.whitelist true
lp group admin permission set bukkit.command.ban true
lp group admin permission set bukkit.command.pardon true
lp group admin permission set bukkit.command.effect true
lp group admin permission set bukkit.command.give true
lp group admin permission set bukkit.command.enchant true
lp group admin permission set bukkit.command.experience true
lp group admin permission set bukkit.command.op.give true
lp group admin permission set bukkit.command.op.take true
lp group admin permission set bukkit.command.duplicate true
lp group admin permission set bukkit.command.whitelist.add true
lp group admin permission set bukkit.command.whitelist.remove true
lp group admin permission set bukkit.command.whitelist.on true
lp group admin permission set bukkit.command.whitelist.off true
```

### headmoderator

```
lp group headmoderator permission set theysixevents.admin.kick true
lp group headmoderator permission set theysixevents.admin.stop true
lp group headmoderator permission set theysixevents.admin.setspawn true
lp group headmoderator permission set bluearcade.admin true
lp group headmoderator permission set bukkit.command.kick true
lp group headmoderator permission set bukkit.command.warn true
lp group headmoderator permission set bukkit.command.time true
lp group headmoderator permission set bukkit.command.weather true
lp group headmoderator permission set bukkit.command.tp true
lp group headmoderator permission set bukkit.command.tp.player true
lp group headmoderator permission set bukkit.command.location true
lp group headmoderator permission set bukkit.command.gamemode true
lp group headmoderator permission set bukkit.command.effect true
lp group headmoderator permission set bukkit.command.give true
lp group headmoderator permission set bukkit.command.op.give true
lp group headmoderator permission set bukkit.command.op.take true
lp group headmoderator permission set bukkit.command.duplicate true
```

### moderator

```
lp group moderator permission set theysixevents.admin.kick true
lp group moderator permission set theysixevents.admin.stop true
lp group moderator permission set bluearcade.admin true
lp group moderator permission set bukkit.command.kick true
lp group moderator permission set bukkit.command.warn true
lp group moderator permission set bukkit.command.time true
lp group moderator permission set bukkit.command.weather true
lp group moderator permission set bukkit.command.tp true
lp group moderator permission set bukkit.command.tp.player true
lp group moderator permission set bukkit.command.location true
```

### helper

```
lp group helper permission set theysixevents.play true
lp group helper permission set theysixevents.stats true
lp group helper permission set theysixevents.achievements true
lp group helper permission set theysixevents.level true
lp group helper permission set theysixevents.store true
lp group helper permission set theysixevents.party.create true
lp group helper permission set theysixevents.party.max.15 true
lp group helper permission set theysixevents.vote true
lp group helper permission set theysixevents.join true
lp group helper permission set theysixevents.quickjoin true
lp group helper permission set theysixevents.leave true
lp group helper permission set bukkit.command.tp true
lp group helper permission set bukkit.command.tp.player true
lp group helper permission set bukkit.command.location true
lp group helper permission set bukkit.command.time true
lp group helper permission set bukkit.command.weather true
lp group helper permission set bukkit.command.gamemode true
```

### staffteam

```
lp group staffteam permission set theysixevents.play true
lp group staffteam permission set theysixevents.stats true
lp group staffteam permission set theysixevents.achievements true
lp group staffteam permission set theysixevents.level true
lp group staffteam permission set theysixevents.store true
lp group staffteam permission set theysixevents.party.create true
lp group staffteam permission set theysixevents.party.max.15 true
lp group staffteam permission set theysixevents.vote true
lp group staffteam permission set theysixevents.join true
lp group staffteam permission set theysixevents.quickjoin true
lp group staffteam permission set theysixevents.leave true
lp group staffteam permission set bukkit.command.tp true
lp group staffteam permission set bukkit.command.tp.player true
lp group staffteam permission set bukkit.command.location true
lp group staffteam permission set bukkit.command.time true
lp group staffteam permission set bukkit.command.weather true
lp group staffteam permission set bukkit.command.gamemode true
lp group staffteam permission set bukkit.command.kick true
lp group staffteam permission set bukkit.command.warn true
```

### vip

```
lp group vip permission set theysixevents.play true
lp group vip permission set theysixevents.stats true
lp group vip permission set theysixevents.achievements true
lp group vip permission set theysixevents.level true
lp group vip permission set theysixevents.store true
lp group vip permission set theysixevents.party.create true
lp group vip permission set theysixevents.party.max.15 true
lp group vip permission set theysixevents.vote true
lp group vip permission set theysixevents.join true
lp group vip permission set theysixevents.quickjoin true
lp group vip permission set theysixevents.leave true
```

### voter

```
lp group voter permission set theysixevents.play true
lp group voter permission set theysixevents.stats true
lp group voter permission set theysixevents.achievements true
lp group voter permission set theysixevents.level true
lp group voter permission set theysixevents.store true
lp group voter permission set theysixevents.party.create true
lp group voter permission set theysixevents.party.max.15 true
lp group voter permission set theysixevents.vote true
lp group voter permission set theysixevents.join true
lp group voter permission set theysixevents.quickjoin true
lp group voter permission set theysixevents.leave true
```

### default

```
lp group default permission set theysixevents.play true
lp group default permission set theysixevents.stats true
lp group default permission set theysixevents.achievements true
lp group default permission set theysixevents.level true
lp group default permission set theysixevents.store true
lp group default permission set theysixevents.party.create true
lp group default permission set theysixevents.party.max.15 true
lp group default permission set theysixevents.vote true
lp group default permission set theysixevents.join true
lp group default permission set theysixevents.quickjoin true
lp group default permission set theysixevents.leave true
```

## 5. Bulk paste for `lp editor`

In the `lp editor` web editor you can paste a comma-separated list straight into the **Permissions** box of a group instead of running one command per node. Copy a line, paste it, and save.

> All nodes below are pasted without a value, so LuckPerms stores them as `true`. If you want `false` instead, paste as `-node` (e.g. `-theysixevents.admin.kick`).

### owner
```
theysixevents.*, theysixevents.admin.*, bluearcade.admin, bukkit.command.*
```

### manager
```
theysixevents.admin.*, bluearcade.admin, bukkit.command.kick, bukkit.command.warn, bukkit.command.time, bukkit.command.weather, bukkit.command.tp, bukkit.command.tp.player, bukkit.command.location, bukkit.command.gamemode, bukkit.command.gamerule, bukkit.command.world, bukkit.command.op, bukkit.command.deop, bukkit.command.whitelist, bukkit.command.ban, bukkit.command.pardon, bukkit.command.effect, bukkit.command.give, bukkit.command.enchant, bukkit.command.experience, bukkit.command.op.give, bukkit.command.op.take, bukkit.command.duplicate, bukkit.command.whitelist.add, bukkit.command.whitelist.remove, bukkit.command.whitelist.on, bukkit.command.whitelist.off, bukkit.command.fill, bukkit.command.replaceitem, bukkit.command.event, bukkit.command.publish, bukkit.command.stop, bukkit.command.reload, bukkit.command.debug, bukkit.command.tps
```

### admin
```
theysixevents.admin.*, bluearcade.admin, bukkit.command.kick, bukkit.command.warn, bukkit.command.time, bukkit.command.weather, bukkit.command.tp, bukkit.command.tp.player, bukkit.command.location, bukkit.command.gamemode, bukkit.command.gamerule, bukkit.command.world, bukkit.command.op, bukkit.command.deop, bukkit.command.whitelist, bukkit.command.ban, bukkit.command.pardon, bukkit.command.effect, bukkit.command.give, bukkit.command.enchant, bukkit.command.experience, bukkit.command.op.give, bukkit.command.op.take, bukkit.command.duplicate, bukkit.command.whitelist.add, bukkit.command.whitelist.remove, bukkit.command.whitelist.on, bukkit.command.whitelist.off
```

### headmoderator
```
theysixevents.admin.kick, theysixevents.admin.stop, theysixevents.admin.setspawn, bluearcade.admin, bukkit.command.kick, bukkit.command.warn, bukkit.command.time, bukkit.command.weather, bukkit.command.tp, bukkit.command.tp.player, bukkit.command.location, bukkit.command.gamemode, bukkit.command.effect, bukkit.command.give, bukkit.command.op.give, bukkit.command.op.take, bukkit.command.duplicate
```

### moderator
```
theysixevents.admin.kick, theysixevents.admin.stop, bluearcade.admin, bukkit.command.kick, bukkit.command.warn, bukkit.command.time, bukkit.command.weather, bukkit.command.tp, bukkit.command.tp.player, bukkit.command.location
```

### helper
```
theysixevents.play, theysixevents.stats, theysixevents.achievements, theysixevents.level, theysixevents.store, theysixevents.party.create, theysixevents.party.max.15, theysixevents.vote, theysixevents.join, theysixevents.quickjoin, theysixevents.leave, bukkit.command.tp, bukkit.command.tp.player, bukkit.command.location, bukkit.command.time, bukkit.command.weather, bukkit.command.gamemode
```

### staffteam
```
theysixevents.play, theysixevents.stats, theysixevents.achievements, theysixevents.level, theysixevents.store, theysixevents.party.create, theysixevents.party.max.15, theysixevents.vote, theysixevents.join, theysixevents.quickjoin, theysixevents.leave, bukkit.command.tp, bukkit.command.tp.player, bukkit.command.location, bukkit.command.time, bukkit.command.weather, bukkit.command.gamemode, bukkit.command.kick, bukkit.command.warn
```

### vip / voter / default
All three share the same base player node list:

```
theysixevents.play, theysixevents.stats, theysixevents.achievements, theysixevents.level, theysixevents.store, theysixevents.party.create, theysixevents.party.max.15, theysixevents.vote, theysixevents.join, theysixevents.quickjoin, theysixevents.leave
```

### Prefixes (bulk)

The `lp editor` group page also has prefix fields — paste the string (quotes not needed in the editor):

```
&#D78C39T&#DE9E30h&#E4B026e&#EBC21Dy&#F1D414S&#F8E60Ai&#FEF801x &7| &r
```

## 6. Create staff track

```
lp createtrack staff
lp track staff append helper
lp track staff append staffteam
lp track staff append moderator
lp track staff append headmoderator
lp track staff append admin
lp track staff append manager
lp track staff append owner
```

## 7. Assign players to groups (template)

Replace `<player>` with the username.

```
lp user <player> parent set owner
lp user <player> parent set manager
lp user <player> parent set admin
lp user <player> parent set headmoderator
lp user <player> parent set moderator
lp user <player> parent set helper
lp user <player> parent set staffteam
lp user <player> parent set vip
lp user <player> parent set voter
lp user <player> parent set default
```

## 8. Sync and verify

```
lp sync
lp group owner info
lp listgroups
lp user <player> info
```

## Notes

- `bluearcade.admin` must be granted to owner/admin/mod — the core jar checks it for in-game command bypass. Can be removed when the jar is rebranded.
- Voter perks are earned by voting 4+ times in the last 24h. Use a vote plugin to auto-assign the voter group based on vote counts.
- Use `lp user <player> track staff promote` for promotions.
- Vote leaderboard period groups (`votelb-daily` / `votelb-weekly` /
  `votelb-monthly`) are **not** ranks. Create them with `/votelb-install`
  (or the `lp creategroup` lines above, then `/votelb-install` for the
  `fancynpcs.npc.*.see` and `fancyholograms.viewhologram.*` nodes). Weight is
  `1` so they never override the player's prefix. Players receive one of these
  as an extra parent on join / NPC click — do **not** parent them on `default`
  or all three periods can show at once.
