# Selenium

## Setup

### Mac OS

Beside `selenium` pypi library, need to install standalone selenium server:

```
$ brew install selenium-server-standalone
```

and browser driver:

+ Download `geckodriver` (Firefox, from [here](https://github.com/mozilla/geckodriver/releases)) and `ChromeDriver` (from [here](https://sites.google.com/a/chromium.org/chromedriver/downloads)). There is a Mac OS version to choose. The target is a standalone executable file.
+ Throw is to any path listed in `/etc/paths`, e.g., `/usr/local/bin`.

```
$ mv geckodriver /usr/local/bin
$ mv chromedriver /usr/local/bin
```

Add add-ons (a.k.a Selenium IDEs) to the existing browsers ([Chrome](https://chrome.google.com/webstore/detail/selenium-ide/mooikfkahbdckldjjndioackbalphokd?hl=en), [Firefox](https://addons.mozilla.org/en-US/firefox/addon/selenium-ide/), ...) has nothing to do with the corresponding drivers (`ChromeDriver`, `GeckoDriver`, ...). That's for create simple scripts/assist in exploratory testing. Also, notice that add add-ons is a user-level action (no password needed).

### Git library

Popular choices include `pygit2` and `GitPython`. The difference is `pygit2` is backed by the `libssh2` library, while `GitPython` uses the git install in the local machine.

+ `pygit2`
  + Pro:
    + Can specify git username and email, rather than inherit from host machine.
  + Cons:
    + Hard to install
    + Does not support ssh protocol/port other than 22.
    + Awkward URL pattern (`git://github.com/libgit2/pygit2.git` rather than `git@github.com:libgit2/pygit2.git` or `ssh://git@github.com:[port]/libgit2/pygit2.git`, and no idea how to generalize it to absolute path e.g. `git@github.com:/home/git/libgit2/pygit2.git`)
    + Weird `_pygit2.GitError` error run in container, although command line works fine.

Therefore, we choose `GitPython` ([tutorial](https://gitpython.readthedocs.io/en/stable/tutorial.html) and [API reference](https://gitpython.readthedocs.io/en/stable/reference.html)).

## Run Tests

```
python3 -m unittest tests.authorization_test
python3 -m unittest tests.member_settings_test
python3 -m unittest tests.organization_creation_test
python3 -m unittest tests.organization_management_test
python3 -m unittest tests.repository_creation_test
python3 -m unittest tests.repository_management_test
python3 -m unittest tests.repository_navigation_test
```

or

```
virtualenv venv
. venv/bin/activate
(venv) pip install -r pip-requirements.txt
(venv) pytest
(venv) deactivate
```

TODO: setup pipenv/Pipfile.

#### Docker

```
docker-compose -f docker-compose.yml -f docker-compose.dev.yml build selenium-test
docker-compose -f docker-compose.yml -f docker-compose.dev.yml run selenium-test bash
curl web:8080/login
pytest tests/authorization_test.py
pytest tests/repository_navigation_test.py
```

Currently need to manually yes

```
ECDSA key fingerprint is SHA256:43SkBHMqnv29vH4PaZVdPZtrE+jmHDVqMRV1VEqCS88.
Are you sure you want to continue connecting (yes/no)?
```

TODO:

Currently cannot `git push origin master` to git container. There are two fold of problems:

(1) From `git-trivial` (git version 2.17.0):

```
error: remote unpack failed: unable to create temporary object directory
To `git clone git@git:/home/git/asdf/asdf.git` and edit and `git push origin master`
> error: remote unpack failed: unable to create temporary object directory
> To git:/home/git/asdf/asdf.git
>  ! [remote rejected] master -> master (unpacker error)
> error: failed to push some refs to 'git@git:/home/git/asdf/asdf.git'
```

This problem is because git folders have permission

```
drwxr-x---  7 root root 4096 Sep 10 22:05 asdf.git
```

while this command is executed using user `git`.

To resolve it, we can try:

+ Run tomcat using user git.
+ `chown` git folders after created by JGit. Confirmed if manually `chown -R git dddd.git` through command line it will resolve the problem.
+ As a workaround, may `git clone root@git:/home/git/asdf/asdf.git` but need to allow root login first.

(2) From selenium-test (git version 2.11.0):

```
> fatal: git upload-pack: protocol error, expected to get sha, not '0000000000000000000000000000000000000000 2b2c5f062dd40bddc7553a8626de0e02565e000d refs/heads/master'
```

Not sure if version un-matching is another problem, and what's the reason to cause the `git upload-pack` error.

## TODO

- [ ] Only test happy path.
