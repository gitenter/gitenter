drop_database () {
    dbname=$1

    export PGPASSWORD=postgres
    export PGHOST=localhost
    psql -U postgres -w -f drop_database.sql -v dbname=$dbname
}

drop_user () {
    export PGPASSWORD=postgres
    export PGHOST=localhost
    psql -U postgres -w -f drop_users.sql
}

create_user() {
    export PGPASSWORD=postgres
    export PGHOST=localhost
    psql -U postgres -w -f create_users.sql
}

init_database () {
    dbname=$1

    export PGPASSWORD=postgres
    export PGHOST=localhost
    psql -U postgres -w -f create_database.sql -v dbname=$dbname

    export PGPASSWORD=zooo
    export PGHOST=localhost
    psql -U gitenter -d $dbname -w -f initiate_database.sql
    psql -U gitenter -d $dbname -w -f privilege_control.sql
}

drop_database 'gitenter'
drop_database 'gitenter_empty'
drop_user

create_user
# The empty test is for reset the database in integrating tests
init_database 'gitenter'
init_database 'gitenter_empty'
