FROM ubuntu:16.04

#################### Install Packages #####################

RUN apt-get update

# Various installations need to be done before other setups,
# rather than to be done separately for the setup of indivitual
# services.
RUN apt-get -y install openssh-server
RUN apt-get -y install git
RUN apt-get -y install tomcat8

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

#RUN mkdir -p .ssh

################## Setup GIT Connection ###################

#USER git
#WORKDIR /home/git

#RUN git init --bare server.git
RUN git init --bare /home/git/server.git

EXPOSE 9418

###################### Setup Tomcat #######################

ADD capsid/target/capsid-0.0.1-alpha.war /var/lib/tomcat8/webapps

EXPOSE 8080
