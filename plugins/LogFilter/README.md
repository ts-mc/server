# LogFilter — Console Log Filter Guide

LogFilter strips noisy or unwanted lines from the server console/log on
**TheySix Events** (`play.theysix.tech`).

## Files

| File | Purpose |
|------|---------|
| `config.yml` | Filter rules (generated at runtime) |

## Notes

- The shipped jar is `LogFilter-Paper-3.2.0.jar`.
- Typical use is hiding repetitive packet/connection warnings so real errors are
  easier to spot.
- Filter rules are regex-based; test a new rule with `/logfilter test` if the
  build provides it, then reload.
- No branding or translation applies.
