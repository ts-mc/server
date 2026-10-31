# spark — Profiler Guide

spark is the performance profiler for **TheySix Events**
(`play.theysix.tech`). It samples the server and shows where tick time is
being spent — useful for diagnosing a laggy arena or plugin.

## Files

spark has no committed YAML config; it writes its data under this folder at
runtime.

## Commands

| Command | Effect |
|---------|--------|
| `/spark profiler` | start a profiler session |
| `/spark profiler stop` | stop and get the result link |
| `/spark tps` | show current TPS |
| `/spark health` | show a server health report |
| `/spark ping` | show your ping |
| `/spark heapsummary` | dump a heap summary |

## Notes

- Restrict spark to staff (`spark.*` permission nodes); profiling has a
  performance cost.
- `/spark tps` is a quick way to verify the server is healthy after an event.
- The TAB staff scoreboard shows TPS via `%server_tps_1_colored%` — see
  [`../TAB/README.md`](../TAB/README.md).
