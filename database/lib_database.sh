drop_database () {
    dbname=$1

    export PGPASSWORD=postgres
    export PGHOST=localhost
    psql -U postgres -w -f drop_database.sql -v dbname=$dbname
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
    psql -U gitenter -d $dbname -w -f alter_sequence.sql
}
