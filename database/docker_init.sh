create_user() {
    # export PGPASSWORD=postgres
    psql -U postgres -w -f /database/create_users.sql
}

init_database () {
    dbname=$1

    export PGPASSWORD=postgres
    psql -U postgres -w -f /database/create_database.sql -v dbname=$dbname

    export PGPASSWORD=zooo
    psql -U gitenter -d $dbname -w -f /database/initiate_database.sql
    psql -U gitenter -d $dbname -w -f /database/privilege_control.sql
}

create_user
init_database 'gitenter'
