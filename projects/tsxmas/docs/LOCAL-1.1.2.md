# TSXMAS 1.1.2 — local changes only

Prepared on November 17, 2026. This version has not been deployed or tested
on a running Minecraft server. The previous LIVE deployment remains 1.1.1.

- Hunt and delivery menus paginate beyond the first five objectives and keep
  every configured objective reachable. Delivery start has a dedicated slot.
- Holograms reject missing NPC anchors and non-finite or out-of-range heights
  during configuration loading, before replacing the running settings.
- A skin API failure falls back to a default skin so an optional skin cannot
  remove access to an NPC's event command.

Use `/xmas hunt menu <page>` or `/xmas delivery menu <page>` to open a page;
next/previous arrows navigate the GUI. Display labels are configurable.

Build with Java 25 and `mvn verify` from this project. Deployment remains a
separate action requiring explicit authorization.
