# Setup Tomcat in Amazon EC2

## Various configurations

### Standard wizard

I follow the standard wizard to setup the EC2 instance. The OS I choose is Ubuntu Server 16.04.

### Elastic IP

An elastic IP shall be setup and associate with the desired instance, otherwise the "Public DNS" will be changed when the instance is restarted. To do it, go to "Network & Security > Elastic IPs" in the manual of the console panel, "Allocate new address" and associate the desired instance with the address.

For this repository, the public DNS becomes

```
ec2-52-35-43-194.us-west-2.compute.amazonaws.com
```

### Tomcat

Tomcat can be installed by the following command through the SSH client.

```bash
$ java -version
openjdk version "1.8.0_121"
OpenJDK Runtime Environment (build 1.8.0_121-8u121-b13-0ubuntu1.16.04.2-b13)
OpenJDK 64-Bit Server VM (build 25.121-b13, mixed mode)
$ sudo apt-get install tomcat8
```

Troubleshooting: I got `failed to fetch` error while `sudo apt-get install tomcat8`. Run `sudo apt-get update` solves the problem. I don't know why. It doesn't happen for my other sandbox instance.

### Security group setting

Port 8080 need to be opened. Since "HTTP" is to port 80, we need to use "Custom TCP Rule" to set it up. The setting is under "Network & Security > Security Groups".

After that, the Tomcat server can be reached by the Public DNS (with `:8080`) with the feedback

> It works !
> If you're seeing this page via a web browser, it means you've setup Tomcat successfully. Congratulations!
> ...

Also, this folder then exists:

```bash
$ cd /var/lib/tomcat8/webapps
$ ls
ROOT
```

TODO: May need to think of a way to map port 80 to the tomcat server.

### Copy the `.war` file to the Tomcat folder

In the SSH client side

```bash
$ sudo chmod 777 /var/lib/tomcat8/webapps
```

In the local side

```bash
/path/to/the/key-pair$ scp -i "capsid-key-pair.pem" /path/to/capsid-0.0.1-prototype.war ubuntu@ec2-52-35-43-194.us-west-2.compute.amazonaws.com:/var/lib/tomcat8/webapps
```

Then, in the SSH client side we can get

```
/var/lib/tomcat8/webapps$ ls
capsid-0.0.1-prototype  capsid-0.0.1-prototype.war  ROOT
```

and the web application can be reached by

```
http://ec2-52-35-43-194.us-west-2.compute.amazonaws.com:8080/capsid-0.0.1-prototype/
```
