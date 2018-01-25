# Gitolite Notes

## Overview

*gitolite* runs under a single, normal user on the server and uses SSH public keys to differentiate access to Git repositories. *gitolite* offers per-repository, per-branch, and even some per-path access control.

Other possibility:

1. Change shell to [git-shell](https://git-scm.com/docs/git-shell). Don't know how hard it is yet.)
2. [Limit access of the users within ssh](https://prefetch.net/blog/index.php/2006/09/05/limiting-access-to-openssh-directives/).

(Find the clues from [this link](https://serverfault.com/questions/170048/create-ssh-user-with-limited-privileges-to-only-use-git-repository)...)

## Technical

Installation: `sudo apt-get install gitolite`

## References

1. [How gitolite uses ssh](http://gitolite.com/gitolite/glssh/index.html)
1. [How To Use Gitolite to Control Access to a Git Server on an Ubuntu 12.04 VPS ](https://www.digitalocean.com/community/tutorials/how-to-use-gitolite-to-control-access-to-a-git-server-on-an-ubuntu-12-04-vps)
