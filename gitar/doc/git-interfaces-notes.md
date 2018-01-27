# Git Interfaces Notes

See a full list in [here](https://git.wiki.kernel.org/index.php/InterfacesFrontendsAndTools).

*(TODO: Need to be reorganized by different proposes. I am starting this survey by seeking git SSH permission control tools, but some comments below are not limited to that.)*

## Gitolite

(Conclusion: Not helpful for my case.)

An access control layer on top of git.

*gitolite* runs under a single, normal user on the server and uses SSH public keys to differentiate access to Git repositories. *gitolite* offers per-repository, per-branch, and even some per-path access control.

Problem solved:

1. Distinguish different users who all logged in as the same remote user `git`.
1. Per-repository access control: restrict user within their own repositories.
1. (Per-branch access control: alter the `update` hook)
1. (Per-path access control)

### SSH-gitolite collaboration

#### SSH side

##### `.ssh/authorized_keys` general

Every line is a public key from a particular user.

Every line can have a set of (1) options and (2) `command=` values to restrict the incoming users.

Without `command=` option, ssh gives you back the shell with full authorization.

##### special setup for gitolite

Every `.ssh/authorized_keys` line has the same `command=`

```
command="[path]/gitolite-shell some-username",[more options] ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEA18S2t...
```

`ssh` does:

1. Finds out which of the public keys in this file match the incoming login.
2. Setup the `SSH_ORIGINAL_COMMAND` environment variable, to save what originally the user want to run.
3. Execute the gitolite command, so gitolite know the username who's logged in.

#### gitolite side

`gitolite-shell` does:

1. Get (1) username, and (2) which repository you want to log in from `SSH_ORIGINAL_COMMAND`.
2. Look at the gitolite config file, to decide whether the user has the access to the repository.

gitolite shell stays in the middle of sshd and `git-receive-pack` (the command which triggers all popular server-side hooks: `pre-receive`/`update`/`post-receive`/`post-update`).

![](http://gitolite.com/gitolite/ov01.png "") => ![](http://gitolite.com/gitolite/ov02.png "")

### Setup

gitolite uses/clones a special git repository `gitolite-admin` to save its configurations. That do have the advantages of remembering the setup history (as it is done by git).

#### (My comments)

However, it doesn't fit my usage, as it is extremely hard to migrate the gitolite configuration to mine.

Other things realized:

+ Should works more on the direction of (advanced setups of) ssh, especially the parts about options and command setups.
+ Should use a library to work with SSH, rather than edit the by myself.
    + That can help not break e.g. `.ssh/authorized_keys`, not delete what shouldn't be deleted.
    + May save the keys in somewhere else, and update `.ssh/authorized_keys` every single time something is altered (that's what gitolite did: to save all the keys to a folder `/gitolite-admin/keydir`)

SSH library possibilities:

+ Java Secure Channel (SJch): http://www.jcraft.com/jsch/
    + Pro: A much more concise API than sshj
+ sshj: https://github.com/hierynomus/sshj
+ Apache Mina/Apache sshd: http://mina.apache.org/sshd-project/
    + Used by Gerrit.

My other question:

How can SSH handle a huge amount of private keys saved in a plain-text `.ssh/authorized_keys` file. Will it be really slowly if there's really a lot.

### Technical

#### Installation

Installation: (1) `sudo apt-get install gitolite3`. (2) `$ ssh-keygen -t rsa -C "ozoox.o@gmail.com"` to generate the key. (3) Set the admin's SSH key to be in path `/home/git/.ssh/id_rsa.pub_`

```
Initialized empty Git repository in /var/lib/gitolite3/repositories/gitolite-admin.git/
Initialized empty Git repository in /var/lib/gitolite3/repositories/testing.git/
WARNING: /var/lib/gitolite3/.ssh missing; creating a new one
    (this is normal on a brand new install)
WARNING: /var/lib/gitolite3/.ssh/authorized_keys missing; creating a new one
    (this is normal on a brand new install)
```

#### Java APIs

`java-gitolite-manager`: https://github.com/devhub-tud/Java-Gitolite-Manager

### Other alternatives

1. By Unix access control list (ACL). Cons: (1) Too many Unix users/passwords, (2) setup with multiple/tedious commands, (3) view premissions also with multiple commands.
1. Change ssh access from `shell` to [`git-shell`](https://git-scm.com/docs/git-shell), and work with it together with a set of Unix groups.
1. [Limit access of the users within ssh](https://prefetch.net/blog/index.php/2006/09/05/limiting-access-to-openssh-directives/).
1. Other git GUIS (gitolite is not a **G**-UI. It is invisible except the access is denied):
    1. [Gerrit Code Review](https://gerrit.googlesource.com/), a.k.a. [Google Git](https://gerrit.googlesource.com/): May actually consider using it.   (1) written in Java, (2) quote: "Gitolite uses a plain text config file; gerrit uses a database.".
    1. Gitlab, gogs, gitblit, ...: Less customizable.

(Find the clues from [this link](https://serverfault.com/questions/170048/create-ssh-user-with-limited-privileges-to-only-use-git-repository)...)

### References

1. [gitolite overview](http://gitolite.com/gitolite/overview/)
1. [How gitolite uses ssh](http://gitolite.com/gitolite/glssh/index.html)
1. [How To Use Gitolite to Control Access to a Git Server on an Ubuntu 12.04 VPS ](https://www.digitalocean.com/community/tutorials/how-to-use-gitolite-to-control-access-to-a-git-server-on-an-ubuntu-12-04-vps)

## Gerrit

A.k.a, Googit Git

Gerrit itself is for code review, which doesn't fit my propose. The part of Gerrit I am interested, is how it handles the access control while everybody is connecting through SSH using the same username `git`.

It does stays in [maven](https://mvnrepository.com/artifact/com.google.gerrit), but it doesn't come as a general library :-(

I haven't local which part of its code is working for that propose. Nor do I find any good references.

### References

1. [Gerrit Code Review - A Quick Introduction](https://review.openstack.org/Documentation/intro-quick.html): Not useful. Mostly the implementation part how to use it as a code review tool.
1. [Gerrit Documentations](https://gerrit-documentation.storage.googleapis.com/Documentation/2.14.5.1/index.html)
1. [Gerrit Code Review - Access Controls](https://review.openstack.org/Documentation/access-control.html): Too detailed and fit to the use case of itself. Mostly comes as a user manual.
