# LobbyFighting — Lobby PvP Guide

LobbyFighting enables PvP fighting in the lobby on **TheySix Events**
(`play.theysix.tech`).

## Files

| File | Purpose |
|------|---------|
| `config.yml` | Lobby-fighting settings (**empty = defaults**) |

## What it handles here

| Feature | Notes |
|---------|-------|
| PvP in the lobby | allow/deny player combat |
| Safe zones | areas where fighting is disabled |
| Combat rules | knockback, damage and death handling |

## Notes

- No documented standalone commands; all behaviour is configured in
  `config.yml`.
- If you also use HubPVP, disable one of the two so both do not fight over the
  same lobby PvP flag — see [`../HubPVP/README.md`](../HubPVP/README.md).
- The lobby world is `world_lobby/` (`world` level name).
