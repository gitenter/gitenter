# Git Synchronization Notes

Git provides synchronization between **a pair of** git repositories. `git remote`, `git fetch`, `git pull`, `git push` are the communication commands in between the two. The communication can be through (1) Local, (2) HTTP, (3) Secure Shell (SSH) and (4) Git protocols.

A git based service defines a workflow how multiple git mirrors works together. That basically builds a **network** using the pair-wise relationship. (1) A centralized server with multiple clients (subversion-style workflow) is just one possibility. Other possibilities includes (2) integration manager workflow, (3) dictator and lieutenants workflow, ...

References: [here](https://git-scm.com/about/distributed) and [here](http://gitref.org/remotes/).

## Communication protocols

A *remote repository* is a bare repository which has no working directory. It has the `.git` directory but nothing else. It is the ideal choice for the server-side repository.

### Through Local protocol

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

### Through SSH protocol

Make sure that SSH is properly installed in the corresponding computers. If not, run `sudo apt-get install openssh-server`.

#### Single user case

It should work trivially:

```bash
$ git clone ssh://beta@192.168.0.106:/home/beta/git/server.git
```

Or

```bash
$ git clone beta@192.168.0.106:/home/beta/git/server.git
```

Then,

```bash
Cloning into 'server'...
The authenticity of host '192.168.0.106' can't be established.
ECDSA key fingerprint is #############################################
Are you sure you want to continue connecting (yes/no)? yes
Warning: Permanently added '192.168.0.106' (ECDSA) to the list of known hosts.
beta@192.168.0.106's password:
```

After provide available password, the clone will be established.

Note:

1. Work for (1) absolute path `git clone beta@192.168.0.106:/home/beta/git/server.git` or (2) relative path `clone beta@192.168.0.106:git/server.git` (not `~/git/server.git`).
2. For a user who has read access to `/home/beta/git/server.git` can `pull`, while if s/he has write access then s/he can also `push`. In my example here is the user `beta` is accessing her home folder, so she has giant permission for everything.

#### Multiple users case

In real practice, you don't want to create all users (with different access level) on your server side. The alternative method is to create a single user `git`, let `git` to have write access to the target folder, and ask every user (who doesn't know `git` password) who is to have write access to generate a SSH key, and provide the public part for you to add to the `~/.ssh/authorized_keys` file of the `git` user. In addition, you want to limit the privilege for the user `git` to do other shell commands, by using the `git-shell` tool.

Or if different users are to have write access to different folders, you may want the user account (email address) to be associated to the SSH key. The client side setup is similar to [here](https://help.github.com/articles/connecting-to-github-with-ssh/). I need to think about what need to be done in the server's side to make it work.

### Through the "dump" HTTP protocol

The "dump" HTTP protocol serves the bare Git repository as normal files from the web server.

#### `gnutls_handshake() failed` error

Naive tryout (under Ubuntu 14.04, using Apache 2, Tomcat 7 servers, and local PHP server) gives `gnutls_handshake() failed` error.

Apache 2 is setup under `/var/www/html/dump-git` using `localhost:80` for access, while Tomcat 7 is setup under `/var/lib/tomcat7/webapps/dump-git` using `localhost:8080` for access. For local PHP server I simply navigate to the root of bare repository and do `php -S localhost:8123` and access using `localhost:8123`.

They'll get the same result. A remote connection from another PC in the same intranet will also give the same result.

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

#### Ubuntu `git` package problem with `gnutls`?

It is argued in [here](https://askubuntu.com/questions/186847/error-gnutls-handshake-failed-when-connecting-to-https-servers) and (maybe) [here](http://stackoverflow.com/questions/13524242/error-gnutls-handshake-failed-git-repository) that the problem is because the `gnutls` package does not work behind a proxy. So we should use `openssl` and recompile `git` with it.

```bash
$ sudo apt-get update
$ sudo apt-get install build-essential fakeroot dpkg-dev libcurl4-openssl-dev
$ sudo apt-get build-dep git
$ mkdir ~/git-openssl
$ cd ~/git-openssl
$ apt-get source git # Then `ls` to see what is exactly in the folder
$ dpkg-source -x git_1.9.1-1ubuntu0.4.dsc
$ cd git-1.9.1
$ sudo gedit debian/control
```

and replace all instances of `libcurl4-gnutls-dev` with `libcurl4-openssl-dev`

```
$ sudo dpkg-buildpackage -rfakeroot -b
$ sudo dpkg -i ../git_1.9.1-1ubuntu0.4_amd64.deb
```

I follow, but get this error for both servers:

```bash
~/git/client_3$ git clone https://localhost:80/dump-git/server.git
Cloning into 'server'...
fatal: unable to access 'https://localhost:80/dump-git/server.git/': error:140770FC:SSL routines:SSL23_GET_SERVER_HELLO:unknown protocol
```

Postscript: `sudo apt-get remove git` and re-`install` will get back the `gnutls_handshake() failed` error.

#### Java 7 TLS/SSL stack bug?

It is argued in [here](https://confluence.atlassian.com/bitbucketserverkb/error-gnutls_handshake-failed-a-tls-warning-alert-has-been-received-779171747.html) that the problem is because [Java 7 that contains the a bug in the TLS/SSL stack](http://bugs.java.com/bugdatabase/view_bug.do?bug_id=8014618).

It is unlikely to be the case in here, because (1) their `error: gnutls_handshake() failed: A TLS warning alert has been received.` is not exactly the same as my error `gnutls_handshake() failed: An unexpected TLS packet was received.`, and (2) it happens not only on Tomcat 7, but also on Apache 2 which has nothing to do with Java.

### Through the "smarter" HTTP protocol

### Through Git protocol

Git protocol is read only, so even GitHub is not using it (the two choices GitHub provided are SSH and HTTPS). The setup may be referred to [here](https://git-scm.com/book/en/v2/Git-on-the-Server-Git-Daemon). Currently I am facing problem with the port 9418, so don't have a working solution yet.

## Restrict malicious uses

### `git-shell`

`git-shell` is to restrict a user to only doing git activities rather than the general SSH activities.

```
$ which git-shell
/usr/bin/git-shell
```

Add it to `/etc/shells` if it is not already in there.

```
sudo chsh git -s $(which git-shell)
```

Then user `git` an only access SSH for git activities.

One may also restrict the detailed (1) git user's home directory, (2) the git commands the server can accept, (3) message, using `git-shell`. Refer to `git help shell` for details.

### Git Daemon

To make the repository unauthenticated accessible (anonymous read).

### Complicated authentications

For lots of users to access the git server with different authentication of different repositories.

What is needed is a set of scripts that help you manage the `authorized_keys` file as well as implement some simple access controls.

+ [Gitosis](https://github.com/tv42/gitosis)
    + In Python
    + Simpler
+ Gitolite
    + In Perl
    + Had advanced functions of authorize pushing to certain refs (branches/tags).

Both have similar design to save the users and repositories setups in a special git repository `gitosis-admin`/`gitolite-admin`.

Need to have general SSH shell rather than `git-shell`, then let THIS program to be in charge of that.

## References

1. Scott Chacon and Ben Straub, Chapter 4 of the Git Pro book [v1](https://git-scm.com/book/en/v1/Git-on-the-Server) [v2](https://git-scm.com/book/en/v2/Git-on-the-Server-The-Protocols). Should refer to v1 because it discusses `Gitosis` and `Gitolite` which are not included in v2.
