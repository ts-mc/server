# Contributing

This repository contains TheySix configuration and plugin sources, not a server
backup. Read [operations](OPERATIONS.md) and [security](SECURITY.md) first.

## Workflow

1. Inspect Git status, the affected plugin and existing owner changes.
2. Compare local configuration, `.remote-sync/` and LIVE deliberately. A newer
   timestamp does not make a remote file authoritative.
3. Make targeted edits; preserve unrelated settings and legacy line endings.
4. Validate code/configuration and review staged filenames and the diff.
5. Commit and push when authorized. Sparked deployment requires separate approval.

Keep source, configs, intentional assets and sanitized `*.example` templates in
Git. Exclude JARs, worlds, player state, databases, logs, caches, backups and keys.
Merge reviewed settings into an installation; never blindly copy entire folders.
Ask before deleting owner material, historical records or assets.

## Validation

TSXMAS requires Java 25 and Maven 3.9+. From `projects/tsxmas`, run `mvn verify`.
From the repository root:

```sh
python3 -m unittest discover -s projects/tsxmas/tools -p 'test_*.py'
python3 -m compileall -q projects/tsxmas/tools projects/paper26-upgrade
bash -n ts-sync.sh plugins/TSStaff/build.sh
git diff --check
git diff --cached --check
```

Parse changed YAML, inspect staged filenames and scan for credentials/runtime
files. Follow [TSStaff's guide](plugins/TSStaff/README.md) to build that plugin.
Record what was actually tested. Unit tests do not establish Java/Bedrock gameplay,
reward delivery, skin downloads or compatibility with the complete server set.

## Architecture constraints

- BlueArcade3 runs normal minigames; TSXMAS Christmas logic is independent.
- BlueArcade rewards use built-in credits and XP. Keep FAWE as WorldGuard's provider.
- Retain EssentialsX, AdvancedBanZ/BanWebhook, Multiverse, Geyser/Floodgate and the
  FancyNpcs/FancyHolograms pair. Keep Geyser 2.12.0 until an update is approved.
- ItemJoin's configured Bedrock prefix is `BR_`; authentication uses Floodgate.
- Halloween/TSEvent, `event_lobby` and `event_arena` are retired. Do not invent a
  replacement `/spawn` destination or reactivate those systems.
- Seasonal display colors are `B3000C`, `E40010`, `D8D8D8`, `1FD537`, `00B32C`.
  Styling must preserve rank assignments, permission groups and balances.

Describe the problem, changes, validation and remaining manual checks in commits
and pull requests. Obtain paid plugin artifacts from the owner's licensed source.
