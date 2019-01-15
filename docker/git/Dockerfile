FROM ubuntu:18.04

RUN echo 'root:secretpassword' | chpasswd

RUN useradd -ms /bin/bash git
RUN echo 'git:secretpassword' | chpasswd

RUN apt-get update && apt-get -y install \
	openssh-server \
    git

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

# Above refers: https://docs.docker.com/engine/examples/running_ssh_service/
# At this point, `ssh git@localhost -p 8822` from host works.

RUN git init --bare /home/git/server.git

#ENTRYPOINT ["git"]
#CMD ["--help"]

EXPOSE 9418

#USER git
WORKDIR /home/git

#git clone git@0.0.0.0:52418/home/git/server.git
#
#Cloning into 'server'...
#Pseudo-terminal will not be allocated because stdin is not a terminal.
#Password:
#Password:
#Password:
#git@0.0.0.0's password:
#Permission denied, please try again.
#git@0.0.0.0's password:
#
# Tried the following but doesn't work:
#RUN sed -i 's/#PermitTTY yes/PermitTTY no/' /etc/ssh/sshd_config
