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
time.sleep(1) 
# time.sleep(1) is for otherwise the two repos may be made at 
# exactly the same time, so exactly the same SHA checksum hash.
#
# TODO:
# Remove the unique constrain of SHA checksum hash, since it
# is actually not correct.
# And remove all "findByShaChecksumHash()" related queries.
create_repository(root, client, 1, 'repo2')
time.sleep(1)
create_repository(root, client, 2, 'repo1')
time.sleep(1)
create_repository(root, client, 2, 'repo2')

