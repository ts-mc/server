# HeadDB — Custom Heads Guide

HeadDB provides a browsable database of custom player heads on **TheySix Events**
(`play.theysix.tech`), used for decorative items and menus.

## Files

| File | Purpose |
|------|---------|
| `config.yml` | HeadDB settings (**empty = defaults**) |
| `categories.yml` | Head categories (**empty = defaults**) |
| `sounds.yml` | Menu sounds |
| `messages/en.yml` | English messages (**empty = defaults**) |

## Commands

| Command | Effect |
|---------|--------|
| `/hdb` | open the head database |
| `/hdb search <term>` | search for a head |
| `/hdb give <player> <id>` | give a head (staff) |
| `/hdb reload` | reload the config |

## Notes

- The shipped jar is `HeadDB-5.0.0-rc.15.jar`.
- Heads are referenced by id in menus; see
  [`../DeluxeMenus/README.md`](../DeluxeMenus/README.md).
- Categories live in `categories.yml`; the free database has a limited set, a
  paid key unlocks more.
