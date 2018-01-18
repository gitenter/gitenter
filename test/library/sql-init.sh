#!/bin/sh

sql_init_single_repo () {

	username=$1
	dbname=$1

	export PGPASSWORD=postgres
	export PGHOST=localhost
	psql -U postgres -w -f $HOME/Workspace/enterovirus/test/library/single-repo-config.sql -v username=$username -v password=$password -v dbname=$dbname

	export PGPASSWORD=postgres
	export PGHOST=localhost
	psql -U $username -d $dbname -w -f $HOME/Workspace/enterovirus/database/initiate_database.sql 
	psql -U $username -d $dbname -w -f $HOME/Workspace/enterovirus/test/library/single-repo-dummy-data.sql
}

sql_init_customized_data () {

	username=$1
	dbname=$1
	datafile=$1-data.sql

	export PGPASSWORD=postgres
	export PGHOST=localhost
	psql -U postgres -w -f $HOME/Workspace/enterovirus/test/library/single-repo-config.sql -v username=$username -v password=$password -v dbname=$dbname

	export PGPASSWORD=postgres
	export PGHOST=localhost
	psql -U $username -d $dbname -w -f $HOME/Workspace/enterovirus/database/initiate_database.sql 
	psql -U $username -d $dbname -w -f $HOME/Workspace/enterovirus/test/$datafile
}

