#!/usr/bin/python

from datetime import datetime
from lxml import html
import requests
from bs4 import BeautifulSoup

'''
CREATE USER
'''

def create_user (name):

    client = requests.session()

    url = 'http://localhost:8888/register'

    form = client.get(url)

    soup = BeautifulSoup(form.content, 'lxml')
    token = soup.find('input', {'name':'_csrf'})['value']

    data = {
        "username" : name,
        "password" : name,
        "displayName" : name,
        "email" : name+"@"+name+".com",
        "_csrf" : token
        }
    r = client.post(url, data=data, headers=dict(Referer=url))

    print("create "+name+" return code "+str(r.status_code))

create_user('xxxy')

'''
CREATE ORGANIZATION
'''

def create_organization (name):

    client = requests.session()

    url = 'http://localhost:8888/login'

    form = client.get(url)

    soup = BeautifulSoup(form.content, 'lxml')
    token = soup.find('input', {'name':'_csrf'})['value']

    data = {
        "username" : name,
        "password" : name,
        "_csrf" : token
        }
    r = client.post(url, data=data, headers=dict(Referer=url))

    print("log in "+name+" return code "+str(r.status_code))

    '''
    Log in not successful yet.
    '''

create_organization('xxxy')
