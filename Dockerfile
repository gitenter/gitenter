FROM ubuntu:16.04

RUN apt-get update
RUN apt-get -y install openssh-server
RUN apt-get -y install git

RUN mkdir /var/run/sshd
RUN echo 'root:screencast' | chpasswd
RUN sed -i 's/PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config

# SSH login fix. Otherwise user is kicked off after login
RUN sed 's@session\s*required\s*pam_loginuid.so@session optional pam_loginuid.so@g' -i /etc/pam.d/sshd

ENV NOTVISIBLE "in users profile"
RUN echo "export VISIBLE=now" >> /etc/profile

### SSH port
EXPOSE 22
CMD ["/usr/sbin/sshd", "-D"]

### Git port
#EXPOSE 9418




#RUN useradd -ms /bin/bash git

#USER git
#WORKDIR /home/git

#RUN mkdir -p .ssh
