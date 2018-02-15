HOME=/home/beta/Workspace/enterovirus

scp -i "ec2-key-pair.pem" -r reset.sh ubuntu@ec2-52-41-66-37.us-west-2.compute.amazonaws.com:/tmp/reset.sh
scp -i "ec2-key-pair.pem" -r deploy.sh ubuntu@ec2-52-41-66-37.us-west-2.compute.amazonaws.com:/tmp/deploy.sh
scp -i "ec2-key-pair.pem" -r $HOME/database ubuntu@ec2-52-41-66-37.us-west-2.compute.amazonaws.com:/tmp/database

scp -i "ec2-key-pair.pem" $HOME/immunessh/git-authorization.sh ubuntu@ec2-52-41-66-37.us-west-2.compute.amazonaws.com:/tmp/git-authorization.sh
scp -i "ec2-key-pair.pem" $HOME/immunessh/target/immunessh-0.0.2-prototype-jar-with-dependencies.jar ubuntu@ec2-52-41-66-37.us-west-2.compute.amazonaws.com:/tmp/immunessh-0.0.1-prototype-jar-with-dependencies.jar

# Copy it to tmp and let user "git" to copy it to /var/lib/tomcat8/webapps
#scp -i "ec2-key-pair.pem" $HOME/capsid/target/capsid-0.0.2-prototype.war ubuntu@ec2-52-41-66-37.us-west-2.compute.amazonaws.com:/var/lib/tomcat8/webapps/ROOT.war
scp -i "ec2-key-pair.pem" $HOME/capsid/target/capsid-0.0.2-prototype.war ubuntu@ec2-52-41-66-37.us-west-2.compute.amazonaws.com:/tmp/ROOT.war
