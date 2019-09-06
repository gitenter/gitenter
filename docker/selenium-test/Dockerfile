FROM circleci/python:3.6.2-stretch-browsers

RUN sudo apt-get update && sudo apt-get install -y curl libssh2-1 cmake

# `chromedriver` has been installed in the parent image.
# We'd like to also install geckodriver so `webdriver.Firefox()`
# can also work.
#
# TODO:
# Currently still not work with error
# > PermissionError: [Errno 13] Permission denied: 'geckodriver.log'
RUN curl -L https://github.com/mozilla/geckodriver/releases/download/v0.24.0/geckodriver-v0.24.0-linux64.tar.gz | sudo tar zx
RUN sudo mv geckodriver /usr/local/bin
RUN sudo chown circleci:circleci /usr/local/bin/geckodriver

COPY selenium-test /selenium-test
RUN sudo sudo pip3 install -r /selenium-test/pip-requirements.txt

RUN sudo sed -i'.original' -e 's/profile = LocalProfile()/profile = DockerProfile()/' /selenium-test/settings/profile.py
RUN sudo chown -R circleci:circleci /selenium-test

USER circleci
RUN cd ~ && ssh-keygen -t rsa -b 4096 -f $HOME/.ssh/id_rsa -N "" -C "my@email.com"
# Until now, if manually add this generated public key to database (through web UI),
# the following command works without asking for a password.
# git clone git@git:/home/git/server.git

WORKDIR /selenium-test

CMD ["/bin/bash"]

# TODO:
# The parent image `circleci/python:3.6.2-stretch-browsers` is 2G+
# Definitely don't need this big. Rewrite the headless browser part and get ride
# of this image + user `circleci`.
