# TSStaff 1.3.0

TSStaff provides chat clearing, moderation tools, announcements, and `/update`.
`/update` scans loaded plugins and reads installed versions from Paper's
`logs/latest.log`, falling back to each plugin's metadata when the startup log
does not contain a version. It opens a read-only paginated GUI and checks
configured Modrinth and Hangar projects for stable releases compatible with
Paper 1.21.11. Plugins without a configured source are marked for manual
review. It never downloads or replaces JARs.

The plugin sources are under `src/main/java/`. Build against the Paper API
embedded in the matching server archive with `./build.sh [server.jar]`; the
default output is `/tmp/TSStaff-1.3.0.jar`, outside the repository. Set
`TSSTAFF_JDK` to a Java 21 JDK directory when `javac` is not on `PATH`.

TSStaff declares these permission nodes:

| Node | Suggested access |
| --- | --- |
| `tsstaff.clearchat` | All staff: helper, moderator, headmoderator, admin, manager, owner |
| `tsstaff.mutechat`, `tsstaff.mutechat.bypass` | Moderator and above |
| `tsstaff.freeze`, `tsstaff.freeze.notify`, `tsstaff.freeze.exempt` | Moderator and above |
| `tsstaff.announce` | Moderator and above |
| `tsstaff.update` | Staff authorized to inspect plugin versions |
| `tsstaff.freeze.override`, `tsstaff.reload`, `tsstaff.antispam.bypass` | Admin, manager, owner |
| `tshide.use` | Moderator and above |

The JAR defaults these nodes to operator access. Grant the intended nodes with
LuckPerms; do not make the staff commands public.

The runtime `config.yml` contains saved moderation state and stays local/ignored.
Use `config.yml.example` as the sanitized configuration template.
