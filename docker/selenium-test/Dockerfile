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

# Seems have a problem running it, because there's no GUI (so no proper running
# firefox) in the docker container.
