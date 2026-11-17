# Paper 26.3 upgrade assessment — 2026-11-14

**Historical assessment of the original 42 JARs.** The failures below were
subsequently addressed; the reconciled 47-plugin set passed an isolated 26.3
startup/status test and was deployed at the owner's request. Current evidence:
[upgrade deployment](../../paper26-upgrade/DEPLOYMENT.md). Keep these original
failures as a record of why the old artifacts must not be restored piecemeal.

Original decision: **do not upgrade production with the original JARs**.

Official Paper 26.3 build 251 (stable) was downloaded from Paper's downloads
service. SHA-256:
`3e7591f1c5b40a379c0ef494bcbeda128a62f5819e32cfc5e6335880bee39c55`.
Clean Paper reached Done on Temurin Java 25.0.5. An isolated second test copied
42 local plugin JARs, excluding retired TSEvent and prohibited plugins, and
used fresh configs and newly generated worlds. It failed startup and crashed.
Artifact hashes and sanitized enable failures are recorded in
[compatibility-26.3.json](compatibility-26.3.json).

| Existing artifact | Observed failure |
| --- | --- |
| FAWE 2.15.4 | Selected its 1.21.11 adapter; `NoSuchFieldError` for `Registries.CONFIGURED_FEATURE` |
| WorldGuard 7.0.17 | Missing `PermissionsResolverManager` after FAWE failed |
| PacketEvents 2.13.0 | Did not recognize 26.3 API version; assumed 1.8.8; reflection initialization failed |
| LibsDisguises 26.8.13 | Initialization and missing PacketEvents classes |
| FancyNpcs 2.12.0 | Command parser initialization error |
| FancyHolograms 2.12.0 | NPC listener registration error after FancyNpcs failure |
| nightcore 2.16.4 | Unknown server version and item-tag initialization error |
| WorldServer 1.9.4 | Version parser `ArrayIndexOutOfBoundsException` |

Fresh DiscordSRV config also lacks a bot token, and Vault has no configured
economy provider in this test. These are expected configuration limitations,
not evidence that credentials should be copied into source control. EssentialsX,
TSStaff and PlayerLogs are not all present among the local root JARs; this is
not a complete LIVE compatibility certification. BlueArcade modules and real
configuration were not copied. Passing load messages for other plugins do not
prove gameplay compatibility. Existing status-ping errors remain unresolved.

Next upgrade work: obtain reviewed 26.3-compatible releases for the affected
plugins, retain a compatible FAWE/WorldGuard pair, repeat the JAR-only test,
then test a private full server clone with secrets retained outside Git.
Preserve Geyser 2.12.0 unless a specific replacement is separately approved.
Do not reactivate retired TSEvent or EssentialsSpawn/Chat to fix missing pieces.
Verify AdvancedBanZ/BanWebhook and the Fancy vote leaderboard in game.

If upgrades cannot pass, an independent 26.3 Christmas instance is a possible
architecture decision for the owner; no proxy, routing, permissions, economy
sharing or new public endpoint has been created here.

Sources:

- [Paper 26.3 release and irreversible world upgrade warning](https://papermc.io/news/26-3/)
- [Official build metadata](https://fill.papermc.io/v3/projects/paper/versions/26.3/builds)
- [Geyser current supported versions](https://geysermc.org/wiki/geyser/supported-versions/)
- [WeihnachtsMC map page — advertises Java 26.3](https://www.planetminecraft.com/project/weihnachtsmc-christmas-village-with-santa-s-workshop/)
- [A Very Minecraft Christmas](https://modrinth.com/resourcepack/a-very-minecraft-christmas)

The local runtime observations above, rather than those pages, establish the
original JAR-set failures. At that assessment stage, no production upgrade or
world conversion had been done; see the deployment report for later work.
