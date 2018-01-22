HOME=/home/beta/Workspace/enterovirus
#scp -i "ec2-key-pair.pem" -r $HOME/database ubuntu@ec2-52-24-221-137.us-west-2.compute.amazonaws.com:/tmp/database
#scp -i "ec2-key-pair.pem" $HOME/capsid/target/capsid-0.0.1-prototype.war ubuntu@ec2-52-24-221-137.us-west-2.compute.amazonaws.com:/var/lib/tomcat8/webapps/ROOT.war
scp -i "ec2-key-pair.pem" $HOME/docker-dummy/tomcat/hello-world.war ubuntu@ec2-52-24-221-137.us-west-2.compute.amazonaws.com:/var/lib/tomcat8/webapps/hello-world.war
