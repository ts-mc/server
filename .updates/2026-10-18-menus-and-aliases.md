# Update — 2026-10-18: BlueArcade menus, `/ts` `/tse` fix, NPC locations

No `.jar` edited — all changes are config files.

## 1. Main-lobby "Games Menu" hub (DeluxeMenus)

**New file:** `plugins/DeluxeMenus/gui_menus/ts_main_menu.yml`
registered in `plugins/DeluxeMenus/config.yml` as `ts_main_menu`,
open command `/tsmenu` (also `/ts-menu` and `/tse-menu` via MyCommand).

Items (slots): Play (0, L-click `/ba quickjoin`, R-click `/ba join`),
Store (2, `/ba store`), Vote (3, `/vote`), Stats (4, `/ba stats`),
Party (6, `/ba party`), Close (8, `[close]`).
Styled with the brand gradients `<gradient:#4CADD0:#B2F9FF>` and
`<gradient:#9FEBF6:#4CADD0>`.

## 2. BlueArcade hotbar (`plugins/BlueArcade3/items.yml`)

`main_lobby_items` reworked:

| Slot | Before | After |
|------|--------|-------|
| 0 | EMERALD "Play again" (`/ba quickjoin`) | **COMPASS "Games Menu"** → `/tsmenu` |
| 1 | BOOK "Maps" (`/ba join`) | EMERALD "Quick Join" (`/ba quickjoin`) |
| 4 | PLAYER_HEAD stats | PLAYER_HEAD "Profile/Stats" (`/ba stats`) |
| 7 | CAKE party | CAKE "Party" (`/ba party`) |
| 8 | RED_BED leave | RED_BED leave message |

Waiting-room and end-game items unchanged (store/vote/team/leave).

## 3. `/ts` and `/tse` fix (`plugins/MyCommand/commands/theysix-aliases.yml`)

Players typing `/ts` saw `Not enough arguments... -> /ba $args` because MyCommand
did not expand `$args`. Replaced `$args` with **`$multiargs`** in the `/ts`,
`/tse` and `/tsa` runcmds; tab completion kept. Also added MyCommand entries
`ts_menu` (`/ts-menu`) and `ts_menu_long` (`/tse-menu`) forwarding to `/tsmenu`.

## 4. Arena command whitelist (`plugins/BlueArcade3/settings.yml`)

`cancel_commands.allowed_commands` now also allows `/ts`, `/tse` and `/tsmenu`
so the compass and menus work while waiting inside an arena.

## 5. NPC locations (`plugins/FancyNpcs/npcs.yml`)

Every NPC with `type: PLAYER` (the discord NPC, leaderboard_1 … leaderboard_5,
kotw, minigame_menu_selector) now has an explicit:

```yaml
    location:
      world: world
      x: -156.5
      y: -47.0
      z: -134.5
      yaw: -120.749954
      pitch: 36.000027
```

Previously only the discord NPC carried a location; the others spawned wherever
FancyNpcs last stored them. All NPCs are pinned to the same spawn point in
`world` at (-156.5, -47.0, -134.5), yaw -120.749954, pitch 36.000027.

## Reload checklist

1. `/mycmd-reload` (MyCommand aliases).
2. Restart the server (DeluxeMenus + BlueArcade read menus/items on enable).
3. In game: main-lobby slot 0 opens the `/tsmenu` hub; `/ts` and `/tse` now
   forward arguments correctly.
