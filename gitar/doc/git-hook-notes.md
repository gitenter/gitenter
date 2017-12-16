# Git Hook Notes

Fire off custom scripts when certain important actions occur.

Format:

+ Examples as (1) Shell Script and (2) Perl thrown in.
+ Any properly named executable scripts (without any extension; bundled hook scripts end with `.sample`) will work fine.

### Client side hook

Triggered by operations such as committing and merging.

Installed at `.git/hooks` under the repository directory.

### Server side hook

Triggered when receiving push commits.

Run before and after pushes to the server.

+ The `pre-receive` hooks can exit non-zero at any time to reject the push as well as print an error message back to the client.

Installed in the `hooks` directory of your bare remote repository, e.g. `repo.git/hooks/`.

## References

1. Chapter 8.3 of Pro Git.
