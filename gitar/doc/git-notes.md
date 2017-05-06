# Git Notes

## Distribution/synchronization

Git provides synchronization between **a pair of** git repositories. `git remote`, `git fetch`, `git pull`, `git push` are the communication commands in between the two.

There is no real difference between a git server and a git client. `git remote`, `git pull`, `git push` ... are just a synchronization between two git repositories. There's no need for a central server.

A git based service defines a workflow how multiple git mirrors works together. That basically builds a **network** using the pair-wise relationship. (1) A centralized server with multiple clients (subversion-style workflow) is just one possibility. Other possibilities includes (2) integration manager workflow, (3) dictator and lieutenants workflow, ...

References: [here](https://git-scm.com/about/distributed) and [here](http://gitref.org/remotes/).
