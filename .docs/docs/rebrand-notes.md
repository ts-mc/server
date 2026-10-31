# TheySix Events — Rebrand Notes

This folder documents the rebranding that was applied to the "Arcade by Blueva"
plugin so it ships as **TheySix Events**.

## Third pass — jar bytecode rebrand (2026-10-24)

The core jar `BlueArcade-3.4.5.jar` was patched at the bytecode level and
re-shipped as `plugins/TheySixEvents-3.4.5.jar` (the old jar was removed from
`plugins/`):

- **Constant-pool rewriter** (`/tmp/rebrand_full.py`): walks every class file's
  UTF-8 constants and replaces player-facing strings only — internal class
  names (`net/blueva/...`), descriptors and signatures are skipped so the
  package layout stays valid (the module loader and reflection still work).
- Replaced: `www.blueva.net`, `docs.blueva.net`, `blueva.net` →
  `events-theysix.ro-mihaiu.xyz` (5 classes, e.g. `MessagesUtils` fallback
  defaults, `ModuleStoreClient`, `MenuImageUtils`, `UpdateManager`),
  `PARTY GAMES` / `Blue Arcade` / `BlueArcade` / `Arcade by Blueva` →
  `TheySix Events`.
- `plugin.yml` inside the jar: `name: TheySix Events`, `author: TheySix`,
  `website: events-theysix.ro-mihaiu.xyz`.
- All 987 class constant pools re-verified after patch; jar re-zip validated.
- Note: the plugin **folder** stays `plugins/BlueArcade3/` (data/config dir);
  only the jar file itself was renamed.

## What was renamed

| Before | After |
|--------|-------|
| `plugins/BlueArcade3/` | `plugins/BlueArcade3/` (display branding changed to TheySix Events) |
| Display name `PARTY GAMES` | `TheySix Events` |
| `Arcade by Blueva` | `TheySix Events` |
| `blueva.net` / `docs.blue` | `events-theysix.ro-mihaiu.xyz` |
| Permission prefix `bluearcade.admin` | left as-is (hardcoded in core jar) |

## Files changed
All `.yml` / `.json` / `.txt` files under `plugins/BlueArcade3/` were
search-and-replaced where they contained player-facing branding.

- `BlueArcade` → `TheySixEvents`
- `bluearcade` → `theysixevents`
- `Blueva` → `TheySix`
- `blueva.net` → `theysix.sparked.network`
- `PARTY GAMES` → `TheySix Events`
- `Party Games` → `TheySix Events`

## Second pass — IP typo, gradients, TAB and guides
A later pass fixed the remaining branding issues and documented the plugin
folders:

- **IP typo fixed everywhere:** the configs used `theysix.sarked.network`
  (missing the **p** in "sparked"). All active occurrences now read
  `theysix.sparked.network` — TAB bossbar/scoreboard/layout/animations,
  BlueArcade3 `settings.yml` / `items.yml` / `rewards.yml` / `sounds.yml` /
  `language.yml`, TAB README, and the MOTD in `server.properties`.
- **Brand gradients applied** (replacing the old `#00d4ff`/`#7c3aed` and
  `#f472b6`/`#a855f7` pairs): primary `<gradient:#4CADD0:#B2F9FF>`, secondary
  `<gradient:#9FEBF6:#4CADD0>`.
- **TAB:** header/scoreboard/footer use the server-name and server-IP gradients;
  the tab layout lists all 19 minigames; a `staffteam` scoreboard section shows
  staff rank, ping, TPS, `%staffteam_chat%`, `%admin_chat%` and `%staffonline%`.
- **Spanish:** the only Spanish content was a commented `es:` line in
  `BlueArcade3/language.yml`; it was removed. The `language/es/*.yml` stubs are
  empty and unreferenced.
- **Guides:** each plugin folder now has a `README.md`, plus a
  `plugins/README.md` index.

### Brand values
| Item | Value |
|------|-------|
| Server name | `TheySix Events` |
| Server IP | `theysix.sparked.network` (port `25625`) |
| Server-name gradient | `&#4CADD0&lT&#5BB8D7&lh&#69C3DD&le&#78CEE4&ly&#86D8EB&lS&#95E3F2&li&#A3EEF8&lx &#A1ECF7&lE&#90E0EF&lv&#7FD3E8&le&#6EC6E0&ln&#5DBAD8&lt&#4CADD0&ls` |
| Server-IP gradient | `&#4CADD0ᴛ&#55B4D4ʜ&#5FBBD9ᴇ&#68C2DDʏ&#71C9E1s&#7AD0E5ɪ&#84D6EAx&#8DDDEE.&#96E4F2s&#9FEBF6ᴘ&#A9F2FBᴀ&#B2F9FFʀ&#A9F2FBᴋ&#9FEBF6ᴇ&#96E4F2ᴅ&#8DDDEE.&#84D6EAɴ&#7AD0E5ᴇ&#71C9E1ᴛ&#68C2DDᴡ&#5FBBD9ᴏ&#55B4D4ʀ&#4CADD0ᴋ` |
| Primary colour | `<gradient:#4CADD0:#B2F9FF>` |
| Secondary colour | `<gradient:#9FEBF6:#4CADD0>` |

## What was NOT changed (intentionally)

- The **core plugin jar** is not included in this repo (it is downloaded by the
  module updater at runtime). The jar still hardcodes the permission node
  `bluearcade.admin` and the command alias `/ba`. These cannot be changed from
  config alone.
- Module jar filenames (e.g. `Race-Module-Minecraft-1.0.1.jar`) are kept as-is
  because the plugin loads them by name.
- The `.nbs` music/sound files are binary and untouched.

## If a deeper rebrand is needed

The core jar must be recompiled or the upstream "Arcade by Blueva" source must be
modified. The config-level rebrand done here is sufficient for in-game text,
titles, MOTD and store labels. The public server address is
`theysix.sparked.network` and the display name is `TheySix Events`.
## Fourth pass — exact chat prefix (2026-10-24, request)

The chat prefix now uses the exact per-character gradient string supplied by the
owner (rather than `<gradient>`):

```
&#4CADD0&lT&#54B3D4&lh&#5CB9D7&le&#64BFDB&ly&#6BC4DE&lS&#73CAE2&li&#7BD0E6&lx &#8BDCED&lE&#93E2F1&lv&#9AE7F4&le&#A2EDF8&ln&#AAF3FB&lt&#B2F9FF&ls
```

Set as `prefix:` in `plugins/BlueArcade3/language/en/main.yml`, followed by
`<gray>»</gray>`. The `plugin_info` block uses the MiniMessage form
`<bold><gradient:#4cadd0:#b2f9ff>TheySix Events</gradient></b>`. `colors.primary`
/ `colors.secondary` switched from named colors (`aqua`/`light_purple`) to hex
(`#4CADD0` / `#B2F9FF`) so any remaining `{color_primary}` placeholders render
in the brand palette.

`settings.yml` proxy server names updated: `bagame1` → `tse-game1`,
`balobby1` → `tslobby1`.
