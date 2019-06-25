#!/bin/bash

# $1 will be the username. Not used in here.
cd /ssheep
pipenv install
pipenv run python get_authorized_keys_content.py
