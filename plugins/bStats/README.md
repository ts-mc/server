# bStats — Metrics Guide

bStats is the anonymous statistics library used by many plugins on
**TheySix Events** (`play.theysix.tech`). It is a dependency, not a
player-facing plugin.

## Files

| File | Purpose |
|------|---------|
| `config.yml` | bStats toggle and server UUID |

## Notes

- `enabled: true` lets plugin authors see anonymous usage counts.
- Set `enabled: false` if your policy forbids third-party telemetry. Doing so
  affects every plugin that uses bStats, not just one.
- No player-facing text, commands or branding apply.
