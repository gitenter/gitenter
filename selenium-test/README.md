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
```

or

```
virtualenv venv
. venv/bin/activate
(venv) pip install -r pip-requirements.txt
(venv) pytest
(venv) deactivate
```

When `pip install -r pip-requirements.txt`, I [got error](https://github.com/uniphil/venvgit2/issues/23). But the test can be executed with no problem (not sure if that's because in my local computer I have been successfully setup `libgit2`).

Can't use `pipenv` as it is not compatible with `venvgit2`.

#### Docker

Right now test in docker is really painful.

Setup:

```
sed -i'.original' -e "s/spring.profiles.active=sts/spring.profiles.active=docker/g" capsid/src/main/resources/application.properties
sed -i'.original' -e 's/profile = LocalProfile()/profile = DockerProfile()/' selenium-test/settings/profile.py
mvn package -f capsid/pom.xml -DskipTests
docker-compose build web
docker-compose up
```

Teardown:

```
docker-compose down
sed -i'.original' -e 's/profile = DockerProfile()/profile = LocalProfile()/' selenium-test/settings/profile.py
sed -i'.original' -e "s/spring.profiles.active=docker/spring.profiles.active=sts/g" capsid/src/main/resources/application.properties
```

## TODO

- [ ] Move from local environment to virtual environment (`pipenv` for now). To make it work in CI we probably need to configure CircleCI python image to be capable with `cmake`/... for the annoying `pygit2` issue.
- [ ] Only test happy path.
