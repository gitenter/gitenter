#!/usr/bin/python

from datetime import datetime
from lxml import html
import requests
from bs4 import BeautifulSoup
from random import randint

# TODO:
# For a lot of pages with form validation involved, failed commands actually
# returns 200 (as they redirect to the original page and ask the user to refill
# it).
# Any way to show in there that something has gone wrong??

def get_csrf (client, url):

    form = client.get(url)

    soup = BeautifulSoup(form.content, 'lxml')
    token = soup.find('input', {'name':'_csrf'})['value']

    return token

'''
CREATE USER
'''

def create_user (root, name, expect_status_code):

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

    if (r.status_code != expect_status_code):
        print("create "+name+" return code "+str(r.status_code))

'''
LOG IN
'''

def log_in (root, name, expect_status_code):

    client = requests.session()

    url = root+'/login'

    data = {
        "username" : name,
        "password" : name,
        "_csrf" : get_csrf(client, url)
        }
    r = client.post(url, data=data, headers=dict(Referer=url))

    if (r.status_code != expect_status_code):
        print("log in "+name+" return code "+str(r.status_code))

    return client

'''
Add SSH KEY
'''

def add_ssh_key (root, client, expect_status_code):

    url = root+'/settings/ssh'

    f = open("/home/beta/.ssh/id_rsa.pub", "r")

    data = {
        "value" : f.read().rstrip(), # Here has a "\n" at the very end. That is allowed for the system.
        "_csrf" : get_csrf(client, url)
    }
    r = client.post(url, data=data, headers=dict(Referer=url))

    if (r.status_code != expect_status_code):
        print("add ssh key for the current logged in user return code "+str(r.status_code))

'''
CREATE ORGANIZATION
'''

def create_organization (root, client, org_name, expect_status_code):

    url = root+'/organizations/create'

    data = {
        "name" : org_name,
        "displayName" : org_name.upper(),
        "_csrf" : get_csrf(client, url)
        }
    r = client.post(url, data=data, headers=dict(Referer=url))

    if (r.status_code != expect_status_code):
        print("create organization "+org_name+" return code "+str(r.status_code))

'''
MANAGER
'''

def load_manager_setting_page (root, client, org_id, expect_status_code):

    url = root+'/organizations/'+str(org_id)+'/managers'
    r = client.get(url)

    if (r.status_code != expect_status_code):
        print("load manager setting page for organization "+str(org_id)+" return code "+str(r.status_code))

# TODO:
# For user who do not have authorization to add/remove managers,
# since they cannot load the page (get the CSRF key) at the first
# step, there is no way to check that these POST method will
# return 404. Find a way to do it.
def add_manager (root, client, org_id, manager_username, expect_status_code):

    url = root+'/organizations/'+str(org_id)+'/managers'

    data = {
        "username" : manager_username,
        "_csrf" : get_csrf(client, url)
        }
    r = client.post(url+'/add', data=data, headers=dict(Referer=url))

    if (r.status_code != expect_status_code):
        print("add manager "+manager_username+" for organization "+str(org_id)+" return code "+str(r.status_code))

def remove_manager (root, client, org_id, member_id, expect_status_code):

    url = root+'/organizations/'+str(org_id)+'/managers'

    data = {
        "member_id" : member_id,
        "_csrf" : get_csrf(client, url)
        }
    r = client.post(url+'/remove', data=data, headers=dict(Referer=url))

    if (r.status_code != expect_status_code):
        print("remove manager id "+str(member_id)+" for organization "+str(org_id)+" return code "+str(r.status_code))

'''
REPOSITORY
'''

def load_create_repository_page (root, client, org_id, expect_status_code):

    url = root+'/organizations/'+str(org_id)+'/repositories/create'
    r = client.get(url)

    if (r.status_code != expect_status_code):
        print("load create repository page for organization "+str(org_id)+" return code "+str(r.status_code))

# So it need to secretly know the org_id that is
# managed by the logged in user.
def create_repository (root, client, org_id, repo_name, include_setup_files, expect_status_code):

    url = root+'/organizations/'+str(org_id)+'/repositories/create'

    data = {
        "name" : repo_name,
        "displayName" : repo_name.upper(),
		"description" : " ".join("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.".split(" ")[:randint(0,30)]),
        "include_setup_files" : include_setup_files,
        "_csrf" : get_csrf(client, url)
        }
    r = client.post(url, data=data, headers=dict(Referer=url))

    if (r.status_code != expect_status_code):
        print("create repository "+repo_name+" for organization "+str(org_id)+" return code "+str(r.status_code))

'''
COLLABORATOR
'''

def load_collaborator_setting_page (root, client, org_id, repo_id, expect_status_code):

    url = root+'/organizations/'+str(org_id)+'/repositories/'+str(repo_id)+"/collaborators"
    r = client.get(url)

    if (r.status_code != expect_status_code):
        print("load create repository page for organization "+str(org_id)+" return code "+str(r.status_code))

def add_collaborator (root, client, org_id, repo_id, collaborator_username, collaborator_role, expect_status_code):

    url = root+'/organizations/'+str(org_id)+'/repositories/'+str(repo_id)+"/collaborators"

    data = {
        "username" : collaborator_username,
        "role" : collaborator_role,
        "_csrf" : get_csrf(client, url)
        }
    r = client.post(url+'/add', data=data, headers=dict(Referer=url))

    if (r.status_code != expect_status_code):
        print("add collaborator "+collaborator_username+" as a "+collaborator_role+" for org "+str(org_id)+" repo "+str(repo_id)+" return code "+str(r.status_code))

# Here uses repository_user_map_id with is hard to guess/remember.
# More complicated unit test need to parse the url to get the values.
def remove_collaborator (root, client, org_id, repo_id, repository_user_map_id, expect_status_code):

    url = root+'/organizations/'+str(org_id)+'/repositories/'+str(repo_id)+"/collaborators"

    data = {
        "repository_member_map_id" : repository_user_map_id,
        "_csrf" : get_csrf(client, url)
        }
    r = client.post(url+'/remove', data=data, headers=dict(Referer=url))

    if (r.status_code != expect_status_code):
        print("remove collaborator with map id "+str(repository_user_map_id)+" for org "+str(org_id)+" return code "+str(r.status_code))

