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
create_user(root, 'user4')
create_user(root, 'user5')
create_user(root, 'user6')
create_user(root, 'user7')
create_user(root, 'user8')

'''
CREATE ORGANIZATION & REPOSITORY
'''

user1_session = log_in(root, 'user1')
user2_session = log_in(root, 'user2')
user3_session = log_in(root, 'user3')
user4_session = log_in(root, 'user4')

# since I am using the SSH key myself (the current Linux user),
# at most one user can have this key.
add_ssh_key(root, user1_session)

create_organization(root, user1_session, 'org1')
create_organization(root, user1_session, 'org2')

add_manager(root, user1_session, 1, 'user2')
add_manager(root, user1_session, 1, 'user3')
add_manager(root, user1_session, 1, 'user4')
remove_manager(root, user1_session, 1, 3)
remove_manager(root, user1_session, 1, 4) 
# So should only user1 and user2 left as a manager

create_repository(root, user1_session, 1, 'repo1')
create_repository(root, user1_session, 1, 'repo2')
create_repository(root, user1_session, 2, 'repo1')
create_repository(root, user1_session, 2, 'repo2')
create_repository(root, user1_session, 2, 'repo3')

add_collaborator(root, user1_session, 1, 1, 'user1', 'PROJECT_LEADER')
add_collaborator(root, user1_session, 1, 1, 'user2', 'EDITOR')
add_collaborator(root, user1_session, 1, 1, 'user3', 'REVIEWER')
add_collaborator(root, user1_session, 1, 1, 'user4', 'READER')
add_collaborator(root, user2_session, 1, 1, 'user5', 'READER')
add_collaborator(root, user2_session, 1, 1, 'user6', 'READER')
add_collaborator(root, user2_session, 1, 1, 'user7', 'READER')
add_collaborator(root, user2_session, 1, 1, 'user8', 'READER')
remove_collaborator(root, user1_session, 1, 1, 6)
remove_collaborator(root, user1_session, 1, 1, 7)
remove_collaborator(root, user1_session, 1, 1, 8)
# Should then user 1,2,3,4,5 left.
