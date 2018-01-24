#!/usr/bin/python

from util import *
import time

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
add_ssh_key(client)
create_organization(client, 'org1')
create_organization(client, 'org2')
create_repository(client, 1, 'repo1')
time.sleep(1) # Otherwise the two repos may be made at exactly the same time, so exactly the same SHA checksum hash.
create_repository(client, 1, 'repo2')

