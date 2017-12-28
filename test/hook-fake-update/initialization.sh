# git constants
testcasename=hook-fake-update

# Database constants
username=hook_fake_update_username
password=hook_fake_update_password
dbname=hook_fake_update_dbname

# Clean up
rm -rf $HOME/Workspace/enterovirus-test/$testcasename/
cd $HOME/Workspace/enterovirus-test/
mkdir $testcasename
cd $testcasename
mkdir org

# Initialize commit SHA
cd $HOME/Workspace/enterovirus-test/$testcasename/
echo "0000000000000000000000000000000000000000" > old_commit_sha.txt
echo "0000000000000000000000000000000000000000" > new_commit_sha.txt

# Initialize server side git repo
cd $HOME/Workspace/enterovirus-test/$testcasename/org/
mkdir repo.git
cd repo.git
git init --bare

# Initialize client side git repo
cd $HOME/Workspace/enterovirus-test/$testcasename/org/
mkdir repo
cd repo
git init
git remote add origin $HOME/Workspace/enterovirus-test/$testcasename/org/repo.git

# Initialize SQL database
export PGPASSWORD=postgres
export PGHOST=localhost
psql -U postgres -w -f $HOME/Workspace/enterovirus/test/$testcasename/$testcasename-config.sql

export PGPASSWORD=$password
export PGHOST=localhost
psql -U $username -d $dbname -w -f $HOME/Workspace/enterovirus/database/initiate_database.sql
psql -U $username -d $dbname -w -f $HOME/Workspace/enterovirus/test/$testcasename/$testcasename-data.sql
