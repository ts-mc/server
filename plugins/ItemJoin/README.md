# ItemJoin — Join Items Guide

ItemJoin gives players custom items when they join — such as a compass menu or
a lobby selector — on **TheySix Events** (`play.theysix.tech`).

## Files

| File | Purpose |
|------|---------|
| `items.yml` | Item definitions, triggers and commands (**empty = defaults**) |
| `config.yml` | ItemJoin settings (**empty = defaults**) |
| `en-lang.yml` | English messages (**empty = defaults**) |

## Example item

```yaml
lobby-selector:
  id: COMPASS
  slot: 0
  name: '&#4CADD0TheySix Events Selector'
  lore:
    - '&7Right-click to open the game selector'
  commands:
    - 'console: ba join %player%'
  triggers: 'join'
```

## Commands

| Command | Effect |
|---------|--------|
| `/itemjoin reload` | reload the config |
| `/itemjoin list` | list defined items |
| `/itemjoin get <item>` | give yourself an item (staff) |
| `/itemjoin toggle` | toggle your items on/off |

## Vote item and server-list voting

`items.yml` ships two items: `minigames-menu` (slot 0, opens the arena selector)
and `vote-menu` (slot 8).

The vote item targets the **`/vote`** command, provided by
[VotingPlugin](https://github.com/BenCodez/VotingPlugin) together with a vote
listener (**NuVotifier**, data folder `plugins/Votifier/`).

**Both are installed**, so the item ships enabled (no permission gate). If the
voting stack is ever removed, the item becomes a dead slot — then either delete
the `vote-menu:` block or set its `triggers: DISABLED`.

Configuration lives in the voting plugins themselves:

- `plugins/VotingPlugin/VoteSites.yml` — vote sites + per-site rewards
- `plugins/VotingPlugin/Config.yml` — behaviour, messages, top voter
- `plugins/Votifier/` — NuVotifier listener + port/keys

The item lore uses **real** VotingPlugin placeholders
([source](https://github.com/BenCodez/VotingPlugin/wiki/PlaceholderAPI-Placeholders)):

| Placeholder | Meaning |
|-------------|---------|
| `%votingplugin_total%` | Player's month vote total |
| `%votingplugin_total_alltime%` | Player's all-time vote total |
| `%votingplugin_dailyvotestreak%` | Current daily vote streak |
| `%votingplugin_points%` | Player's voting points |
| `%votingplugin_top_all_1%` | Name of the #1 all-time voter |

> **Do not** use `%bluearcade_votes_*%` for this: BlueArcade's expansion has no
> vote keys at all. Its `/ba vote` command is an **in-arena** minigame vote and
> only works while waiting in an arena lobby — it is not a server-list vote.
> In-arena voting is already provided by BlueArcade itself via
> `plugins/BlueArcade3/items.yml` → `waiting_room_items.vote`.

## Money

Player `money` (ExcellentEconomy, Vault-primary) is not shown on the join items;
it appears on the TAB scoreboard (`%vault_eco_balance_formatted%`) and is paid
out per match by `plugins/BlueArcade3/rewards.yml`.

## Notes

- The shipped jar is `ItemJoin-6.1.7.jar`.
- The minigames plugin also defines its own lobby items in
  `plugins/BlueArcade3/items.yml`; use one source or the other to avoid duplicate
  hotbar items.
- `items-Overwrite` is `false` in the shipped file. The hub cosmetics pouch is
  protected from death drops, so on respawn it stays in the inventory and
  ItemJoin refuses to re-deliver it ("1 item could not be delivered"). Set it to
  `true` if you want it re-given on every respawn/respawn.
- Brand text: **TheySix Events** / `play.theysix.tech`.
