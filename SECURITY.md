# Security and private configuration

Exclude credentials, passwords, tokens, webhook URLs, private SFTP addresses,
Floodgate keys and permission/database backups from Git. Use sanitized
`*.example` templates with placeholders such as `CHANGE_ME`.

Inspect staged files before committing. `.gitignore` does not protect files that
are already tracked. Untracking runtime files must preserve the local copies
unless deletion is explicitly approved.

Keep credential files user-owned and mode 600; verify host keys. Avoid exposing
secrets in logs, shell arguments, issues or screenshots. Report vulnerabilities
or exposed credentials privately to the repository owner, without posting a
working secret publicly.

Revoke or rotate a published secret first. Deleting it in a later commit does
not remove it from history. Coordinate history rewrites with the owner instead
of force-pushing automatically.

Review server changes, back up runtime state and obtain separate deployment
authorization. Keep permissions narrow and test protection as a normal player.
