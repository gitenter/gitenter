project_home=/home/beta/Workspace/enterovirus

cd $project_home
cd gitar
mvn clean install
cd $project_home
cd protease
mvn clean install
cd $project_home
cd enzymark
mvn clean install
cd $project_home
cd gihook/postreceive
mvn clean compile assembly:single

cd $project_home
cd immunessh
sudo cp git-authorization.sh /home/git
sudo chmod +x /home/git/git-authorization.sh
mvn clean compile assembly:single
sudo cp target/immunessh-0.0.1-prototype-jar-with-dependencies.jar /home/git

cd $project_home
cd capsid

sed -i "s/spring.profiles.active=sts/#spring.profiles.active=sts/g" $project_home/capsid/src/main/resources/application.properties

sed -i "s/#spring.profiles.active=localhost/spring.profiles.active=localhost/g" $project_home/capsid/src/main/resources/application.properties
#sed -i "s/#spring.profiles.active=production/spring.profiles.active=production/g" $project_home/capsid/src/main/resources/application.properties

mvn clean package
cp target/capsid-0.0.1-prototype.war /var/lib/tomcat8/webapps

sed -i "s/#spring.profiles.active=sts/spring.profiles.active=sts/g" $project_home/capsid/src/main/resources/application.properties

sed -i "s/spring.profiles.active=localhost/#spring.profiles.active=localhost/g" $project_home/capsid/src/main/resources/application.properties
#sed -i "s/spring.profiles.active=production/#spring.profiles.active=production/g" $project_home/capsid/src/main/resources/application.properties
