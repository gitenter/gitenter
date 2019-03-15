# GitEnter

[![Travis CI Build Status](https://travis-ci.org/gitenter/gitenter.svg?branch=master)](https://travis-ci.org/gitenter/gitenter)
[![CircleCI](https://circleci.com/gh/gitenter/gitenter.svg?style=svg)](https://circleci.com/gh/gitenter/gitenter)

A Git based version control tool for requirement engineering, design control, verification and validation processes.

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

### Lombok

To make Lombok work with the IDE, you need to not only add Lombok to `pom.xml`, but also setup the IDE dependency path.

Navigate to the Lombok `.jar` folder (`.m2/repository/org/projectlombok/lombok`) and `sudo java -jar lombok-1.16.18.jar`, then a GUI will be opened. Through the GUI, `specify Location...` and choose the STS execusion path (in my case `/opt/sts-bundle/sts-3.9.0.RELEASE/STS`), then `Install/Update`. Restart STS and the generated getters and setters works in eclipse `Outline`.

#### Mac OS

Enable Lombok on Mac OS is kind of tricky, but it works by following [this link](https://nawaman.net/blog/2017-11-05).

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

### Docker

Docker is installed by following [this link](https://www.digitalocean.com/community/tutorials/how-to-install-and-use-docker-on-ubuntu-16-04). Currently there's no need to `sudo` run docker commands.

Start over:

```
docker rm -f -v gitenter_database_1
docker-compose build --no-cache
docker-compose up
```

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

### Terraform

```
$ brew install terraform
$ terraform --version
Terraform v0.11.13
```

```
cd tf-config
terraform init
terraform plan -var-file="secret.tfvars"
terraform apply -var-file="secret.tfvars"
terraform destroy -var-file="secret.tfvars"
```

When global initialization, needs manually create:

- AMI user w/ `access_key` and `secret_key`
- S3 bucket `gitenter-config` if it is not existing yet, versioning enabled. It is used for save and share remote state.

### CircleCI

Install [CircleCI local CLI](https://circleci.com/docs/2.0/local-cli/) so the setup can be tested locally.

```
$ circleci config validate
$ circleci local execute --job build
```

## Unclassified TODOs and features

+ [x] A better CSS.
	+ The words crown together.
	+ Margin in between traced items.
+ If only one single upstream item is provided, and it is in the same document/paragraph, should the item be just `tab` of the upstream one, rather than list separately with "hard to understand" link?
+ [ ] No need for `enable_systemwide` value in `gitenter.properties`. An alternative way is to `git diff` (1) this properties file and (2) the specified included folders. If there is any change happens, then turn on the system; otherwise turn it off.
+ [ ] RNAtom id generator has a bug when the item is with empty upstream but in the form of `- [tag]` rather than `-[tag]{}`.
+ [ ] Currently domain layer is in charge of mapping to database table/working with ORM, while it also have non-ORM owned attributes which fill values from git. Repositories are in charge of load both. We have pain issue that when e.g. load a one-to-many object, the git values are not bootstrapped.
    + Separate the domain layer to the database access layer. Domain layer then have dual backup layers of ORM DTO and git DTO. Each DTO has its corresponding repository/DAO. Domain layer are queries by the (stateless) service layer, which further call the repositories/DAOs.
    + May need a way to synchronize the values in the domain layer and the DTO layers. Consider observer pattern for which the boilerplate code may later on be abstract to some reflection in the superclasses (tedious but should be duable).
	+ Will **not** naturally solve the problem that the git lazy evaluation placeholders will not be setup in one-to-many navigations. However, it does have a good separation between the two data sources. May need a better git access point with some code similar to the backend implementation of JPA/Hibernate lazy evalutaion.
