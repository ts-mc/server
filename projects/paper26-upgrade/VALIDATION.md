# Original bundle validation — 2026-11-14

Subsequent local/SFTP activation and the 47-plugin LIVE-set test are documented
in [DEPLOYMENT.md](DEPLOYMENT.md). The original bundle below remains a preparation
artifact, not the current deployment receipt.

Bundle: `.backups/paper26-upgrade/TheySix-Paper-26.3-staging.zip`

- Size: 246,274,532 bytes.
- SHA-256: `e72872e7372a5bc8968367600ef6f5dc75dbde50dd4e5063ce254563b6732fa1`.
- 46 top-level plugin JARs, 19 BlueArcade modules, 11 bundled minigame libraries,
  and official Paper 26.3 build 251.
- ZIP integrity passed. The server payload contains only JARs. No world files,
  databases, runtime YAML, Floodgate keys or private connection settings included.
- All candidate JAR hashes match the exact files used by the final runtime test.
- All original active root JAR hashes still match the pre-upgrade manifest.
  Existing server.jar, worlds and owner configuration were not replaced.

The final isolated test used Java 25.0.5, copies of WorldServer/Multiverse
settings, all candidate plugin JARs and the existing 19 minigame modules.
It reached Done, executed the console checks and stopped normally. There were
no plugin-enable failures or Java exception classes in the final log. A real
localhost status query returned Paper 26.3 / protocol 777.

Multiverse reported the nine configured production world folders missing from
the test instance, as expected because those worlds were never copied. An
optional LibsDisguises hosting network check failed. Fresh Discord configuration
does not establish a production bot connection. No claim is made that these
tests exercised world conversion, real player data or in-game behavior.

Five pure version-parser cases passed for the WorldServer source compatibility
patch. TSStaff 1.3.1 built successfully against the actual 26.3 API. EasyCommandBlocker
1.19.1 Paper/core source built successfully against the candidate dependency set.
Python utilities passed syntax compilation, TSStaff's build script passed shell
syntax validation, and `git diff --check` passed. Nothing entered the Git index.

The machine-readable test summary is [test-result.json](test-result.json). The
ZIP also contains manifests, hashes, an old-JAR removal list, installation notes
and corresponding source/provenance for the local builds. Raw vendor logs are
excluded because they can contain license/account identifiers.

The bundle is for **private staging acceptance**. See [README.md](README.md) for
the development-build choices, paid-core limitation, required full backup,
config/data migration checks and Java/Bedrock gameplay acceptance before LIVE.
