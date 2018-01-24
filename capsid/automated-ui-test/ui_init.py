#!/usr/bin/python

from util import *
import time
import sys

# the URL should go without the "/" at the end of it
root = sys.argv[1]

'''
CREATE USER
'''

create_user(root, 'user1')
create_user(root, 'user2')
create_user(root, 'user3')

'''
CREATE ORGANIZATION & REPOSITORY
'''

client = log_in(root, 'user1')
add_ssh_key(root, client)
create_organization(root, client, 'org1')
create_organization(root, client, 'org2')
create_repository(root, client, 1, 'repo1')
time.sleep(1) # Otherwise the two repos may be made at exactly the same time, so exactly the same SHA checksum hash.
create_repository(root, client, 1, 'repo2')

