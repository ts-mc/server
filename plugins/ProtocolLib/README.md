# ProtocolLib — Protocol Library Guide

ProtocolLib gives other plugins low-level access to Minecraft packets on
**TheySix Events** (`play.theysix.tech`). It is a dependency, not a
player-facing plugin.

## Files

| File | Purpose |
|------|---------|
| `config.yml` | ProtocolLib settings (**empty = defaults**) |

## What depends on it here

| Plugin | Why |
|--------|-----|
| TAB | packet-level tab list, scoreboard and nametag control |
| LibsDisguises | disguise packets |
| SkinsRestorer | skin packets |
| spark / others | diagnostics |

## Commands

| Command | Effect |
|---------|--------|
| `/protocol` | show ProtocolLib status and loaded packet listeners |

## Notes

- Version: `ProtocolLib-5.4.0.jar`.
- If a packet-based plugin misbehaves, run `/protocol` first to confirm
  ProtocolLib loaded and the expected listeners are registered.
- No branding or translation applies to this plugin.
