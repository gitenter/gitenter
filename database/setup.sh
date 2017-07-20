export PGPASSWORD=postgres
export PGHOST=localhost
psql -U postgres -w -f drop_database.sql
psql -U postgres -w -f drop_users.sql
psql -U postgres -w -f create_users.sql
psql -U postgres -w -f create_database.sql

export PGPASSWORD=zooo
export PGHOST=localhost
psql -U enterovirus -d enterovirus -w -f initiate_database.sql
psql -U enterovirus -d enterovirus -w -f privilege_control.sql
