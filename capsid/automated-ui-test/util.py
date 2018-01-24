#!/usr/bin/python

from datetime import datetime
from lxml import html
import requests
from bs4 import BeautifulSoup

# without the "/" at the end of it
#root = 'http://localhost:8080/capsid-0.0.1-prototype/' 
root = 'http://localhost:8888/' 

def get_csrf (client, url):

    form = client.get(url)

    soup = BeautifulSoup(form.content, 'lxml')
    token = soup.find('input', {'name':'_csrf'})['value']

    return token

'''
CREATE USER
'''

def create_user (name):

    client = requests.session()

    url = root+'/register'

    data = {
        "username" : name,
        "password" : name,
        "displayName" : name.upper(),
        "email" : name+"@"+name+".com",
        "_csrf" : get_csrf(client, url)
        }
    r = client.post(url, data=data, headers=dict(Referer=url))

    print("create "+name+" return code "+str(r.status_code))

'''
LOG IN
'''

def log_in (name):

    client = requests.session()

    url = root+'/login'

    data = {
        "username" : name,
        "password" : name,
        "_csrf" : get_csrf(client, url)
        }
    r = client.post(url, data=data, headers=dict(Referer=url))

    print("log in "+name+" return code "+str(r.status_code))

    return client

'''
CREATE ORGANIZATION
'''

def create_organization (client, org_name):

    url = root+'/organizations/create'

    data = {
        "name" : org_name,
        "displayName" : org_name.upper(),
        "_csrf" : get_csrf(client, url)
        }
    r = client.post(url, data=data, headers=dict(Referer=url))

    print("create organization "+org_name+" return code "+str(r.status_code))

'''
CREATE ORGANIZATION
'''

# So it need to secretly know the org_id that is
# managed by the logged in user.
def create_repository (client, org_id, repo_name):

    url = root+'/organizations/'+str(org_id)+'/repositories/create'

    data = {
        "name" : repo_name,
        "displayName" : repo_name.upper(),
        "_csrf" : get_csrf(client, url)
        }
    r = client.post(url, data=data, headers=dict(Referer=url))

    print("create repository "+repo_name+" return code "+str(r.status_code))
