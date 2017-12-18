#!/usr/bin/python

from util import *

'''
CREATE USER
'''

create_user('xxxy')

'''
CREATE ORGANIZATION
'''

client = log_in('xxxy')
create_organization(client, 'orgxy')

'''
CREATE REPOSITORY
'''

create_repository(client, 1, 'repo1')
