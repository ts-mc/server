# DeluxeMenus — GUI Menu Guide

DeluxeMenus builds the clickable inventory GUIs used on **TheySix Events**
(`play.theysix.tech`), such as the coin shop and cosmetics menus.

## Files

| File | Purpose |
|------|---------|
| `config.yml` | DeluxeMenus settings (**empty = defaults**) |
| `gui_menus/Cosmetics.yml` | Cosmetics menu (**empty — to be authored**) |
| `gui_menus/modalidades.yml` | Game-modes menu (**empty — to be authored**) |
| `gui_menus/CoinShop-Cosmetics/` | Pets and particle-effect shop menus (**empty — to be authored**) |

> All menu files are currently empty stubs. DeluxeMenus loads only non-empty
> menu files, so nothing appears until these are authored.

## Structure of a menu file

```yaml
menu_title: '&7TheySix Events &8» &fCosmetics'
open_command: cosmetics
size: 54
items:
  example:
    material: DIAMOND
    slot: 22
    display_name: '&#4CADD0Cosmetic'
    lore:
      - '&7Click to equip'
    left_click_commands:
      - '[player] cosmetics equip example'
```

## Commands

| Command | Effect |
|---------|--------|
| `/dm open <menu>` | open a menu as a player |
| `/dm list` | list loaded menus |
| `/dm reload` | reload all menus |

## Notes

- Menu files must be non-empty and contain a valid `menu_title` to load.
- Cosmetics themselves are provided by UltraCosmetics — see
  [`../UltraCosmetics/README.md`](../UltraCosmetics/README.md).
- Use the brand gradient `&#4CADD0` … `&#B2F9FF` for menu titles and item names.
