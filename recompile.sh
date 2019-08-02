project_home=~/Workspace/gitenter

cd $project_home
mvn clean install
cd hooks/post-receive
mvn clean compile assembly:single

cd $project_home
cd immunessh
sudo cp git-authorization.sh /home/git
sudo chmod +x /home/git/git-authorization.sh
mvn clean compile assembly:single
sudo cp target/immunessh-0.0.2-prototype-jar-with-dependencies.jar /home/git

cd $project_home
cd capsid

sed -i "s/spring.profiles.active=local/#spring.profiles.active=local/g" $project_home/capsid/src/main/resources/application.properties

#sed -i "s/#spring.profiles.active=localhost/spring.profiles.active=localhost/g" $project_home/capsid/src/main/resources/application.properties
sed -i "s/#spring.profiles.active=production/spring.profiles.active=production/g" $project_home/capsid/src/main/resources/application.properties

mvn clean package
cp target/capsid-0.0.2-prototype.war /var/lib/tomcat8/webapps

sed -i "s/#spring.profiles.active=local/spring.profiles.active=local/g" $project_home/capsid/src/main/resources/application.properties

#sed -i "s/spring.profiles.active=localhost/#spring.profiles.active=localhost/g" $project_home/capsid/src/main/resources/application.properties
sed -i "s/spring.profiles.active=production/#spring.profiles.active=production/g" $project_home/capsid/src/main/resources/application.properties
