FROM ubuntu:16.04
MAINTAINER Cong-Xin Qiu <ozooxo@gmail.com>

RUN apt-get update && \
	apt-get install -y \
		openjdk-8-jdk \
		tomcat8 && \
	apt-get clean && \
	rm -rf /var/lib/apt/lists/* && \
	rm -rf /var/cache/oracle-jdk8-installer;

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64/
RUN export JAVA_HOME

# Copy the war file to the ROOT, so it can be accessed through tomcat root.
# Refer to http://www.baeldung.com/tomcat-root-application
RUN rm -rf /var/lib/tomcat8/webapps/ROOT
COPY hello-world.war /var/lib/tomcat8/webapps/ROOT.war

RUN sed -i "s/    <Connector port=\"8080\" protocol=\"HTTP\/1.1\"/    <Connector port=\"80\" protocol=\"HTTP\/1.1\"/g" /var/lib/tomcat8/conf/server.xml

ENV CATALINA_HOME /usr/share/tomcat8
ENV CATALINA_BASE /var/lib/tomcat8
ENV CATALINA_PID /var/run/tomcat8.pid
ENV CATALINA_SH /usr/share/tomcat8/bin/catalina.sh

EXPOSE 80
CMD [ "/usr/share/tomcat8/bin/catalina.sh", "run" ]