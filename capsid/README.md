## Development environment setting

### Dependencies

#### `gitar`

Although in eclipse/STS you can set up the dependency of `gitar` under `Properties -> Java Build Path -> Projects`, using command line tools such as `mvn package` you kind of need to fake the `gitar` package under `.m2`.

To do it, compile the gitar library using `mvn intall`. See the `README.md` under the gitar package.

(Try to set it in a local way to link to `gitar/target/*.jar` in the `pom.xml` of `capsid`. Not successful yet. May refer to [here](https://stackoverflow.com/questions/2229757/maven-add-a-dependency-to-a-jar-by-relative-path) or [here](https://maven.apache.org/settings.html) more carefully some other day.)

## Development

### STS

Although for deployment we should just `mvn package` and put the `.war` file in an existing Tomcat folder, for testing it is more convenient to run the Spring Boot application inside of STS.

To set it up, Go to `Run > Run configurations` and under the `Spring Boot` leave and Main type `enterovirus.capsid.CapsidApplication`. In Override Properties, you right click Add Row and set property as `server.port`, and value as `8888`. Then the site is accessible from `http://localhost:8888`.

#### Linux group and user

In the final product, I have group `enterovirus` with users (1)`tomcat8` and (2)`git`. While testing, I have `enterovirus` with users (1) `tomcat8`, (2)`git`, and (3)`beta` -- myself. Since the STS tomcat is running under myself (rather than user `tomcat8`), that will make the development process easier.

## Deployment

Install Tomcat 8. Then `sudo chmod 777 /var/lib/tomcat8/webapps` and put the `.war` file into that folder. The site is accessible from `http://localhost:8080/capsid-[version]/`.

Need to update the git home folder in `enterovirus.capsid.config.GitConfig.java`.

Shell scripts in `capsid/src/main/resources/git-server-side-hooks` need to be executable. Make it works by e.g., `chmod +x update`.

### Remain problem

Sometimes deploy `.war` file to tomcat8 gets `java.lang.OutOfMemoryError: Java heap space` error. In `catalina.20xx-xx-xx.log`:

```
09-Feb-2018 17:01:55.253 SEVERE [localhost-startStop-22] org.apache.catalina.core.ContainerBase.addChildInternal ContainerBase.addChild: start:
 ...
Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name '...': ... Invocation of init method failed; nested exception is java.lang.OutOfMemoryError: Java heap space
```

Right now it seems `sudo /etc/init.d/tomcat8 restart` and deploy again solve the problem. But we may want to double check later what is the real cause for that.

## Testing

### Spring authorization

It ever happens that the website ask a spring authorization. I don't understand the reason yet (if recompile, the authorization is no longer needed, even if from a different browser).

```
http://localhost:8888 is requesting your username and password. The site says: “Spring”
```

If that happens, use user `user` and password printed in the console. E.g.

```
[main] INFO org.springframework.boot.autoconfigure.security.AuthenticationManagerConfiguration -

Using default security password: c543b70b-7aa5-4dcf-ab0b-eea37e792363
```

### Testing URLs

```
http://localhost:8888/api/users/ann/
http://localhost:8888/api/organizations/gov
```
