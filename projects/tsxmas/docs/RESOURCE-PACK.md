# Christmas texture fix and required Java packs

## Diagnosis from the latest LIVE snapshot

On November 15, the relevant plugin/configuration directories, latest log,
three recent archived logs and current generated BetterModel ZIP were fetched
into the ignored `.remote-sync/` comparison snapshot. No snapshot was mirrored
back into LOCAL or LIVE. Existing owner settings were compared before editing.

LIVE's `server.properties` had an empty `resource-pack`, empty hash and
`require-resource-pack=false`. TSXMAS also had `resource-pack.enabled: false`.
BetterModel loaded normally. No matching model/pack load failures were found
in the three recent archived logs. Server logs cannot prove client rendering.

The venue contains item displays referencing models such as
`weihnachtsmc:deko/laternenpfahl`, furniture, trees and presents. Vanilla clients
do not have these definitions. Not serving their pack explains missing/purple
and black models independently of whether BetterModel's plugin is enabled.

## Delivery policy

Two Java packs are used:

| Pack | Delivery | Policy |
| --- | --- | --- |
| WeihnachtsMC venue + current LIVE BetterModel assets | Paper's server.properties | Required |
| A Very Minecraft Christmas 1.3.0, Java 26.3 | TSXMAS join handler, SEASONAL mode | Required; added to the pack stack |

The seasonal pack uses its author's unchanged official Modrinth download URL.
Modrinth's project metadata declares All Rights Reserved, so that ZIP is not
embedded in or republished with the hosted venue pack. Its vanilla snow/GUI
textures take precedence when TSXMAS adds it above the venue pack; the map's
`weihnachtsmc` namespace remains available.

The hosted venue pack retains every generated LIVE BetterModel asset and every
source-map asset except the old vanilla background-music mute. It does not
modify the source archive or replace BetterModel's generated `build.zip`.
Its credits file links the map and BetterModel and preserves the map page's
music/public-domain-art credits. This does not relicense third-party assets.

MCPacks was the initial requested host, but its human CAPTCHA could not be
completed through the agent dashboard. The owner approved another public HTTPS
host. The venue ZIP was uploaded through Catbox's official anonymous upload API.

URL: `https://files.catbox.moe/0xupdx.zip`

SHA-1: `dd85146b2795270bdbf6bb4cdf50704d0ceb6733`

SHA-256: `210e1c519f0fc7f7c0a843a099af5dc3daabeaa57da0e87c3bfa8a79d6fc412f`

Size: 10,871,214 bytes. 11,702 files, including 10,562 parsed JSON files.
Static checks found no conflicting model files or missing custom model/texture
references. Downloading the hosted ZIP back matched both hashes.

The seasonal ZIP SHA-1 remains
`848a7a8e8d2ef149bec10b336b77185cb15936a2`.

## Targeted configuration and rollback

`pack-policy.py` prepares only the five Paper resource-pack properties and the
TSXMAS pack enabled/mode/prompt/required settings. It preserves the top-level
gameplay `enabled` flag, all event settings, URL/hash of the official seasonal
pack, credentials and unrelated server properties. It stages inactive files,
checks source hashes again, refuses activation while LIVE appears running,
retains the old two configs in `.deploy-backups/tsxmas-pack-<timestamp>/`, and
reads active files back to verify SHA-256. See `pack-deployment.json` for the
completed deployment receipt; its presence with `active: true` confirms the
configuration was actually activated.

No Java process is started by these tools. A normal server restart is necessary
for Paper's server.properties policy to apply. The event map and player data are
not changed. For rollback, stop LIVE and restore those two matching backed-up
files; retain the current files privately before restoring.

## What to check after starting LIVE

1. Join using Java **26.3** with server resource packs enabled in the saved
   server entry. If it was set to Disabled, edit that entry before joining.
2. Accept the required downloads. Both pack IDs should load; refusing the
   required venue pack should prevent Java login. Check the client log for
   failed downloads/invalid JSON if loading fails.
3. `/mvtp tsxmas_christmas_2026`, then `/tp -125 67 543`. Check lamp posts,
   trees, furniture, presents, paintings and the workshop. A client screenshot
   or a successful pack download alone is not a full model-rendering test.
4. If stale cached content persists, disconnect and remove only the matching
   server-pack cache entry in the client's `.minecraft/server-resource-packs`,
   then rejoin. Do not remove worlds or server model data.
5. Follow [PLAYBOOK.md](PLAYBOOK.md) to configure locations and start activities.

The required ZIPs target Java 26.3. Geyser/Floodgate retain their existing
configuration; no Geyser upgrade was made. Java ZIP assets do not become Bedrock
resources automatically. Bedrock custom visuals need a separate native pack
and mappings; TSXMAS skips its Java seasonal ZIP for Floodgate players and keeps
command/text gameplay fallbacks. Older Java clients through ViaBackwards still
need separate pack compatibility testing.

If BetterModel models change, rebuild the venue pack from the new generated ZIP,
host the new artifact, verify its hash, and update URL/hash together. A static
hosted ZIP does not automatically incorporate future generated assets. Catbox's
FAQ states that anonymous files may be removed after two years without access;
retain the local ZIP and use an owner-controlled CDN for long-term hosting.

Official references:
[BetterModel 3.6.0 configuration](https://raw.githubusercontent.com/toxicity188/BetterModel/3.6.0/core/src/main/resources/config.yml),
[seasonal pack license metadata](https://api.modrinth.com/v2/project/BXBRe04Q),
[map and credits](https://www.planetminecraft.com/project/weihnachtsmc-christmas-village-with-santa-s-workshop/),
[Catbox upload API](https://catbox.moe/tools.php),
[Catbox retention](https://catbox.moe/faq.php).
