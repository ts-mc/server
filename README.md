# TheySix server

Configuration and custom plugin sources for [TheySix Events](https://events.theysix.tech).
Repository: https://github.com/ts-mc/server

This checkout is configuration and project material, not a server backup.
Keep worlds, plugin JARs, player state, databases and credentials outside Git.
Copy sanitized `*.example` files to their corresponding local configuration paths
and fill in credentials locally. Existing installations should merge changes
rather than replace their live configuration.

- [Christmas plugin and operations](projects/tsxmas/README.md)
- [Paper 26.3 migration](projects/paper26-upgrade/README.md)
- [Contributing and validation](CONTRIBUTING.md)
- [Server operations](OPERATIONS.md)
- [Games](GAMES.md)
- [Security](SECURITY.md)
- [Change history](CHANGELOG.md)

`ts-sync.sh` only downloads a comparison snapshot to `.remote-sync/`. It reads
connection details from ignored `.vscode/sftp.json` and the protected credential
file at `~/.config/ts-server/sftp-password` (or `TS_SERVER_SFTP_CREDENTIAL`).
The credential directory keeps its historical name independently of the checkout.
Verify the host key before connecting; unknown hosts are not accepted automatically.

Deployment scripts contain explicit staging/activation operations. Running tests
or preparing a Git commit does not authorize a deployment.
