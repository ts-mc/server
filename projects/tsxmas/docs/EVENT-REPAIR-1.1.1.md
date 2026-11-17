# TSXMAS 1.1.1 event repair

LIVE was stopped for this deployment. Its copied SQLite database had player-name
state but no event state rows: all activities therefore defaulted to STOPPED.
No database records or physical map blocks were changed by deployment.

## After starting Paper

Run `/xmas start ready`, then immediately run the `/xmas confirm <code>` printed
by the plugin. This explicitly starts only stopped activities whose locations
validate; the unconfigured build contest is skipped. Existing paused/running
activities stay as they are. `/xmas staff` shows each activity's state.

Use `/xmas delivery` for the route menu, or right-click Santa to start. Santa's
actual NPC position is accepted in addition to the saved workshop point.
Right-click the mailbox or bamboo fence near each existing house checkpoint.
The menu lists remaining deliveries and coordinates. The delivery match radius
is configurable in events.yml (horizontal 1.5 blocks, vertical 2.5 blocks).
If a newly built mailbox is farther away, save that house checkpoint at the
mailbox with `/xmas setlocation house-one` (or house-two/house-three).
Do not reset player progress to repair a marker.

Active delivery routes take priority over hunt discoveries when markers overlap.
Hunt matching remains exact; `/xmas hunt` offers objectives and completion state
without revealing coordinates. Grinch remains a command/menu checkpoint after
completing hunt and delivery, at the owner's stone-brick platform.

## NPCs and holograms

Santa and merchant receive FancyNpcs skins from the public Minecraft profiles
SantaClaus and MHF_Villager. Skin resolution requires the external skin services;
profile owners can change their textures. Change `npcs.<id>.skin` to another
supported FancyNpcs skin identifier, then reload. Skins are not custom commissioned
textures, and visual appearance still requires an in-game check.

`holograms.santa.npc: santa` and `holograms.market.npc: market` anchor their labels
to the NPC position plus the configurable `height` (2.7 blocks). Original saved
NPC/hologram coordinates are retained. Existing market text was absent; it is
now populated. Calendar, journey, Santa and merchant text use the approved five
Christmas colours. `/xmas setnpc` immediately refreshes attached labels.

Twelve bounded `tsxmas-interact-*` WorldGuard regions allow use/interact around
configured event points and NPCs at priority 10. They retain chest-access deny
and do not grant building membership or bypass the village's protections.
Review these regions after relocating event markers, since they are not moved
by TSXMAS. No production lobby or minigame regions are changed.

## Validation

46 Maven tests and YAML semantic preservation checks passed before
upload. Tests cover mailbox height/adjacency, distant markers, exact hunt matching,
and oversized radius rejection. Existing coordinates, plot definitions, boundary,
resource-pack policy and player records are preserved. The agent did not start
Paper; verify NPC skin downloads, labels, WorldGuard interaction and a complete
route with a normal Java/Bedrock player after startup.

The deployment receipt records the remote rollback folder. Restore the saved
JAR and changed configurations together while stopped if rollback is needed.
