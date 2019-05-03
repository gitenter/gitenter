FROM tomcat:latest

RUN ["rm", "-fr", "/usr/local/tomcat/webapps/ROOT"]
COPY ./hello-world.war /usr/local/tomcat/webapps/ROOT.war

CMD ["catalina.sh", "run"]
