# Git Synchronization Notes

Git provides synchronization between **a pair of** git repositories. `git remote`, `git fetch`, `git pull`, `git push` are the communication commands in between the two. The communication can be through (1) Local, (2) HTTP, (3) Secure Shell (SSH) and (4) Git protocols.

A git based service defines a workflow how multiple git mirrors works together. That basically builds a **network** using the pair-wise relationship. (1) A centralized server with multiple clients (subversion-style workflow) is just one possibility. Other possibilities includes (2) integration manager workflow, (3) dictator and lieutenants workflow, ...

References: [here](https://git-scm.com/about/distributed) and [here](http://gitref.org/remotes/).

## Communication protocols

A *remote repository* is a bare repository which has no working directory. It has the `.git` directory but nothing else. It is the ideal choice for the server-side repository.

### Through local protocol

Setup the bare repository:

```bash
~/git$ git init --bare server.git
Initialized empty Git repository in /home/beta/git/server.git/
```

Clone the bare repository:

```bash
~/git$ git clone server.git
Cloning into 'server'...
warning: You appear to have cloned an empty repository.
done.
~/git$ mv server client_1
~/git$ cd client_1
~/git/client_1$ git remote -v
origin	/home/beta/git/server.git (fetch)
origin	/home/beta/git/server.git (push)
```

Add the bare repository to an existing git project:

```bash
~/git$ mkdir client_2
~/git$ cd client_2
~/git/client_2$ git init
Initialized empty Git repository in /home/beta/git/client_2/.git/
~/git/client_2$ git remote add origin /home/beta/git/server.git
~/git/client_2$ git remote -v
origin	/home/beta/git/server.git (fetch)
origin	/home/beta/git/server.git (push)
```

Test:

```bash
~/git/client_1$ touch test-add-a-file-from-client_1
~/git/client_1$ git add -A
~/git/client_1$ git commit -m "test add file"
[master (root-commit) c347422] test add file
 1 file changed, 0 insertions(+), 0 deletions(-)
 create mode 100644 test-add-a-file-from-client_1
~/git/client_1$ git push origin master
Counting objects: 3, done.
Delta compression using up to 4 threads.
Compressing objects: 100% (2/2), done.
Writing objects: 100% (3/3), 237 bytes | 0 bytes/s, done.
Total 3 (delta 0), reused 0 (delta 0)
To /home/beta/git/server.git
 * [new branch]      master -> master
```

```bash
~/git/client_2$ git remote update
Fetching origin
remote: Counting objects: 3, done.
remote: Compressing objects: 100% (2/2), done.
remote: Total 3 (delta 0), reused 0 (delta 0)
Unpacking objects: 100% (3/3), done.
From /home/beta/git/server
 * [new branch]      master     -> origin/master
~/git/client_2$ git pull origin master
From /home/beta/git/server
 * branch            master     -> FETCH_HEAD
~/git/client_2$ ls
test-add-a-file-from-client_1
```

### Through the "dump" HTTP protocol

The "dump" HTTP protocol serves the bare Git repository as normal files from the web server.

#### `gnutls_handshake() failed` error

Naive tryout (under Ubuntu 14.04, using both Apache 2 and Tomcat 7 servers) gives `gnutls_handshake() failed` error.

Apache 2 is setup under `/var/www/html/dump-git` using `localhost:80` for access, while Tomcat 7 is setup under `/var/lib/tomcat7/webapps/dump-git` using `localhost:8080` for access. They'll get the same result. A remote connection from another PC in the same intranet will also give the same result.

Setup the bare repository:

```bash
/var/www/html/dump-git$ git init --bare server.git
Initialized empty Git repository in /var/www/html/dump-git/server.git/
/var/www/html/dump-git$ cd server.git
/var/www/html/dump-git/server.git$ mv hooks/post-update.sample hooks/post-update
/var/www/html/dump-git$ chmod a+x hooks/post-update
```

Clone the bare repository:

```bash
~/git/client_3$ git clone http://localhost:80/dump-git/server.git
Cloning into 'server'...
fatal: repository 'http://localhost:80/dump-git/server.git/' not found
~/git/client_3$ git clone https://localhost:80/dump-git/server.git
Cloning into 'server'...
fatal: unable to access 'https://localhost:80/dump-git/server.git/': gnutls_handshake() failed: An unexpected TLS packet was received.
```

#### Ubuntu package problem with `gnutls`?

#### Java 7 TLS/SSL stack bug?

It is argued in [here](https://confluence.atlassian.com/bitbucketserverkb/error-gnutls_handshake-failed-a-tls-warning-alert-has-been-received-779171747.html) that the problem is because [Java 7 that contains the a bug in the TLS/SSL stack](http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8014618).

It is unlikely to be the case in here, because (1) their `error: gnutls_handshake() failed: A TLS warning alert has been received.` is not exactly the same as my error `gnutls_handshake() failed: An unexpected TLS packet was received.`, and (2) it happens not only on Tomcat 7, but also on Apache 2 which has nothing to do with Java.

### Through the "smarter" HTTP protocol

## References

1. Scott Chacon and Ben Straub, [Pro Git](https://git-scm.com/book/en/v2), Chapter 4.
