export PGPASSWORD=zooo
export PGHOST=localhost

#psql -U enterovirus -d enterovirus -w -f truncate_all_tables_generator.sql -o truncate_all_tables.sql
#sed -i '1,2d' truncate_all_tables.sql
#sed -i "$(($(wc -l < truncate_all_tables.sql)-1)),\$d" truncate_all_tables.sql
#psql -U enterovirus -d enterovirus -w -f truncate_all_tables.sql
#rm truncate_all_tables.sql

psql -U enterovirus -d enterovirus -w -f data.sql
