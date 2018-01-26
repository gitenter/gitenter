# Gitolite Notes

*gitolite* runs under a single, normal user on the server and uses SSH public keys to differentiate access to Git repositories. *gitolite* offers per-repository, per-branch, and even some per-path access control.

Other possibility:

1. Change shell to [git-shell](https://git-scm.com/docs/git-shell). Don't know how hard it is yet.)
2. [Limit access of the users within ssh](https://prefetch.net/blog/index.php/2006/09/05/limiting-access-to-openssh-directives/).

(Find the clues from [this link](https://serverfault.com/questions/170048/create-ssh-user-with-limited-privileges-to-only-use-git-repository)...)

## gitolite solved

1. Distinguish different users who all logged in as the same remote user `git`.
1. Restrict user within their own repositories.

(Beside repository level restriction, gitolite can also do branch level restriction by alter the `update` hook)

### `.ssh/authorized_keys` does

Every line is a public key from a particular user.

Every line can have a set of (1) options and (2) `command=` values to restrict the incoming users.

Without `command=` option, ssh gives you back the shell with full authorization.

### gitolite does

Every `.ssh/authorized_keys` line has the same `command=`

```
command="[path]/gitolite-shell some-username",[more options] ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEA18S2t...
```

`ssh` does:

1. Finds out which of the public keys in this file match the incoming login.
2. Setup the `SSH_ORIGINAL_COMMAND` environment variable, to save what originally the user want to run.
3. Execute the gitolite command, so gitolite know the username who's logged in.

`gitolite-shell` does:

1. Get (1) username, and (2) which repository you want to log in from `SSH_ORIGINAL_COMMAND`.
2. Look at the gitolite config file, to decide whether the user has the access to the repository.

## Java APIs

`java-gitolite-manager`: https://github.com/devhub-tud/Java-Gitolite-Manager

## Technical

Installation: (1) `sudo apt-get install gitolite3`. (2) `$ ssh-keygen -t rsa -C "ozoox.o@gmail.com"` to generate the key. (3) Set the admin's SSH key to be in path `/home/git/.ssh/id_rsa.pub_`

```
Initialized empty Git repository in /var/lib/gitolite3/repositories/gitolite-admin.git/
Initialized empty Git repository in /var/lib/gitolite3/repositories/testing.git/
WARNING: /var/lib/gitolite3/.ssh missing; creating a new one
    (this is normal on a brand new install)
WARNING: /var/lib/gitolite3/.ssh/authorized_keys missing; creating a new one
    (this is normal on a brand new install)
```

## References

1. [gitolite overview](http://gitolite.com/gitolite/overview/)
1. [How gitolite uses ssh](http://gitolite.com/gitolite/glssh/index.html)
1. [How To Use Gitolite to Control Access to a Git Server on an Ubuntu 12.04 VPS ](https://www.digitalocean.com/community/tutorials/how-to-use-gitolite-to-control-access-to-a-git-server-on-an-ubuntu-12-04-vps)
