# Update — 2026-09-25: MOTD packet crash and hotbar cleanup

## 1. Server-list ping NPE (MOTD)

Root cause: `plugins/MiniMOTD/main.conf` shipped MiniMessage markup
(`<gradient:…>`, `<bold>`) that MiniMOTD does **not** parse in the server-list
lines, plus three invalid keys (`line3`/`line4`/`line5` — only `line1`+`line2`
are read). The over-long entry truncated the status JSON at 262 chars and the
encoder threw `NullPointerException` on ~every other ping (MiniMOTD picks a
MOTD entry at random).

Fix: both `server.properties` `motd=` and `plugins/MiniMOTD/main.conf` rewritten
as short, plain legacy-colour MOTDs, one `line1` + `line2` pair per entry.
Details: `.docs/docs/fix-log-2026-09-25.md`.

## 2. ItemJoin hotbar duplicates removed (`plugins/ItemJoin/items.yml`)

The `items:` section was emptied on purpose. ItemJoin join items duplicated the
BlueArcade3 hotbar (`plugins/BlueArcade3/items.yml`), which owns the lobby/
waiting-room slots. Comment block left in the file explaining this. The vote
item is still available via `/vote`; the hotbar diamond was dropped with the
rest.
