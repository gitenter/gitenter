FROM ubuntu:16.04

#################### Install Packages #####################

RUN apt-get update
RUN apt-get -y install openssh-server
RUN apt-get -y install git

###################### Setup Users ########################

RUN echo 'root:password' | chpasswd

RUN useradd -ms /bin/bash git
RUN echo 'git:password' | chpasswd

################## Setup SSH Connection ###################

RUN mkdir /var/run/sshd
#RUN sed -i 's/PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config
RUN sed -i 's/PermitRootLogin prohibit-password/PermitRootLogin forced-commands-only/' /etc/ssh/sshd_config

# SSH login fix. Otherwise user is kicked off after login
RUN sed 's@session\s*required\s*pam_loginuid.so@session optional pam_loginuid.so@g' -i /etc/pam.d/sshd

ENV NOTVISIBLE "in users profile"
RUN echo "export VISIBLE=now" >> /etc/profile

RUN mkdir -p /home/git/.ssh

# This is the pseudo-authorized key for testing purposes, which should be removed later.
ADD gitar/resources/id_rsa.pub /home/git/.ssh/authorized_keys

EXPOSE 22
CMD ["/usr/sbin/sshd", "-D"]

#USER git
#WORKDIR /home/git

#RUN mkdir -p .ssh

################## Setup GIT Connection ###################

EXPOSE 9418







