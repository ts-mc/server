# TheySix Events — ILM Handover

Handover notes for the next ILM (incoming line manager /接手人) who needs to
work on the TheySix Events Minecraft server.

## Where everything is

| Item | Path |
|------|------|
| Server root | `/home/mihu/github/mc-server/` |
| Original archive (do not delete) | `/home/mihu/github/mc-server/archive-2026-09-14T135646Z/` |
| Party Games reference setup | `/home/mihu/github/mc-server/Party Games Setup (2.15.1) by Blueva/` |
| Minigames plugin (display-rebranded) | `/home/mihu/github/mc-server/plugins/BlueArcade3/` |
| Arena worlds | `/home/mihu/github/mc-server/PG-01/` … `PG-06/`, `world_lobby/` |
| Documentation | `/home/mihu/github/ts-server/.docs/docs/` (moved out of the server root) |

## What was done

1. **Transferred** every file from `archive-2026-09-14T135646Z/` into the project
   root (server.jar, plugins/, config/, world/, libraries/, versions/).
2. **Copied** the Party Games setup's BlueArcade3 plugin + 6 arena worlds +
   lobby world into the project root.
3. **Rebranded the player-facing configuration** in `plugins/BlueArcade3/` to
   **TheySix Events** while retaining the load-bearing `BlueArcade3` data folder.
   See `docs/rebrand-notes.md`.
4. **Configured LuckPerms**: rebuilt the H2 database from the JSON backup
   (the .mv.db files were empty), set group weights, default group, and added
   TheySix Events permission nodes. See `docs/luckperms-setup.md`.
5. **Created** the minigames list (`docs/minigames-list.md`), setup guide
   (`docs/setup-guide.md`), plugin inventory (`docs/plugin-inventory.md`), the
   LuckPerms doc (`docs/luckperms-setup.md`) and the site handover
   (`docs/site-handover.md`).

## Docs index

- `docs/minigames-list.md` — all 19 minigames with IDs, modes, team formats
- `docs/setup-guide.md` — how to start the server and configure arenas
- `docs/plugin-inventory.md` — every plugin, module, config file
- `docs/rebrand-notes.md` — what was renamed & what still needs a jar rebuild
- `docs/luckperms-setup.md` — permissions database & group weights
- `docs/fix-log-2026-09-20.md` — startup-error + in-game bug fixes (0-byte data recovery)
- `docs/site-handover.md` — brand info copied from theysix.ro-mihaiu.xyz
- `docs/discord/README.md` — Discord announcement hub + pre-flight checklist
- `docs/discord/announcements.md` — ready-to-post Discord announcements
- `docs/discord/templates.md` — reusable skeletons with `{placeholders}`

## Known limitations

- The **core BlueArcade3 jar is not in the repo** — modules are, but the main
  plugin jar is fetched by the module updater at runtime. You will need to
  obtain `BlueArcade3.jar` (or the rebranded equivalent) separately.
- The permission node `bluearcade.admin` and the command alias `/ba` are
  hardcoded in the core jar and were **not** changed.
- `server.properties` still has `online-mode=false` and `max-players=100` —
  review before going public.

## Useful commands

- `ls /home/mihu/github/mc-server/` — server root
- `ls /home/mihu/github/mc-server/plugins/BlueArcade3/modules/` — minigame modules
- `cat /home/mihu/github/mc-server/plugins/BlueArcade3/data/arenas/1/arena.json` — arena 1 config## Publishing to GitHub

The repo is configured for version control of **config, plugins, docs and the
server core** — not of runtime state.

- `.gitignore` excludes logs, caches, `libraries/`, `versions/`, world folders,
  plugin runtime data (player UUID files, H2 databases), archives and secrets.
- `plugins/BlueArcade3/data/` is intentionally **kept** (arena configs and
  schematics, documented in `docs/setup-guide.md`), except `data/users/` and
  `data/cache/`.
- **Secrets are not committed:** `.vscode/sftp.json` (SFTP password) is ignored,
  and `server.properties` → `management-server-secret` is left blank. Set these
  locally after cloning. Discord bot tokens, webhook URLs and DB passwords must
  never be committed.

If you cloned before this change and still see large files staged, re-run:

```bash
git rm -r --cached --ignore-unmatch logs libraries versions cache "world*" \
  .vscode/sftp.json plugins/*/userdata plugins/*/data
git add -A
```
