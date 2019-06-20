FROM ubuntu:18.04

RUN echo 'root:secretpassword' | chpasswd

RUN useradd -ms /bin/bash git
RUN echo 'git:secretpassword' | chpasswd

RUN apt-get update && apt-get -y install \
	openssh-server \
    git

# Below refers: https://docs.docker.com/engine/examples/running_ssh_service/

RUN mkdir /var/run/sshd
#RUN sed -i 's/PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config
RUN sed -i 's/PermitRootLogin prohibit-password/PermitRootLogin forced-commands-only/' /etc/ssh/sshd_config
RUN sed 's@session\s*required\s*pam_loginuid.so@session optional pam_loginuid.so@g' -i /etc/pam.d/sshd

ENV NOTVISIBLE "in users profile"
RUN echo "export VISIBLE=now" >> /etc/profile

# This is the pseudo-authorized key for testing purposes, which should be removed later.
#ADD id_rsa.pub /home/git/.ssh/authorized_keys

EXPOSE 22
CMD ["/usr/sbin/sshd", "-D"]

# $ ssh git@localhost -p 8822
# Works at this point.

# To use database as backend rather than `.ssh/authorized_keys` to save public keys
# for users to login, the following page listed most possibilities:
# https://serverfault.com/questions/162238/openssh-with-public-keys-from-database#answer-443230
# Also the following post have some useful information:
# https://serverfault.com/questions/117072/a-system-for-distributing-ssh-public-keys#answer-117549
# And what GitHub is doing (basically a patch on OpenSSH):
# https://github.blog/2009-10-20-how-we-made-github-fast/#tracing-an-ssh-request

# OpenSSH source code:
# https://github.com/openssh/openssh-portable/

# Specifies a program to be used to look up the user's public keys.
# http://manpages.ubuntu.com/manpages/xenial/man5/sshd_config.5.html
#
# TODO:
# The problem about this approach is, although the command `user_keys.sh` can accept
# $1 as any username and giving back a series of keys, the $1 username need to already
# exist in our system. By so, currently only `git` and `root` can work (without
# password needed).
# Probably can bypass it by adding every user (local/remote) to each `git` instance,
# so the file doesn't need to contain all users, but otherwise it becomes unavoidable
# to do a OpenSSH patch for public key lookup (input public key and return boolean)
# myself.
COPY ./user_keys.sh /usr/local/bin/user_keys.sh
RUN chmod a+x /usr/local/bin/user_keys.sh
RUN sed -i 's/#AuthorizedKeysCommand none/AuthorizedKeysCommand \/usr\/local\/bin\/user_keys.sh/' /etc/ssh/sshd_config
# Any user who can execute the above command can be used. And most operating systems
# have a nobody user. So we just use the default `nobody` user.
RUN sed -i 's/#AuthorizedKeysCommandUser nobody/AuthorizedKeysCommandUser nobody/' /etc/ssh/sshd_config

# Other tools includes:
# (1) There's a patch to access public keys through an LDAP server which may be helpful:
# https://code.google.com/archive/p/openssh-lpk/
# (2) pam_ldap (?): https://linux.die.net/man/5/pam_ldap
# (3) ssh-keydb: https://code.google.com/archive/p/ssh-keydb/
# Not useful. This is just dynamically generate `authorized_keys` from db content
# which is basically a trivial task (especialluy after `AuthorizedKeysCommand` is
# included in standard OpenSSH).
# (4) SSH Tectia Client which is not free (the company own `ssh.com`) so we'll not
# consider it.
# https://www.ssh.com/manuals/server-admin/44/Pluggable_Authentication_Module__PAM__Submethod.html
# (5) openssh-script-auth: https://github.com/mizzy/openssh-script-auth
# It is similar to `AuthorizedKeysCommand` which just generate a file, rather than
# using the public key as input and give boolean about whether the user can login
# or not. Not usefor for our case.

# We can't replace `.ssh/authorized_keys` through ssh pluggable authentication modules (PAM),
# as this part of logic is inside of OpenSSH rather than through PAM. `/etc/pam.d/sshd`
# file only defines how to authorize a user based on his/her system username/password.
# In Ubuntu, `/etc/pam.d/sshd` imports `/etc/pam.d/common-auth` which triggers `pam_unix.so`
# which checks if the system username/password is matched (other systems are slightly
# different).
#
# Notes:
# (1) Knowledge about PAM:
# https://mirrors.edge.kernel.org/pub/linux/libs/pam/
# https://access.redhat.com/documentation/en-us/red_hat_enterprise_linux/6/html/managing_smart_cards/pluggable_authentication_modules
# https://www.pks.mpg.de/~mueller/docs/suse10.2/html/opensuse-manual_en/manual/cha.pam.html
# http://manpages.ubuntu.com/manpages/bionic/man7/PAM.7.html
# (2) `pam_unix.so` username/password authorization:
# http://manpages.ubuntu.com/manpages/trusty/man8/pam_unix.8.html
# (3) A full list of modules PAM refers can be checked by `ls /lib/*/security`.
# (4) `pam.d` configuration files cannot be freely edited, as they'll be overwritten
# by `pam-auth-update`. The correct way is to edit `/etc/security/*.conf` files.
# http://manpages.ubuntu.com/manpages/xenial/man8/pam-auth-update.8.html
# https://www.digitalocean.com/community/tutorials/how-to-use-pam-to-configure-authentication-on-an-ubuntu-12-04-vps
# http://www.rjsystems.nl/en/2100-pam-debian.php
# https://askubuntu.com/questions/574845/what-is-the-correct-way-to-modify-etc-pam-d-common-auth
# (5) `/etc/pam.d/common-password` is for password changing so irrelavent. Although it
# has similar content to `/etc/pam.d/common-auth`.
#
# Relevant post on PAM:
# https://www.linuxquestions.org/questions/linux-security-4/openssh-authentication-pam-428406/

# Both MySQL and Postgres have PAM support. However, those are mainly for users to
# log into the database (rather than to use database data to decide which user can
# log in). In addition, I am not sure it is only in username/password level or it
# can replace `authorized_keys`.
# https://dev.mysql.com/doc/refman/5.5/en/pam-pluggable-authentication.html
# https://www.postgresql.org/docs/11/auth-pam.html
#
# Relevant post:
# https://serverfault.com/questions/608445/manage-ssh-users-with-mysql-backend

# TODO:
# Setup non-interactive login so user cannot login through SSH.

# TODO: Remove it.
RUN git init --bare /home/git/server.git

WORKDIR /home/git

# Can't set user to be git. Otherwise there are authorization problem
# to load SSH config files. However, also cannot loose file mod (600->644),
# as then SSH will complain that it is too loose so not secure.
#USER git

# $ git clone ssh://git@0.0.0.0:8822/home/git/server.git
# Works.
#
# $ git clone git@0.0.0.0:/home/git/server.git
# or
# $ git clone git@0.0.0.0:8822/home/git/server.git
# Not work. The reason is if there's a customized port, then
# `git@` is not supported and only `ssh://git` works.
# https://git-scm.com/docs/git-clone#_git_urls_a_id_urls_a
# Error message:
# > Cloning into 'server'...
# > Pseudo-terminal will not be allocated because stdin is not a terminal.
# Tried the following but doesn't work:
#RUN sed -i 's/#PermitTTY yes/PermitTTY no/' /etc/ssh/sshd_config
