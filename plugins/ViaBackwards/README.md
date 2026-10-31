# ViaBackwards — Older Client Support Guide

ViaBackwards lets older Minecraft clients connect to the Paper 1.21.11 server
running **TheySix Events** (`play.theysix.tech`) by translating packets
between protocol versions.

## Files

| File | Purpose |
|------|---------|
| `config.yml` | ViaBackwards settings (**empty = defaults**) |

## Notes

- ViaBackwards requires ViaVersion to be installed; the shipped jars are
  `ViaVersion-5.12.0.jar` and `ViaBackwards-5.12.0.jar`.
- ViaRewind adds support for very old client versions but is **not** present in
  this repo — install it separately if needed.
- Manage and reload from ViaVersion: `/viaversion list`, `/viaversion reload`.
- Brand text is unaffected; see [`../ViaVersion/README.md`](../ViaVersion/README.md).
