## Development environment setting

### Dependencies

#### `Gitar`

Although in eclipse/STS you can set up the dependency of `gitar` under `Properties -> Java Build Path -> Projects`, using command line tool `mvn package` you kind of need to fake the `gitar` package under `.m2`.

After successfully compiled and packaged the `gitar` package, navigate to the corresponding folder and run the following command:

```
mvn install:install-file -Dfile=gitar-0.0.1-alpha.jar -DgroupId=enterovirus.gitar -DartifactId=gitar -Dversion=0.0.1-alpha -Dpackaging=jar
```

(Try to set it in a local way in the `pom.xml` of `capsid`. Not successful yet. May refer to [here](https://maven.apache.org/settings.html) more carefully some other day.)

### Lombok

To make Lombok work with the IDE, you need to not only add Lombok to `pom.xml`, but also setup the IDE dependency path.

Navigate to the Lombok `.jar` folder and `sudo java -jar lombok-1.16.18.jar`, then a GUI will be opened. Through the GUI, `specify Location...` and choose the STS execusion path (in my case `/usr/local/eclipse/sts-bundle/sts-3.8.3.RELEASE/STS`), then `Install/Update`. Restart STS and the generated getters and setters works in eclipse `Outline`.
