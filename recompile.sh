project_home=/home/beta/Workspace/enterovirus

cd $project_home
cd gitar
mvn install
cd $project_home
cd protease
mvn install
cd $project_home
cd enzymark
mvn install
cd $project_home
cd gihook/postreceive
mvn clean compile assembly:single

cd $project_home
cd immunessh
sudo cp git-authorization.sh /home/git
sudo chmod +x /home/git/git-authorization.sh
mvn package
sudo cp target/immunessh-0.0.1-prototype /home/git

cd $project_home
cd capsid
mvn package
cp target/capsid-0.0.1-prototype.war /var/lib/tomcat8/webapps
