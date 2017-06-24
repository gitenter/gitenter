# Docker Notes

A deployment tool which setup a Docker image for the target application to run. It makes the application to be independent of the system.

Key words:

+ Image: Linux OS w/ various associated dependencies/environment needed.
+ Container: Running instance of images.

Compare to virtual machines (VMs):

+ VM:
	+ Running in a *guest* OS for which the hardwares are all virtual/powered by the host OS.
	+ Better separation: Very few ways a problem in host OS can affect the software running in the guest OS. Vice versa.
	+ Adds gigabytes of space to the project.
	+ Computation overhead (slow boot, ...).
+ Docker container:
	+ Leveraging the low-level mechanics: Provide most of the isolation of VMs, but at a fraction of the computing power.
		+ Sharing OS kernel across all the containers.
		+ (Just) running separate processes of the host OS for different container.
	+ More efficient usage of the underlying system and resources.

Usage:

+ Building and deploying applications.
	+ On the Cloud:
		+ Static website to AWS.
		+ Dynamic webapp to EC2 (Elastic Beanstalk/Elastic Container Service/...).

Parts:

+ Docker daemon: Background service running on the host OS.
+ Docker client: Command line tool for the user to talking to the daemon.
+ Docker hub: registry (remotely at [Docker hub](hub.docker.com) or locally) of all available images.

## Commands

May add `sudo` or create a group for associated permissions.

+ Setup: `docker pull [image-name]`
+ Show all:
	+ Images/Repositories: `docker images`
	+ Container: `docker ps` (the ones running)/`docker ps -a` (historic)
+ Delete:
	+ Containers:
		+ User container ID: `docker rm [container-ID] [another-container-ID]`
		+ Delete all: `docker rm $(docker ps -a -q -f status=exited)`
	+ Images: `docker rmi [image-name]` (need to delete all associated containers first...)
+ Run commands (create a container from an image):
	+ One single command: `docker run [image-name] [command]`
	+ Go to the shell of the container: `docker run -it [image-name] sh`
		+ `-i` for assigning a pseudo-tty or terminal inside the new container.
		+ `-t` for allowing to make interactive connections.
	+ Other options:
		+ `--name`: Customize a container name.
			+ Otherwise docker gives a container name which can be checked by `docker ps` (the name has the format `adj_noun` and always sound funny...).
		+ `-d`: Detached mode. Detach to terminal, so after close the terminal the container is still running.
			+ Stop a container (if it is running on the detached mode): `docker stop [container-name]`
		+ `-P`: Publish all exposed ports to random ports. `-p [redirect-port]:[expose-port]` to specify port.
			+ Check the redirected ports: `docker port [container-name]`
			+ Access the app using `http://localhost:[redirect-port]`
		+ `--rm`: Clean up the container and remove the file system when the work is done.

## Make Docker images and Run

Either (1) build completely from sketching/image such as ubuntu/debian/busybox/... (called a *base image*), or (2) pull from [Docker hub](hub.docker.com) and then build on top of it (called a *child image*).

