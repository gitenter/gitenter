# Tomcat Configuration Notes

## JVM version

(Ubuntu 14.04 w/ Tomcat 7)

It is kind of weird that Tomcat doesn't follow the system Java version. Rather, it tries to match the best JVM. See `/etc/init.d/tomcat7`:

```bash
OPENJDKS=""
find_openjdks
# The first existing directory is used for JAVA_HOME (if JAVA_HOME is not
# defined in $DEFAULT)
JDK_DIRS="/usr/lib/jvm/default-java ${OPENJDKS} /usr/lib/jvm/java-6-openjdk /usr/lib/jvm/java-6-sun /usr/lib/jvm/java-7-oracle"
```

In practice, you can see the conflicts:

```bash
$ sudo update-java-alternatives -s java-7-oracle
$ java -version
java version "1.7.0_80"
Java(TM) SE Runtime Environment (build 1.7.0_80-b15)
Java HotSpot(TM) 64-Bit Server VM (build 24.80-b11, mixed mode)
```

```bash
$ sh /usr/share/tomcat7/bin/version.sh
Using CATALINA_BASE:   /usr/share/tomcat7
Using CATALINA_HOME:   /usr/share/tomcat7
Using CATALINA_TMPDIR: /usr/share/tomcat7/temp
Using JRE_HOME:        /usr/lib/jvm/java-8-oracle
Using CLASSPATH:       /usr/share/tomcat7/bin/bootstrap.jar:/usr/share/tomcat7/bin/tomcat-juli.jar
Server version: Apache Tomcat/7.0.52 (Ubuntu)
Server built:   Feb 17 2017 03:37:39
Server number:  7.0.52.0
OS Name:        Linux
OS Version:     3.13.0-46-generic
Architecture:   amd64
JVM Version:    1.8.0_131-b11
JVM Vendor:     Oracle Corporation
```

One way to solve the problem is to edit `/etc/default/tomcat7` and to uncomment the `JAVA_HOME=` line to any of the available choices and then `sudo service tomcat7 restart`.

```
/usr/lib/jvm$ ls
default-java              java-1.7.0-openjdk-amd64  java-7-openjdk-amd64
java-1.5.0-gcj-4.8-amd64  java-6-openjdk-amd64      java-7-oracle
java-1.6.0-openjdk-amd64  java-6-openjdk-common     java-8-oracle
```

But it seems not working in my case. Reason unknown.
