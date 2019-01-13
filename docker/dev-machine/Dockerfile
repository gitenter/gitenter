FROM ubuntu:18.04

RUN apt-get update && apt-get -y install \
    apt-utils \
    openjdk-11-jdk \
    maven \
    python3-pip

# There's no openjdk-10 but only has openjdk-11 on Ubuntu 18.04
# (https://askubuntu.com/questions/1037646/why-is-openjdk-10-packaged-as-openjdk-11).
# However, by unknown reasons after installation `java --version`
# will give back Java 10.
#
# Try to install Java 10 (Oracle) through `ppa:linuxuprising/java`; however,
# it has been removed.
#
#RUN apt-get update && apt-get -y install software-properties-common
#RUN add-apt-repository ppa:linuxuprising/java
#
# Try also install Java 10 manually. `update-alternatives --install` doesn't work.
# After executing that `update-alternatives --config java` or `update-java-alternatives --list`
# will not see the newly added one.
#
#RUN apt-get update && apt-get -y install wget
#WORKDIR /download
#RUN wget -O ./openjdk-10_linux-x64_bin.tar.gz "https://download.java.net/java/GA/jdk10/10.0.2/19aef61b38124481863b1413dce1855f/13/openjdk-10.0.2_linux-x64_bin.tar.gz"
#RUN tar xzvf openjdk-10_linux-x64_bin.tar.gz
#RUN mv jdk-10.0.2 /usr/lib/jvm/java-10.0.2-openjdk-amd64/
#RUN update-alternatives --install /usr/bin/java java /usr/lib/jvm/java-10.0.2-openjdk-amd64/bin/java 1103
#RUN update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/java-10.0.2-openjdk-amd64/bin/javac 1103

# Right now, `mvn test` of gitar doesn't work in this environment.
# `java.io.File.setReadOnly()` doesn't work, and after that `canWrite()`
# still gives true. Seems create file if not exist is also behaving
# differently. That will fail tests, e.g.
# testInitFolderReadOnly(com.gitenter.gitar.GitBareRepositoryTest)
# testInitFolderNotExist(com.gitenter.gitar.GitBareRepositoryTest)
# However, build package can be done without any problem, by using
# `mvn clean install -DskipTests` inside of the container.
#
# The other problem (people said that's a problem between Java 10 and 11) is
# ```
# Illegal reflective access by com.google.inject.internal.cglib.core.$ReflectUtils$1
# (file:/usr/share/maven/lib/guice.jar) to method
# java.lang.ClassLoader.defineClass(java.lang.String,byte[],int,int,java.security.ProtectionDomain)
# ```
# So if we
#WORKDIR /gitar
#RUN mvn clean install -DskipTests
# The warning is raised, and then the image build fails. So we currently have no
# solution except login into the container and do it manually.
# ```
# cd /gitar
# mvn clean install -DskipTests
# ```
#
# TODO:
# Probably just upload gitar to maven so we don't need to worry about that forever.

RUN apt-get -y install postgresql-client

WORKDIR /gitenter

# Although it is unforturnate that gitar install need to manually execute first,
# if we change `TestDatabaseConfig` to "jdbc:postgresql://database:5432/gitenter",
# tests in `protease` can pass with no problem (`mvn test`).
#
# TODO:
#
# (1) `/root/.m2` is not cached, so every time we restart the container,
# maven need to load the libraries again. One possibility is to just mirror
# `/root/.m2` to the local one, but that mixes up the dependency and no longer
# makes the docker environment a clean starting point. Check
# https://stackoverflow.com/questions/42208442/maven-docker-cache-dependencies
# https://github.com/carlossg/docker-maven/blob/322d0dff5d0531ccaf47bf49338cb3e294fd66c8/jdk-8/Dockerfile#L14
# to see if there are other ideas.
#
# (2) Right now the database has the hostname `database`. Although we can use
# `link` to setup an atlas, the atlas `localhost` doesn't work.
# ```
#   links:
#     - "database:localhost"
# ```
# So we need to either change the Java code (or better, have different Spring
# profiles but need to change to the correct one inside of the container), or
# setup some SSHing to make `localhost:5432` inside of this container to
# `database:5432`, or setup an environmental variable `DATABASE_URL` and inject
# it to the docker container.
