# Update — 2026-09-20: 0-byte corruption recovery

Full analysis: `.docs/docs/fix-log-2026-09-20.md`.

A corruption/copy event (`Sep 19 11:55` timestamp) truncated many configs,
libraries and caches to **0 bytes**, cascading into most plugins failing to
enable. All fixes are file-level; no jar was patched.

## What was replaced / deleted

| 0-byte path | Action | Result |
|---|---|---|
| `plugins/LuckPerms/libs/*.jar` | deleted | LP re-downloads libs |
| `plugins/BlueArcade3/libraries/*` | deleted | core re-extracts |
| `plugins/FancyGlow/libraries/*` | deleted | FancyGlow enables |
| `plugins/FastAsyncWorldEdit/lang/strings.json` | deleted | FAWE enables |
| `plugins/Essentials/items.json` | deleted | Essentials/Chat/Spawn enable |
| `plugins/SkinsRestorer/cache/*` | deleted | cache rebuilt |
| `plugins/FancyNpcs/version.yml` (stale) | replaced | NPC data format correct |
| `plugins/BlueArcade3/settings.yml`, `items.yml`, `rewards.yml`, `sounds.yml`, `data/*.json` | regenerated from defaults | core runs; custom tweaks re-applied in game |

## Plugin jar replacements

- **WorldGuard 7.0.13 → 7.0.19** — 7.0.13 hard-depends on the removed WEPIF API
  (`NoClassDefFoundError PermissionsResolverManager`). Old jar moved to
  `plugins/.disabled-jars/`.

## In-game bug fixes

- **Ghost-dropping diamond armour crash (HubPVP):** armour restore on death
  could leave ghost items that crashed the server; HubPVP behaviour reined in
  (armour locked into inventory, restore guarded).
- DeluxeMenus NMS hook warning was cosmetic (empty config = defaults), left as is.
