FROM ubuntu:18.04

RUN apt-get update && apt-get -y install \
    apt-utils \
    wget \
    zip \
    gzip \
    python3-pip

RUN pip3 install selenium

WORKDIR /download

RUN wget -O ./geckodriver-v0.23.0-linux64.tar.gz "https://github.com/mozilla/geckodriver/releases/download/v0.23.0/geckodriver-v0.23.0-linux64.tar.gz"
RUN tar xzvf geckodriver-v0.23.0-linux64.tar.gz
RUN mv geckodriver /usr/local/bin

RUN wget -O ./chromedriver_linux64.zip "https://chromedriver.storage.googleapis.com/2.45/chromedriver_linux64.zip"
RUN unzip chromedriver_linux64.zip
RUN mv chromedriver /usr/local/bin

RUN apt-get update && apt-get -y install \
    firefox

# TODO:
# We would like to use a "headless browser" to run the test, as we don't have a
# GUI for our docker container so Firefox/Chrome are not our choices.
# Possible resources:
# `HtmlUnitDriver` - https://github.com/SeleniumHQ/selenium/wiki/HtmlUnitDriver
#                  - https://stackoverflow.com/questions/4618373/how-do-i-use-the-htmlunit-driver-with-selenium-from-python/5518175#5518175
# `xvfb` - https://stackoverflow.com/questions/10399557/is-it-possible-to-run-selenium-firefox-web-driver-without-a-gui
# Ghost Driver - https://github.com/detro/ghostdriver
