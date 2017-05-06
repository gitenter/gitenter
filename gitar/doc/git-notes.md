# Git Notes

## Distribution/synchronization

Git provides synchronization between **a pair of** git repositories. `git remote`, `git fetch`, `git pull`, `git push` are the communication commands in between the two. The communication can be through (1) Local, (2) HTTP, (3) Secure Shell (SSH) and (4) Git protocols.

A git based service defines a workflow how multiple git mirrors works together. That basically builds a **network** using the pair-wise relationship. (1) A centralized server with multiple clients (subversion-style workflow) is just one possibility. Other possibilities includes (2) integration manager workflow, (3) dictator and lieutenants workflow, ...

References: [here](https://git-scm.com/about/distributed) and [here](http://gitref.org/remotes/).

### Communication

A *remote repository* is a bare repository which has no working directory. It has the `.git` directory but nothing else. It is the ideal choice for the server-side repository.

#### Local protocol

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