Written in the form of [Dockerfile](https://docs.docker.com/engine/reference/builder/#usage), which contains a list of (equivalent Linux) commands that the Docker client calls while creating an image. Save it in the same folder as the app with the name `Dockerfile`. A list of Dockerfile examples of other repositories can be found in [GitHub](https://github.com/search?l=Dockerfile&q=EXPOSE&type=Code&utf8=%E2%9C%93) (the word after `&q=` is the Docker keyword you want to search).

Dockerfile syntax to define the associated image:

+ Setup base image: `FROM [base-image-name]:[onbuild-version]`
	+ For example, `FROM ubuntu:14.04` (a typical *base image* case).
	+ The `onbuild` version may be `latest`.
	+ The `onbuild` version takes care of copying the files and installing the dependencies (typically for *child image* cases, then you skip the steps to setup the 3rd parties).
+ Image build step `RUN` (committed to the Docker image).
	+ Install system-wide dependencies: `RUN apt-get -yqq install ...`.  The `yqq` flag is used to suppress output and assumes "Yes" to all prompt.
	+ Create symbolic links: `RUN ln -s ...`
+ Copy our application into a new volume in the container: `ADD [current-directory] [target-directory-in-container]`
	+ Then setup the working directory by `WORKDIR [some-container-directory]`
	+ Create mount-point for a volume by `VOLUME [some-container-directory]`. It is typically for data, and data is highly recommended to be kept out of the container.
	+ May include a `.dockerignore` file in the root, so when `docker build` the unnecessary files will not be included.
+ Container executing command `CMD` (executes when launch the build image).
	+ Syntax: `CMD ["command-string-1", "command-string-2", ...]`
	+ Notice that when you have something like `CMD ["java", ...]`, `CMD ["mvn", "package"]`, `CMD ["python", "something.py"]`, this command is running in the current shell directory, so you want to `docker run` under the current directory.
+ Specify the port number to expose: `EXPOSE [expose-port]`

Run:

Run `docker build -t [username]/[image-name]` which will create a Docker image from this Dockerfile. The new setup image shall be appeared from `docker images`. Then you can do `docker run -p [redirect-port]:[expose-port] [username]/[image-name]`.

## Connect the port of one Docker container from another Docker container

It is kind of because Docker cannot do "mixin" of base images. So we may setup containers of databases/other apps/... in some Docker container (with proper redirect port), and let the main container (for which the app is running on top of it) to talk to these ports.

It is done by docker network `bridge` (It works, but the result cannot stay. Also, it is not robust if the IP changes).

+ List all networks: `docker network ls`. `bridge` should be in it.
+ Check the redirect IP address of the dependent container: `docker network inspect bridge` returns a JSON file and the targeting IP is at `Container.[container-id].IPv4Address`
+ Then you can `docker run -it --rm [username]/[image-name] bash` the main container, and `curl [IP]:[port]` this IP address.

So the better way is to create another bridge network.

+ Create network: `docker network create [bridge-network-name]`
+ Launch the dependent container(s) under the network: `docker run -dp [port]:[port] --net [bridge-network-name] --name [container-name] [dependent-image-name]`
+ Launch the main container within the bridge network: `docker run -it --rm --net [bridge-network-name] [username]/[image-name] bash`, and `curl [container-name]:[port]` for bridging.

## Deploy on the Cloud

#### AWS Elastic Beanstalk

Publish the image in the Docker registry. For the registry [Docker hub](hub.docker.com): `docker push [username-used-in-Docker-hub]/[image-name]`. Then everybody can use it in Amazon Elastic Beanstalk by command `docker run -p [redirect-port]:[expose-port] [username-used-in-Docker-hub]/[image-name]`.

Or may use [other Docker registries](https://aws.amazon.com/ecr/) or [host your own](https://docs.docker.com/registry/deploying/), but the setup is more complicated.

#### AWS Beanstalk

Some UI works, then setup the `Dockerrun.aws.json` file.

## Other interesting tools

+ [Docker Machine](https://docs.docker.com/machine/): Install Docker engine on virtual hosts, and manage those hosts.
+ [Docker Compose](https://docs.docker.com/compose/) co-work with [AWS Elastic Container Service](https://aws.amazon.com/ecs/)
+ [Docker Swarm](https://docs.docker.com/swarm/)

## References

1. [Docker for beginners](https://prakhar.me/docker-curriculum/) *(This is the one I mainly refer to)*
1. [Docker Tutorial — Getting Started with Python, Redis, and Nginx.](https://hackernoon.com/docker-tutorial-getting-started-with-python-redis-and-nginx-81a9d740d091) *(Read)*
1. [Docker -- Container Tutorials](http://containertutorials.com/) *(Too detailed so I didn't touch yet)*
1. [https://docs.docker.com/get-started/](https://docs.docker.com/get-started/) *(Super detailed official doc I didn't touch yet)*
