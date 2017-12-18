#!/usr/bin/python

from util import *

'''
CREATE USER
'''

create_user('user1')
create_user('user2')
create_user('user3')

'''
CREATE ORGANIZATION & REPOSITORY
'''

client = log_in('user1')
create_organization(client, 'org1')
create_organization(client, 'org2')
create_repository(client, 1, 'repo1')
create_repository(client, 1, 'repo2')

