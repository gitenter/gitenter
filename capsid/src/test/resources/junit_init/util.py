#!/usr/bin/python

from datetime import datetime
from lxml import html
import requests
from bs4 import BeautifulSoup

root = 'http://localhost:8888' # without the "/" at the end of it

'''
CREATE USER
'''

def create_user (name):

    client = requests.session()

    url = root+'/register'

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

'''
LOG IN
'''

def log_in (name):

    client = requests.session()

    url = root+'/login'

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

    return client

'''
CREATE ORGANIZATION
'''

def create_organization (client, org_name):

    url = root+'/organizations/create'

    form = client.get(url)

    soup = BeautifulSoup(form.content, 'lxml')
    token = soup.find('input', {'name':'_csrf'})['value']

    data = {
        "name" : org_name,
        "displayName" : org_name,
        "_csrf" : token
        }
    r = client.post(url, data=data, headers=dict(Referer=url))

    print("create organization "+org_name+" return code "+str(r.status_code))

