username=user_auth
password=zooo
dbname=user_auth

# Update SQL database
export PGPASSWORD=postgres
export PGHOST=localhost
psql -U postgres -w -f $HOME/Workspace/enterovirus/test/user-auth/user-auth-config.sql

export PGPASSWORD=$password
export PGHOST=localhost
echo $password
echo $PGPASSWORD
psql -U $username -d $dbname -w -f $HOME/Workspace/enterovirus/database/initiate_database.sql
psql -U $username -d $dbname -w -f $HOME/Workspace/enterovirus/test/user-auth/user-auth-data.sql
