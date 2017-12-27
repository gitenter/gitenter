username=user_auth_username
password=user_auth_password
dbname=user_auth_dbname

# Update SQL database
export PGPASSWORD=postgres
export PGHOST=localhost
psql -U postgres -w -f $HOME/Workspace/enterovirus/test/user-auth/user-auth-config.sql

export PGPASSWORD=$password
export PGHOST=localhost
psql -U $username -d $dbname -w -f $HOME/Workspace/enterovirus/database/initiate_database.sql
psql -U $username -d $dbname -w -f $HOME/Workspace/enterovirus/test/user-auth/user-auth-data.sql
