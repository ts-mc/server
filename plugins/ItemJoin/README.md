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

## Vote navigation

Server-list vote links and vote statistics are provided by DankVotes. The
vote menu is managed separately from ItemJoin; this plugin's active item
configuration does not depend on VotingPlugin or Votifier. VotifierPlus remains
installed for vote receiving.

BlueArcade's `/ts vote` is an **in-arena** minigame vote, available while
waiting in an arena lobby. It is separate from server-list voting.

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
