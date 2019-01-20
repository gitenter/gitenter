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
