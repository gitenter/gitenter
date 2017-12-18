export PGPASSWORD=zooo
export PGHOST=localhost

psql -U enterovirus -d enterovirus -w -f initiate_views.sql
psql -U enterovirus -d enterovirus -w -f privilege_control.sql

