# Update — 2026-09-22: startup crash (help.yml / FAWE)

Full analysis: `.docs/docs/fix-log-2026-09-22.md`.

## Startup crash loop

Two events had been conflated:

1. **Clean stop (23:03:14)** — the panel console detached
   (`Input/output error` on the console reader) and Paper treated dead stdin as
   a stop request. No exception, worlds saved.
2. **Actual crash on every restart** —
   `NoCapablePlatformException: Not all platforms have been registered yet`
   from FastAsyncWorldEdit while `SimpleHelpMap.initializeCommands()` built
   command help topics.

Root cause: `help.yml` (and `bukkit.yml`, `commands.yml`, `permissions.yml`)
were **0 bytes** left over from the Sep 19 corruption. An empty `help.yml` is
not "use defaults" — with no ignore list Paper built help topics for every
command, including FAWE's, before FAWE registered its platform, and
`BukkitCommandInspector` blew up.

Fix: restored valid (default) `help.yml` / `bukkit.yml` / `commands.yml` /
`permissions.yml`. No jar edited; commands.yml keeps only the standard
`icanhasbukkit` alias.

## BlueArcade placeholders

`plugins/TAB/config.yml` and `plugins/ItemJoin/items.yml` used BlueArcade
placeholders that returned literal text when the core was running empty
(see the 2026-09-20 entry). Refreshed once the core regenerated its config.
