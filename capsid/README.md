## Development environment setting

### Dependencies

#### `gitar`

Although in eclipse/STS you can set up the dependency of `gitar` under `Properties -> Java Build Path -> Projects`, using command line tool `mvn package` you kind of need to fake the `gitar` package under `.m2`.

After successfully compiled and packaged the `gitar` package, navigate to the corresponding folder and run the following command:

```
mvn install:install-file -Dfile=gitar-0.0.1-alpha.jar -DgroupId=enterovirus.gitar -DartifactId=gitar -Dversion=0.0.1-alpha -Dpackaging=jar
```

(Try to set it in a local way in the `pom.xml` of `capsid`. Not successful yet. May refer to [here](https://stackoverflow.com/questions/2229757/maven-add-a-dependency-to-a-jar-by-relative-path) or [here](https://maven.apache.org/settings.html) more carefully some other day.)

### Lombok

To make Lombok work with the IDE, you need to not only add Lombok to `pom.xml`, but also setup the IDE dependency path.

Navigate to the Lombok `.jar` folder (`.m2/repository/org/projectlombok/lombok`) and `sudo java -jar lombok-1.16.18.jar`, then a GUI will be opened. Through the GUI, `specify Location...` and choose the STS execusion path (in my case `/opt/sts-bundle/sts-3.9.0.RELEASE/STS`), then `Install/Update`. Restart STS and the generated getters and setters works in eclipse `Outline`.

## Development

### STS

Although for deployment we should just `mvn package` and put the `.war` file in an existing Tomcat folder, for testing it is more convenient to run the Spring Boot application inside of STS.

To set it up, Go to `Run > Run configurations` and under the `Spring Boot` leave and Main type `enterovirus.capsid.CapsidApplication`. In Override Properties, you right click Add Row and set property as `server.port`, and value as `8888`. Then the site is accessible from `http://localhost:8888`.

## Deployment

Install Tomcat 8. Then `sudo chmod 777 /var/lib/tomcat8/webapps` and put the `.war` file into that folder. The site is accessible from `http://localhost:8080/capsid-0.0.1-alpha/`.

Need to update the git home folder in `enterovirus.capsid.config.GitConfig.java`.

## Testing URLs

```
http://localhost:8888/api/users/ann/
http://localhost:8888/api/organizations/gov
```

