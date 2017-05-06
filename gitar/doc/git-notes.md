# Git Notes

## Distribution/synchronization

Git provides synchronization between **a pair of** git repositories. `git remote`, `git fetch`, `git pull`, `git push` are the communication commands in between the two. The communication can be through (1) Local, (2) HTTP, (3) Secure Shell (SSH) and (4) Git protocols.

A git based service defines a workflow how multiple git mirrors works together. That basically builds a **network** using the pair-wise relationship. (1) A centralized server with multiple clients (subversion-style workflow) is just one possibility. Other possibilities includes (2) integration manager workflow, (3) dictator and lieutenants workflow, ...

References: [here](https://git-scm.com/about/distributed) and [here](http://gitref.org/remotes/).

### Communication

A *remote repository* is a bare repository which has no working directory. It has the `.git` directory but nothing else. It is the ideal choice for the server-side repository.

#### Through local protocol

Setup the bare repository:

```bash
beta@landmark:~/git$ git init --bare server.git
Initialized empty Git repository in /home/beta/git/server.git/
```

Clone the bare repository:

```bash
beta@landmark:~/git$ git clone server.git
Cloning into 'server'...
warning: You appear to have cloned an empty repository.
done.
beta@landmark:~/git$ mv server client_1
beta@landmark:~/git$ cd client_1
beta@landmark:~/git/client_1$ git remote -v
origin	/home/beta/git/server.git (fetch)
origin	/home/beta/git/server.git (push)
```

Add the bare repository to an existing git project:

```bash
beta@landmark:~/git$ mkdir client_2
beta@landmark:~/git$ cd client_2
beta@landmark:~/git/client_2$ git init
Initialized empty Git repository in /home/beta/git/client_2/.git/
beta@landmark:~/git/client_2$ git remote add origin /home/beta/git/server.git
beta@landmark:~/git/client_2$ git remote -v
origin	/home/beta/git/server.git (fetch)
origin	/home/beta/git/server.git (push)
```

Test:

```bash
beta@landmark:~/git/client_1$ touch test-add-a-file-from-client_1
beta@landmark:~/git/client_1$ git add -A
beta@landmark:~/git/client_1$ git commit -m "test add file"
[master (root-commit) c347422] test add file
 1 file changed, 0 insertions(+), 0 deletions(-)
 create mode 100644 test-add-a-file-from-client_1
beta@landmark:~/git/client_1$ git push origin master
Counting objects: 3, done.
Delta compression using up to 4 threads.
Compressing objects: 100% (2/2), done.
Writing objects: 100% (3/3), 237 bytes | 0 bytes/s, done.
Total 3 (delta 0), reused 0 (delta 0)
To /home/beta/git/server.git
 * [new branch]      master -> master
```

```bash
beta@landmark:~/git/client_2$ git remote update
Fetching origin
remote: Counting objects: 3, done.
remote: Compressing objects: 100% (2/2), done.
remote: Total 3 (delta 0), reused 0 (delta 0)
Unpacking objects: 100% (3/3), done.
From /home/beta/git/server
 * [new branch]      master     -> origin/master
beta@landmark:~/git/client_2$ git pull origin master
From /home/beta/git/server
 * branch            master     -> FETCH_HEAD
beta@landmark:~/git/client_2$ ls
test-add-a-file-from-client_1
```

#### Through the "dump" HTTP protocol

The "dump" HTTP protocol serves the bare Git repository as normal files from the web server.

##### Tomcat 7 with Ubuntu 14.04

Setup the bare repository:

```bash
beta@landmark:/var/lib/tomcat7/webapps/dump-git$ git init --bare server.git
Initialized empty Git repository in /var/lib/tomcat7/webapps/dump-git/server.git/
beta@landmark:/var/lib/tomcat7/webapps/dump-git$ cd server.git/
beta@landmark:/var/lib/tomcat7/webapps/dump-git/server.git$ mv hooks/post-update.sample hooks/post-update
beta@landmark:/var/lib/tomcat7/webapps/dump-git/server.git$ chmod a+x hooks/post-update
```

Clone the bare repository:

```bash
beta@landmark:~/git/client_3$ git clone https://localhost:8080/dump-git/server.git
Cloning into 'server'...
fatal: unable to access 'https://localhost:8080/dump-git/server.git/': gnutls_handshake() failed: An unexpected TLS packet was received.
```

The `gnutls_handshake()` error is because [Java 7 that contains the a bug in the TLS/SSL stack](http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8014618). Possible solution may be refer to [here](https://confluence.atlassian.com/bitbucketserverkb/error-gnutls_handshake-failed-a-tls-warning-alert-has-been-received-779171747.html). We'll see whether Tomcat 8 with Java 8 (in Ubuntu 16.04) works or not.

> As it is a Java version problem, and my Java 7 version is 7u80 for which the bug should already be solved, I try to change the Java version of Tomcat (as it does not go with the default system Java version), but it doesn't work.
>
> ```bash
> $ sudo update-java-alternatives -s java-7-oracle
> $ java -version
> java version "1.7.0_80"
> Java(TM) SE Runtime Environment (build 1.7.0_80-b15)
> Java HotSpot(TM) 64-Bit Server VM (build 24.80-b11, mixed mode)
> $ sh /usr/share/tomcat7/bin/version.sh
> Using CATALINA_BASE:   /usr/share/tomcat7
> Using CATALINA_HOME:   /usr/share/tomcat7
> Using CATALINA_TMPDIR: /usr/share/tomcat7/temp
> Using JRE_HOME:        /usr/lib/jvm/java-8-oracle
> Using CLASSPATH:       /usr/share/tomcat7/bin/bootstrap.jar:/usr/share/tomcat7/bin/tomcat-juli.jar
> Server version: Apache Tomcat/7.0.52 (Ubuntu)
> Server built:   Feb 17 2017 03:37:39
> Server number:  7.0.52.0
> OS Name:        Linux
> OS Version:     3.13.0-46-generic
> Architecture:   amd64
> JVM Version:    1.8.0_131-b11
> JVM Vendor:     Oracle Corporation
> $ sudo gedit /etc/default/tomcat7
> ```
> Then change `JAVA_HOME` to e.g. `/usr/lib/jvm/java-7-oracle` and `sudo service tomcat7 restart`, but it still gives me the Java 8 version.

#### Through the "smarter" HTTP protocol

## References

1. Scott Chacon and Ben Straub, [Pro Git](https://git-scm.com/book/en/v2).
