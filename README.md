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

## SFTP helper

Copy `theysix.sh.example` to ignored `theysix.sh`, and
`.vscode/sftp.json.example` to ignored `.vscode/sftp.json`. Fill in connection
fields only; never add a password to these files. Create the external mode-600 credential file using the commands below,
which prompt without putting the password in shell history. Run `bash ./theysix.sh snapshot` to refresh comparison files.
The `help` action does not connect. This helper does not upload or deploy.

The external credential is used by the shell and Python tools. Editor SFTP
extensions have their own authentication setup; enter credentials through the
extension's prompt or configure SSH authentication rather than saving a password
in JSON.

```sh
install -d -m 700 "$HOME/.config/ts-server"
(umask 077
 read -r -s -p 'SFTP password: ' secret
 printf '\n'
 printf '%s\n' "$secret" > "$HOME/.config/ts-server/sftp-password"
 unset secret)
chmod 600 "$HOME/.config/ts-server/sftp-password"
```
