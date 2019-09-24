# There's no easy way to let different profile to share the same docker image
# and inject environment variable for config. See comment in `docker-compose.yml`.
sed -i "s/spring.profiles.active=local/spring.profiles.active=docker/g" capsid/src/main/resources/application.properties
sed -i "s/		System.setProperty(\"spring.profiles.active\", \"local\");/		System.setProperty(\"spring.profiles.active\", \"docker\");/g" hooks/post-receive/src/main/java/com/gitenter/post_receive_hook/PostReceiveApplication.java

# Those changes are necessary to make unit test passing in docker build, for which
# the testing profile is defined in indivitual test (so always `local`) and not follow
# what is defined globally for the application.
#
# TODO:
# Is it possible to pass in those profiles in docker env variable, so we don't
# need to `sed` in here?
#
# Note:
# (1) We can in genral setup netowrk atlas in docker-compose, but `localhost`
# doesn't work.
# >  links:
# >    - "database:localhost"
# (2) In `protease` profiles are used for different git repo (e.g. `minimal`)
# and that's the reason it is not easy to setup profiles like local/docker/...
# like other package.
sed -i "s/		return getDataSource(\"localhost\");/		return getDataSource(\"postgres\");/g" protease/src/test/java/com/gitenter/protease/config/TestPostgresConfig.java
sed -i "s/			put(\"local\", \"localhost\");/			put(\"local\", \"postgres\");/g" hooks/post-receive/src/main/java/com/gitenter/post_receive_hook/config/PostgresConfig.java
sed -i "s/			put(\"local\", \"localhost\");/			put(\"local\", \"postgres\");/g" capsid/src/main/java/com/gitenter/capsid/config/PostgresConfig.java

# TODO:
# Consider moving this part to `java-build` in docker-compose definition.
mvn clean install
mvn compile assembly:single -f hooks/post-receive/pom.xml -DskipTests
mvn package -f capsid/pom.xml -DskipTests

sed -i "s/spring.profiles.active=docker/spring.profiles.active=local/g" capsid/src/main/resources/application.properties
sed -i "s/		System.setProperty(\"spring.profiles.active\", \"docker\");/		System.setProperty(\"spring.profiles.active\", \"local\");/g" hooks/post-receive/src/main/java/com/gitenter/post_receive_hook/PostReceiveApplication.java

sed -i "s/		return getDataSource(\"postgres\");/		return getDataSource(\"localhost\");/g" protease/src/test/java/com/gitenter/protease/config/TestPostgresConfig.java
sed -i "s/			put(\"local\", \"postgres\");/			put(\"local\", \"localhost\");/g" hooks/post-receive/src/main/java/com/gitenter/post_receive_hook/config/PostgresConfig.java
sed -i "s/			put(\"local\", \"postgres\");/			put(\"local\", \"localhost\");/g" capsid/src/main/java/com/gitenter/capsid/config/PostgresConfig.java
