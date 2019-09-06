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

Test image in docker (selenium running on local) is really painful.

Setup:

```
sed -i'.original' -e 's/profile = LocalProfile()/profile = DockerProfile()/' selenium-test/settings/profile.py
```

Teardown:

```
sed -i'.original' -e 's/profile = DockerProfile()/profile = LocalProfile()/' selenium-test/settings/profile.py
```

Right now we don't have a container for selenium environment yet (should be very similar to the one we use in CircleCI).

## TODO

- [ ] Only test happy path.
