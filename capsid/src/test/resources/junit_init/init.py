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

    form = client.get('http://localhost:8888/register')

    soup = BeautifulSoup(form.content, 'lxml')
    token = soup.find('input', {'name':'_csrf'})['value']

    user_data = {
        "username" : name,
        "password" : name,
        "displayName" : name,
        "email" : name+"@"+name+".com",
        "_csrf" : token
        }
    url = 'http://localhost:8888/register'
    r = client.post(url, data=user_data, headers=dict(Referer=url))

    print("create "+name+" return code "+str(r.status_code))

create_user('xxxy')
