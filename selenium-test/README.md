# Selenium

## Setup

### Mac OS

Need to install standalone selenium server:

```
$ brew install selenium-server-standalone
```

python library:

```
$ pip3 install selenium
```

and browser driver:

+ Download `geckodriver` (Firefox, from [here](https://github.com/mozilla/geckodriver/releases)) and `ChromeDriver` (from [here](https://sites.google.com/a/chromium.org/chromedriver/downloads)). There is a Mac OS version to choose. The target is a standalone executable file.
+ Throw is to any path listed in `/etc/paths`, e.g., `/usr/local/bin`.

```
$ mv geckodriver /usr/local/bin
$ mv chromedriver /usr/local/bin
```

Add add-ons (a.k.a Selenium IDEs) to the existing browsers ([Chrome](https://chrome.google.com/webstore/detail/selenium-ide/mooikfkahbdckldjjndioackbalphokd?hl=en), [Firefox](https://addons.mozilla.org/en-US/firefox/addon/selenium-ide/), ...) has nothing to do with the corresponding drivers (`ChromeDriver`, `GeckoDriver`, ...). That's for create simple scripts/assist in exploratory testing. Also, notice that add add-ons is a user-level action (no password needed).

### `libgit2`

```
brew install libssh2
```

```
wget https://github.com/libgit2/libgit2/archive/v0.27.4.tar.gz
tar xzf v0.27.4.tar.gz
cd libgit2-0.27.4/
cmake .
make
sudo make install
```

```
pip3 install pygit2
```

In virtual environment, `pygit2` is really hard to be installed. Luckily the hard part is done by [`venvgit2`](https://pypi.org/project/venvgit2/).

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

TODO: Make docker work I changed `venvgit2` to `pygit2`. Not sure if it breaks above.

When `pip install -r pip-requirements.txt`, I [got error](https://github.com/uniphil/venvgit2/issues/23). But the test can be executed with no problem (not sure if that's because in my local computer I have been successfully setup `libgit2`).

Can't use `pipenv` as it is not compatible with `venvgit2`.

#### Docker

```
docker-compose -f docker-compose.yml -f docker-compose.dev.yml build selenium-test
docker-compose -f docker-compose.yml -f docker-compose.dev.yml run selenium-test bash
curl web:8080/login
pytest tests/authorization_test.py
```

## TODO

- [ ] Move from local environment to virtual environment (`pipenv` for now). To make it work in CI we probably need to configure CircleCI python image to be capable with `cmake`/... for the annoying `pygit2` issue.
- [ ] Only test happy path.
