# Updater — Plugin Updater Guide

Updater checks for and downloads new plugin versions on **TheySix Events**
(`play.theysix.tech`).

## Files

| File | Purpose |
|------|---------|
| `config.yml` | Update source and behaviour settings (**empty = defaults**) |

## Notes

- Empty config means defaults are used.
- Automatic downloads over the network should be **disabled on a locked-down
  host**; prefer a manual, reviewed update flow for a production event server.
- The minigames core has its own module updater — see the
  `updates.module_updates.enabled` setting notes in
  [`docs/setup-guide.md`](../../docs/setup-guide.md).
- No player-facing commands or branding apply.
