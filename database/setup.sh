init_database () {

    dbname=$1

    export PGPASSWORD=postgres
    export PGHOST=localhost
    psql -U postgres -w -f drop_database.sql -v dbname=$dbname
    psql -U postgres -w -f drop_users.sql
    psql -U postgres -w -f create_users.sql
    psql -U postgres -w -f create_database.sql -v dbname=$dbname

    export PGPASSWORD=zooo
    export PGHOST=localhost
    psql -U enterovirus -d $dbname -w -f initiate_database.sql
    psql -U enterovirus -d $dbname -w -f privilege_control.sql
}

init_database 'enterovirus'

# The empty test is for reset the database in integrating tests
init_database 'enterovirus_empty'
