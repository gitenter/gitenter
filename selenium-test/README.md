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

In virtual environment, `pygit2` is really hard to be installed. Luckily the hard part is done by [venvgit2](https://pypi.org/project/venvgit2/).

## Run Tests

```
python3 -m unittest tests.authorization_test
```

## TODO

- [ ] Move from local environment to virtual environment (`pipenv` for now). To make it work in CI we probably need to configure CircleCI python image to be capable with `cmake`/... for the annoying `pygit2` issue.
- [ ] Move from python unittest to pytest.
- [ ] Deprecate reset database through `psycopy2` (which cannot be done in CI, as CircleCI cannot manipulate database otherwise there's security problem). Try to seek the possibility to make tests idempotent (maybe by adding a "remove user" functionality). Or a little bit worse, we may partitioning the environment (make multiple users with different names for different test scenarios), and cleanup the database after all tests are finished (still not that easy in CI, unless we kill/restart RDS every time).
- [ ] Only test happy path.
