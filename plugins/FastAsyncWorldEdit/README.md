# FastAsyncWorldEdit — World Editing Guide

FastAsyncWorldEdit (FAWE) is the asynchronous WorldEdit implementation used on
**TheySix Events** (`play.theysix.tech`) to edit and paste arena
schematics.

## Files

| File | Purpose |
|------|---------|
| `config.yml` | FAWE settings (**empty = defaults**) |
| `worldedit-config.yml` | WorldEdit-compatible settings (**empty = defaults**) |

## What it is used for here

- Selecting and pasting the arena builds in `PG-01` … `PG-06`.
- Routine world edits while building the hub/lobby.

## Commands

| Command | Effect |
|---------|--------|
| `//wand` | get the selection wand |
| `//pos1` / `//pos2` | set selection corners |
| `//copy` / `//paste` | copy/paste a selection |
| `//schem save <name>` | save a schematic |
| `//schem load <name>` | load a schematic |
| `//undo` / `//redo` | undo or redo an edit |

## Notes

- The shipped jar is `FastAsyncWorldEdit-Paper-2.15.4.jar`.
- Permission nodes are `fawe.*` (WorldEdit nodes such as `worldedit.*` also work).
- WorldGuard region selection uses the same `//wand` — see
  [`../WorldGuard/README.md`](../WorldGuard/README.md).
