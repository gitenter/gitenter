#!/bin/bash

. lib_user.sh
. lib_database.sh

create_user
# The empty test is for reset the database in integrating tests
init_database 'gitenter'
init_database 'gitenter_empty'
