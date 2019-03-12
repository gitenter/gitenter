#!/bin/bash

. lib_user.sh
. lib_database.sh

drop_database 'gitenter'
drop_user

create_user
# The empty test is for reset the database in integrating tests
init_database 'gitenter'
