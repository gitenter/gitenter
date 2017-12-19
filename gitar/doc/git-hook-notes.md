# Git Hook Notes

Fire off custom scripts when certain important actions occur.

Format:

+ Examples as (1) Shell Script and (2) Perl thrown in.
+ Any properly named executable scripts (without any extension; remove the `.sample` in templates) will work fine.
+ May use Shell Scripts to bundled hook scripts.

[This site](https://www.digitalocean.com/community/tutorials/how-to-use-git-hooks-to-automate-development-and-deployment-tasks) gives a list of hooks and their associated commands.

Don't forget to make the hook file executable (using e.g. `chmod +x applypatch-msg`).

### Client side hook

Triggered by operations such as committing and merging.

Installed at `.git/hooks` under the repository directory.

Not copied when you `git clone` (so not a git-enforced policy).

Default:

```
applypatch-msg.sample  pre-applypatch.sample      pre-push.sample
commit-msg.sample      pre-commit.sample          pre-rebase.sample
post-update.sample     prepare-commit-msg.sample  update.sample
```

### Server side hook

Triggered when receiving push commits.

Used as a git-enforced policy.

Run before and after pushes to the server.

+ The `pre-receive` hooks can exit non-zero at any time to reject the push as well as print an error message back to the client.

Installed in the `hooks` directory of your bare remote repository, e.g. `repo.git/hooks/`. Default the same as client side (`pre-receive` and `post-receive` doesn't have samples).

#### `update`

Run once per branch. Only failed references (branches) will be rejected.

Takes three arguments:

1. The name of the reference (branch)
1. The SHA-1 that reference pointed to before the push
1. The SHA-1 the user is trying to push
1. (May have optional input of "user" -- SSH user. If using single user `git` with public key, need to have a shell wrapper to decide it.)

## References

1. Chapter 8.3 and 8.4 of Pro Git.
