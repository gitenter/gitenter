#!/usr/bin/python

from datetime import datetime
from lxml import html
import requests
from bs4 import BeautifulSoup

def get_csrf (client, url):

    form = client.get(url)

    soup = BeautifulSoup(form.content, 'lxml')
    token = soup.find('input', {'name':'_csrf'})['value']

    return token

'''
CREATE USER
'''

def create_user (root, name):

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

def log_in (root, name):

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
Add SSH KEY
'''

def add_ssh_key (root, client):

	url = root+'/settings/ssh'

	f = open("/home/beta/.ssh/id_rsa.pub", "r")
	data = {
		"key" : f.read(),
        "_csrf" : get_csrf(client, url)
		}
	r = client.post(url, data=data, headers=dict(Referer=url))

	print("add ssh key for the current logged in user return code "+str(r.status_code))

'''
CREATE ORGANIZATION
'''

def create_organization (root, client, org_name):

    url = root+'/organizations/create'

    data = {
        "name" : org_name,
        "displayName" : org_name.upper(),
        "_csrf" : get_csrf(client, url)
        }
    r = client.post(url, data=data, headers=dict(Referer=url))

    print("create organization "+org_name+" return code "+str(r.status_code))

'''
ADD MANAGER
'''

def add_manager (root, client, org_id, manager_username):

    url = root+'/organizations/'+str(org_id)+'/managers'

    data = {
        "managerName" : manager_username,
        "_csrf" : get_csrf(client, url)
        }
    r = client.post(url+'/add', data=data, headers=dict(Referer=url))

    print("add manager "+manager_username+" for organization "+str(org_id)+" return code "+str(r.status_code))

'''
CREATE REPOSITORY
'''

# So it need to secretly know the org_id that is
# managed by the logged in user.
def create_repository (root, client, org_id, repo_name):

    url = root+'/organizations/'+str(org_id)+'/repositories/create'

    data = {
        "name" : repo_name,
        "displayName" : repo_name.upper(),
        "_csrf" : get_csrf(client, url)
        }
    r = client.post(url, data=data, headers=dict(Referer=url))

    print("create repository "+repo_name+" for organization "+str(org_id)+" return code "+str(r.status_code))
