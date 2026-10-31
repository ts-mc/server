# HubPVP — Hub PvP Guide

HubPVP adds PvP combat to the hub world on **TheySix Events**
(`play.theysix.tech`), with optional arenas, combat tagging and
kill rewards.

## Files

| File | Purpose |
|------|---------|
| `config.yml` | Hub PvP settings (**empty = defaults**) |

## What it handles here

| Feature | Notes |
|---------|-------|
| PvP toggle | enable/disable player combat in the hub |
| Combat tag | prevent combat-logging |
| Kill rewards | items/currency for kills |
| Safe zone | area where PvP is off |

## Commands

| Command | Effect |
|---------|--------|
| `/hubpvp` | show plugin help/status |
| `/hubpvp reload` | reload the config |

## Notes

- The shipped jar is `HubPVP-3.1.2.jar`.
- The arena worlds use WorldGuard PvP flags instead — see
  [`../WorldGuard/README.md`](../WorldGuard/README.md).
- The dedicated hub-PvP world is `world/` (`config.yml` → world setting).
