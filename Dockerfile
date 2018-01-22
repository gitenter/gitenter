FROM ubuntu:16.04

###################### Setup UTF-8 ########################

RUN apt-get update && \
	apt-get -y install locales

RUN locale-gen en_US.UTF-8
ENV LANG en_US.UTF-8
ENV LC_ALL en_US.UTF-8

RUN apt-get update && \
	apt-get dist-upgrade -y

###################### Setup Users ########################

RUN echo 'root:secretpassword' | chpasswd

RUN useradd -ms /bin/bash git
RUN echo 'git:secretpassword' | chpasswd

###################### Install Java #######################

#RUN apt-get update && \
#	apt-get install -y software-properties-common && \
#	apt-add-repository ppa:webupd8team/java && \
#	apt-get update -y && \
#	apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys EEA14886 && \
#	echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections && \
#	apt-get install -y oracle-java8-installer && \
#	apt-get install -y oracle-java8-unlimited-jce-policy && \
#	apt-get clean && \
#	rm -rf /var/lib/apt/lists/* && \
#	rm -rf /var/cache/oracle-jdk8-installer

# Refer to: https://www.digitalocean.com/community/tutorials/how-to-install-java-with-apt-get-on-ubuntu-16-04
RUN apt-get update && \ 
	apt-get install -y openjdk-8-jdk && \ 
	apt-get clean && \ 
	rm -rf /var/lib/apt/lists/* && \ 
	rm -rf /var/cache/oracle-jdk8-installer;

ENV JAVA_HOME JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64/
RUN export JAVA_HOME

################## Setup SSH Connection ###################

RUN apt-get update && \
	apt-get -y install openssh-server

RUN mkdir /var/run/sshd
#RUN sed -i 's/PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config
RUN sed -i 's/PermitRootLogin prohibit-password/PermitRootLogin forced-commands-only/' /etc/ssh/sshd_config

# SSH login fix. Otherwise user is kicked off after login
RUN sed 's@session\s*required\s*pam_loginuid.so@session optional pam_loginuid.so@g' -i /etc/pam.d/sshd

ENV NOTVISIBLE "in users profile"
RUN echo "export VISIBLE=now" >> /etc/profile

RUN mkdir -p /home/git/.ssh

# This is the pseudo-authorized key for testing purposes, which should be removed later.
ADD id_rsa.pub /home/git/.ssh/authorized_keys

EXPOSE 22
CMD ["/usr/sbin/sshd", "-D"]

#RUN mkdir -p .ssh

################## Setup GIT Connection ###################

RUN apt-get update && \
	apt-get -y install git

#USER git
#WORKDIR /home/git

#RUN git init --bare server.git
RUN git init --bare /home/git/server.git

EXPOSE 9418

#################### Setup PostgreSQL #####################

RUN apt-get update && \
	apt-get -y install postgresql postgresql-contrib

###################### Setup Tomcat #######################

RUN apt-get update && \
	apt-get -y install tomcat8

ADD capsid/target/capsid-0.0.1-prototype.war /var/lib/tomcat8/webapps

ENV CATALINA_HOME /usr/share/tomcat8
ENV CATALINA_BASE /var/lib/tomcat8
ENV CATALINA_PID /var/run/tomcat8.pid
ENV CATALINA_SH /usr/share/tomcat8/bin/catalina.sh

EXPOSE 8080
CMD [ "/usr/share/tomcat8/bin/catalina.sh", "run" ]
