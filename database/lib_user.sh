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
