# jar/war build by a different profile are identical. However, those changes
# are necessary to make unit test passing in docker build.
#
# TODO:
# Is it possible to pass in those profiles in docker env variable, so we don't
# need to `sed` in here?
sed -i'.original' -e "s/\t\tdataSource.setUrl(\"jdbc:postgresql:\/\/localhost:5432\/gitenter\");/\t\tdataSource.setUrl(\"jdbc:postgresql:\/\/postgres:5432\/gitenter\");/g" protease/src/test/java/com/gitenter/protease/config/TestPostgresConfig.java
sed -i'.original' -e "s/@ActiveProfiles(\"local\")/@ActiveProfiles(\"docker\")/g" hooks/post-receive/src/test/java/com/gitenter/post_receive_hook/service/UpdateDatabaseFromGitServiceTest.java

# TODO:
# Consider moving this part to the `dev` docker container.
mvn clean install
mvn compile assembly:single -f hooks/post-receive/pom.xml -DskipTests
mvn package -f capsid/pom.xml -DskipTests

sed -i'.original' -e "s/\t\tdataSource.setUrl(\"jdbc:postgresql:\/\/postgres:5432\/gitenter\");/\t\tdataSource.setUrl(\"jdbc:postgresql:\/\/localhost:5432\/gitenter\");/g" protease/src/test/java/com/gitenter/protease/config/TestPostgresConfig.java
sed -i'.original' -e "s/@ActiveProfiles(\"docker\")/@ActiveProfiles(\"local\")/g" hooks/post-receive/src/test/java/com/gitenter/post_receive_hook/service/UpdateDatabaseFromGitServiceTest.java
