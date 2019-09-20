# Selenium

## Setup

### Local

#### Selenium Driver (Mac OS)

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

#### Git library

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

#### Run Tests

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

### Docker

```
docker-compose -f docker-compose.yml -f docker-compose.dev.yml build selenium-test
docker-compose -f docker-compose.yml -f docker-compose.dev.yml run selenium-test bash
circleci@c88fa4db458b:/selenium-test$ curl web-app:8080/login
circleci@c88fa4db458b:/selenium-test$ pytest tests/authorization_test.py
circleci@c88fa4db458b:/selenium-test$ pytest tests/repository_navigation_test.py
```

If want to edit/debug while running the test, inside of the container

```
cd /selenium-test-in-dev
find . -name '*.pyc' -delete
sed -i 's/profile = LocalProfile()/profile = DockerProfile()/' /selenium-test/settings/profile.py
```

but then need to manually `sed -i 's/profile = LocalProfile()/profile = DockerProfile()/' /selenium-test/settings/profile.py` which affects host file.

TODO:

It seems there's a race condition between (1) when the SSH key is written into the database, (2) when the dynamically generated `authorized_keys` will include that key, and (3) whether `check_if_can_edit_repository.py` will return `True`.

If SSH key is not in `authorized_keys` it will ask password. If it is in but `check_if_can_edit_repository.py` returns false, it will return

> Authentication failed.
> fatal: Could not read from remote repository.
>
> Please make sure you have the correct access rights

Right now, I see it happens in Linux/Ubuntu but everything passes with no problem in Mac.

## TODO

- [ ] Only test happy path.
- [ ] Cumcumber test.
