sed -i'.original' -e "s/spring.profiles.active=local/spring.profiles.active=docker/g" capsid/src/main/resources/application.properties
sed -i'.original' -e "s/		System.setProperty(\"spring.profiles.active\", \"local\");/		System.setProperty(\"spring.profiles.active\", \"docker\");/g" hooks/post-receive/src/main/java/com/gitenter/post_receive_hook/PostReceiveApplication.java
sed -i'.original' -e "s/\t\tdataSource.setUrl(\"jdbc:postgresql:\/\/localhost:5432\/gitenter\");/\t\tdataSource.setUrl(\"jdbc:postgresql:\/\/postgres:5432\/gitenter\");/g" protease/src/test/java/com/gitenter/protease/config/TestPostgresConfig.java
sed -i'.original' -e "s/@ActiveProfiles(\"local\")/@ActiveProfiles(\"docker\")/g" hooks/post-receive/src/test/java/com/gitenter/post_receive_hook/service/UpdateDatabaseFromGitServiceTest.java

# TODO:
# Consider moving this part to the `dev` docker container.
mvn clean install
mvn compile assembly:single -f hooks/post-receive/pom.xml -DskipTests
mvn package -f capsid/pom.xml -DskipTests

sed -i'.original' -e "s/spring.profiles.active=docker/spring.profiles.active=local/g" capsid/src/main/resources/application.properties
sed -i'.original' -e "s/		System.setProperty(\"spring.profiles.active\", \"docker\");/		System.setProperty(\"spring.profiles.active\", \"local\");/g" hooks/post-receive/src/main/java/com/gitenter/post_receive_hook/PostReceiveApplication.java
sed -i'.original' -e "s/\t\tdataSource.setUrl(\"jdbc:postgresql:\/\/postgres:5432\/gitenter\");/\t\tdataSource.setUrl(\"jdbc:postgresql:\/\/localhost:5432\/gitenter\");/g" protease/src/test/java/com/gitenter/protease/config/TestPostgresConfig.java
sed -i'.original' -e "s/@ActiveProfiles(\"docker\")/@ActiveProfiles(\"local\")/g" hooks/post-receive/src/test/java/com/gitenter/post_receive_hook/service/UpdateDatabaseFromGitServiceTest.java
