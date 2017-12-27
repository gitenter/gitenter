# git constants
testcasename=hook-update-client-side-fake

# Database constants
username=hook_update_client_side_fake_username
password=hook_update_client_side_fake_password
dbname=hook_update_client_side_fake_dbname

# Clean up
rm -rf $HOME/Workspace/enterovirus-test/$testcasename/
cd $HOME/Workspace/enterovirus-test/
mkdir $testcasename

# Initialize client side git repo
cd $HOME/Workspace/enterovirus-test/$testcasename/
git init

# Initialize SQL database
export PGPASSWORD=postgres
export PGHOST=localhost
psql -U postgres -w -f $HOME/Workspace/enterovirus/test/$testcasename/$testcasename-config.sql

export PGPASSWORD=$password
export PGHOST=localhost
psql -U $username -d $dbname -w -f $HOME/Workspace/enterovirus/database/initiate_database.sql
psql -U $username -d $dbname -w -f $HOME/Workspace/enterovirus/test/$testcasename/$testcasename-data.sql
