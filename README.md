# enterovirus

Enterovirus is not a computer virus!

## Development

### STS

It is very tricky to setup STS. For the dependencies of all maven packages, you don't want to add anything on the project's `Properties > Java Build Path > Projects`. What should be done is to `mvn install` all packages (so they are properly setup in `.m2`) and let Eclipse to load them from `.m2` just like external packages.

Reasons:

(1) Although in the final product, the testing class are completely irrelavent, STS mixed up the `src/test/java` parts of different packages. For example, it may then say errors like this:

```
Caused by: org.springframework.context.annotation.ConflictingBeanDefinitionException: Annotation-specified bean name 'testDatabaseConfig' for bean class [enterovirus.gihook.postreceive.config.TestDatabaseConfig] conflicts with existing, non-compatible bean definition of same name and class [enterovirus.protease.config.TestDatabaseConfig]
```

That seems also because STS cannot handle two classes which accidentally have the same bean name (the bean name should be by default the camelCase of the class name).

*(Consider simplify the case and report the bug to STS someday?)*

(2) STS mixed up the setup of a normal Spring project and a Spring Boot project (they may refer to different/incompatible packages regard to `pom.xml`). For example, if `capsid` (a Spring Boot project) refer to `protease` (a normal Spring project), then

```
[main] WARN org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext - Exception encountered during context initialization - cancelling refresh attempt: org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'securityConfig': Unsatisfied dependency expressed through method 'setContentNegotationStrategy' parameter 0; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration$EnableWebMvcConfiguration': Invocation of init method failed; nested exception is java.lang.AbstractMethodError
[main] WARN org.springframework.boot.SpringApplication - Error handling failed (Error creating bean with name 'delegatingApplicationListener' defined in class path resource [org/springframework/security/config/annotation/web/configuration/WebSecurityConfiguration.class]: BeanPostProcessor before instantiation of bean failed; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration': Initialization of bean failed; nested exception is org.springframework.beans.factory.NoSuchBeanDefinitionException: No bean named 'org.springframework.context.annotation.ConfigurationClassPostProcessor.importRegistry' available)
[main] ERROR org.springframework.boot.SpringApplication - Application startup failed
```

### Log4j multiple binding

Analyze the dependency tree using `mvn dependency:tree`. Then remove the dependencies by e.g.

```
<exclusions>
    <exclusion>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-log4j12</artifactId>
    </exclusion>
    <exclusion>
        <groupId>log4j</groupId>
        <artifactId>log4j</artifactId>
    </exclusion>
</exclusions>
```

I try to remove all logging dependencies from packages, and define a independent one in every package.

At this moment, I try to do the modules under `org.slf4j:slf4j-log4j12` (working), and maybe use `ch.qos.logback:logback-classic` for `capsid` (has problem inside of STS).

## Deployment

### Docker

Docker is installed by following [this link](https://www.digitalocean.com/community/tutorials/how-to-install-and-use-docker-on-ubuntu-16-04). Currently there's no need to `sudo` run docker commands.

(Re-)build image

```
docker stop panda
#docker rm panda
docker rm $(docker ps -a -q -f status=exited)
docker rmi ozooxo/enterovirus
docker build -t ozooxo/enterovirus .
docker run -d -p 52022:22 -p 52418:9418 -p 58080:8080 --name panda ozooxo/enterovirus
#docker port panda
```

```
docker build --no-cache -t ozooxo/enterovirus .
```

Access shell (need to stop containers first by `docker stop panda`):

```
docker run -it ozooxo/enterovirus sh
```

Log in by SSH

```
ssh git@0.0.0.0 -p 52022
```

Clone git repository (not working)

```
git clone ssh://git@0.0.0.0:52418/home/git/server.git
git clone git@0.0.0.0:52418/home/git/server.git
```

Connect to Tomcat server (should have "Hello enterovirus capsid!" return in the browser)

```
http://0.0.0.0:58080/capsid-0.0.1-alpha
```

## Unclassified TODOs and features

+ [ ] A better CSS. 
	+ The words crown together.
	+ Margin in between traced items.
+ If only one single upstream item is provided, and it is in the same document/paragraph, should the item be just `tab` of the upstream one, rather than list separately with "hard to understand" link?
