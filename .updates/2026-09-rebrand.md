# Update — September 2026: rebrand to TheySix Events

Full notes: `.docs/docs/rebrand-notes.md`.

## Renames applied across all configs

| Before | After |
|--------|-------|
| Display name `PARTY GAMES` | `TheySix Events` |
| `Arcade by Blueva` | `TheySix Events` |
| `blueva.net` / `docs.blueva.net` | `theysix.sparked.network` |
| Permission prefix `bluearcade.admin` | left as-is (hardcoded in the core jar) |

Every player-facing `.yml`/`.json`/`.txt` under `plugins/BlueArcade3/`,
plus TAB, MOTD and guides, was search-and-replaced
(`BlueArcade → TheySixEvents`, `Blueva → TheySix`, `Party Games → TheySix Events`).

## Second pass

- **IP typo fixed everywhere:** `theysix.sarked.network` (missing **p**) →
  `theysix.sparked.network` in TAB bossbar/scoreboard/layout, BlueArcade3
  settings/items/rewards/sounds/language, TAB README and `server.properties` MOTD.
- **Brand gradients applied** (replacing the old `#00d4ff`/`#7c3aed` and
  `#f472b6`/`#a855f7` pairs): primary `<gradient:#4CADD0:#B2F9FF>`,
  secondary `<gradient:#9FEBF6:#4CADD0>`.
- **TAB:** header/scoreboard/footer use the server-name and server-IP gradients;
  the tab layout lists all 19 minigames; a `staffteam` scoreboard section was added.
- `/ba` kept as the working alias: it is hardcoded in ~15 compiled classes in
  the core jar, so MyCommand provides `/ts`, `/tse` (player) and `/tsa` (admin)
  on top instead of trying to rename it.
