#!/usr/bin/python

from util import *
import time
import sys

# the URL should go without the "/" at the end of it
root = sys.argv[1]

'''
USER
'''

create_user(root, 'user1', 200)
create_user(root, 'user2', 200)
create_user(root, 'user3', 200)
create_user(root, 'user4', 200)
create_user(root, 'user5', 200)
create_user(root, 'user6', 200)
create_user(root, 'user7', 200)
create_user(root, 'user8', 200)

'''
ORGANIZATION
'''

user1_session = log_in(root, 'user1', 200)
user2_session = log_in(root, 'user2', 200)
user3_session = log_in(root, 'user3', 200)
user4_session = log_in(root, 'user4', 200)
user5_session = log_in(root, 'user5', 200)
user6_session = log_in(root, 'user6', 200)
user7_session = log_in(root, 'user7', 200)
user8_session = log_in(root, 'user8', 200)

# Since I am using the SSH key myself (the current Linux user),
# at most one user can have this key.
add_ssh_key(root, user1_session, 200)

create_organization(root, user1_session, 'org1', 200)
create_organization(root, user1_session, 'org2', 200)

# Since user1 create the org1, up to now only
# user1 is the manager of it. After the setting
# both user1 and user2 are managers of org1
add_manager(root, user1_session, 1, 'user2', 200)
add_manager(root, user1_session, 1, 'user3', 200)
add_manager(root, user1_session, 1, 'user4', 200)
remove_manager(root, user1_session, 1, 3, 200)
remove_manager(root, user1_session, 1, 4, 200)

# At the beginning user1 is the manager of org1.
# After the settings, user3 and user4 are left
# as manager.
add_manager(root, user1_session, 2, 'user3', 200)
remove_manager(root, user3_session, 2, 1, 200)
add_manager(root, user3_session, 2, 'user4', 200)

# Check that indeed user1 and user2 are manager of org1
load_manager_setting_page(root, user1_session, 1, 200)
load_manager_setting_page(root, user2_session, 1, 200)
load_manager_setting_page(root, user3_session, 1, 403)
load_manager_setting_page(root, user4_session, 1, 403)
load_create_repository_page(root, user1_session, 1, 200)
load_create_repository_page(root, user2_session, 1, 200)
load_create_repository_page(root, user3_session, 1, 403)
load_create_repository_page(root, user4_session, 1, 403)

# Check that indeed user3 and user4 are manager of org2
load_manager_setting_page(root, user1_session, 2, 403)
load_manager_setting_page(root, user2_session, 2, 403)
load_manager_setting_page(root, user3_session, 2, 200)
load_manager_setting_page(root, user4_session, 2, 200)
load_create_repository_page(root, user1_session, 2, 403)
load_create_repository_page(root, user2_session, 2, 403)
load_create_repository_page(root, user3_session, 2, 200)
load_create_repository_page(root, user4_session, 2, 200)

'''
REPOSITORY
'''

# Create repository:
# User1 and user2 are managers of org1
create_repository(root, user1_session, 1, 'repo1', "false", "true", 200)
create_repository(root, user2_session, 1, 'repo2', "true", "false", 200)
# User3 and user4 are managers of org2
create_repository(root, user3_session, 2, 'repo1', "true", "false", 200)
create_repository(root, user3_session, 2, 'repo2', "true", "false", 200)
create_repository(root, user4_session, 2, 'repo3', "true", "false", 200)

# Since only user1 and user2 are managers of org1, user3 and
# user4 should not have authorization to set collaborators.
load_collaborator_setting_page(root, user2_session, 1, 1, 200)
load_collaborator_setting_page(root, user2_session, 1, 2, 200)
load_collaborator_setting_page(root, user3_session, 1, 1, 403)
load_collaborator_setting_page(root, user4_session, 1, 2, 403)

# Add and remove collaborators of repo1 by both user1 and user2
add_collaborator(root, user1_session, 1, 1, 'user1', 'PROJECT_LEADER', 200)
add_collaborator(root, user1_session, 1, 1, 'user2', 'EDITOR', 200)
add_collaborator(root, user1_session, 1, 1, 'user3', 'REVIEWER', 200)
add_collaborator(root, user1_session, 1, 1, 'user4', 'READER', 200)
add_collaborator(root, user2_session, 1, 1, 'user5', 'PROJECT_LEADER', 200)
add_collaborator(root, user2_session, 1, 1, 'user6', 'EDITOR', 200)
add_collaborator(root, user2_session, 1, 1, 'user7', 'REVIEWER', 200)
add_collaborator(root, user2_session, 1, 1, 'user8', 'READER', 200)
remove_collaborator(root, user1_session, 1, 1, 2, 200)
remove_collaborator(root, user1_session, 1, 1, 3, 200)
# Should then user 1,4,5,6,7,8 left.

# Check that user 2,3 cannot access repo1, while 
# user 1,4 can.
load_repository_page(root, user1_session, 1, 1, 200)
load_repository_page(root, user2_session, 1, 1, 403)
load_repository_page(root, user3_session, 1, 1, 403)
load_repository_page(root, user4_session, 1, 1, 200)

# Since repo2 is public, everybody (although no collaborator
# is set up yet) can access it.
load_repository_page(root, user1_session, 1, 2, 200)
load_repository_page(root, user2_session, 1, 2, 200)
load_repository_page(root, user3_session, 1, 2, 200)
load_repository_page(root, user4_session, 1, 2, 200)
