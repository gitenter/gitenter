FROM tomcat:8-jre11

RUN rm -rf /usr/local/tomcat/webapps/ROOT
COPY ./capsid-0.0.2-prototype.war /usr/local/tomcat/webapps/ROOT.war

CMD ["catalina.sh", "run"]
