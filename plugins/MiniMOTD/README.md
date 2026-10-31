# MiniMOTD — MOTD Guide

MiniMOTD customises the server-list MOTD and player-count text on
**TheySix Events** (`play.theysix.tech`).

## Files

| File | Purpose |
|------|---------|
| `main.conf` | Main MOTD config (defines the `motds=[ ... ]` list) |
| `plugin_settings.conf` | Plugin settings (**empty = defaults**) |

> `main.conf` (the `motds=[ ... ]` list) **overrides** `server.properties`
> `motd=` whenever it defines at least one entry. Both files must hold a
> *valid* value, because the server-list response is built from them on every
> ping — a malformed MOTD here makes the server look offline (see
> [the status-response crash](#why-a-malformed-motd-makes-the-server-look-offline)).

## Colour tags: legacy codes only, NOT MiniMessage

The MOTD lines in `main.conf` are **plain legacy text**. Despite the plugin's
name and its other message settings, `<gradient:#4CADD0:#B2F9FF>`,
`<bold>`, `<gray>` and `<br>` are **not** interpreted here. Written as-is they
are stored in the status response as literal characters and then break the
packet encoding.

| Do | Don't |
|----|-------|
| `line1="TheySix Events"` | `line1="<gradient:...><bold>TheySix Events"` |
| `line2="&btheysix.network"` | `line2="<gray>theysix.network</gray>"` |
| one `line1` + one `line2` | `line3=` / `line4=` / `line5=` |

The legacy `&` codes (`&b` aqua, `&8` dark grey, `&f` white, `&l` bold) are
supported, and `&#RRGGBB` hex codes work on 1.16+ clients.

## Current MOTD list

MiniMOTD picks one entry at random on each ping, so having several makes the
server list feel alive. This is exactly what is configured in `main.conf`:

```conf
motds=[
    {
        line1="TheySix Events"
        line2="&btheysix.network"
    },
    {
        line1="TheySix Events"
        line2="&f19 Minigames &8| &bJava + Bedrock"
    }
]
```

### Hard limit: 59 visible characters per line

The Minecraft client truncates the status JSON at **262 characters**. If that
truncation lands in the middle of `line1`, `line2` etc., the server cannot
encode the response and the ping fails. Keep every line at **59 visible
characters or fewer** (colour codes such as `&b` do not count as visible
characters — they are two characters of the three a colour code actually
occupies on the wire, so 59 is the safe budget).

The current entries are 14 + 17 and 14 + 35 characters — comfortably inside
the limit. Re-check this whenever you edit the lines.

## Why a malformed MOTD makes the server look offline

`ClientboundStatusResponsePacket` encodes the status through
`ServerStatus.CODEC.encodeStart(JsonOps.INSTANCE, status)`, and
`ClientboundStatusResponsePacket.CODEC` writes the result as a JSON string
through `ByteBufCodecs.lenientJson(262144)`. During encoding,
`ServerStatus.description` is written via `ComponentSerialization.CODEC` →
`PlainTextContents.MAP_CODEC` → `Codec.STRING`. If any step leaves a null
string, the encoder throws `NullPointerException` at
`Encoder$1.encode(Encoder.java:25)`.

The practical consequence on this server was `logs/latest.log` filling with:

```
io.netty.handler.codec.EncoderException: Failed to encode packet 'clientbound/minecraft:status_response'
Caused by: java.lang.NullPointerException
    at com.mojang.serialization.Encoder$1.encode(Encoder.java:25)
```

once per server-list ping — the client shows the server as **offline**.

### Notes on this config
- `icon=ts.png` must exist at `plugins/MiniMOTD/icons/ts.png` — the `icons/`
  folder is currently **empty**, so the entries above deliberately omit `icon=`
  (entries render fine without one). Add a 64×64 PNG and an `icon=` line if you
  want a custom icon.
- A 64×64 PNG is the only size the client accepts; anything else falls back to
  the default.
- The server list renders colours only — nothing animates there, so no moving
  or typing effects are possible in the MOTD.
- Swap `motds=[ ... ]` for `motd="..."` (singular, one string with `\n`) when
  you want a single fixed MOTD instead of a random pick.

## Notes
- Java: `play.theysix.tech`; Bedrock: `bedrock.theysix.tech:25832`.
- The plain `server.properties` MOTD is
  `motd=\u00A7bTheySix Events \u00A78| \u00A7ftheysix.network`. The `\u00A7`
  escapes are how a colour code is written there (a literal `§` character gets
  mangled by the properties editor); the next restart rewrites it to a literal
  `§`, which is normal.
- `plugins/MiniMOTD/plugin_settings.conf` is an empty file. MiniMOTD treats an
  empty settings file as "use defaults", which is fine — only `main.conf`
  carries a value that has to parse.
