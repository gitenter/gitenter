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

```
brew install chromedriver
brew install Caskroom/versions/google-chrome-canary
```

Add add-ons (a.k.a Selenium IDEs) to the existing browsers ([Chrome](https://chrome.google.com/webstore/detail/selenium-ide/mooikfkahbdckldjjndioackbalphokd?hl=en), [Firefox](https://addons.mozilla.org/en-US/firefox/addon/selenium-ide/), ...) doesn't help. That's for create simple scripts/assist in exploratory testing. Also, notice that add add-ons is a user-level action (no password needed).
