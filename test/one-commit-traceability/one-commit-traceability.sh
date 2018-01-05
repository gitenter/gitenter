# git constants
testcasename=one-commit-traceability

# Database constants
username=one_commit_traceability_username
password=one_commit_traceability_password
dbname=one_commit_traceability_dbname

# Clean up
rm -rf $HOME/Workspace/enterovirus-test/$testcasename/
cd $HOME/Workspace/enterovirus-test/
mkdir $testcasename
cd $testcasename
touch commit-sha-list.txt
mkdir org

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

# Fake a commit with two document files with traceable items inside
cd $HOME/Workspace/enterovirus-test/$testcasename/org/repo
cat > document-1.md <<- EOM
~~garbage~~not part of the traceable items~~
- [document-1-tag-0001] document-1-0001-content
- [document-1-tag-0002] document-1-0002-content
- [document-1-tag-0003] document-1-0003-content
~~garbage~~not part of the traceable items~~
EOM
cat > document-2.md <<- EOM
~~garbage~~not part of the traceable items~~
- [document-2-tag-0001] document-2-0001-content
- [document-2-tag-0002]{document-1-tag-0001} document-2-0002-content
- [document-2-tag-0003]{document-1-tag-0001,document-1-tag-0002} document-2-0003-content
- [document-2-tag-0004]{document-2-tag-0001} document-2-0004-content
~~garbage~~not part of the traceable items~~
EOM
git add -A
git commit -m "Commit of two documents with traceability relationship in between."
git push origin master

export commit_id=$(git log -1 --pretty="%H")
echo $commit_id >> $HOME/Workspace/enterovirus-test/$testcasename/commit-sha-list.txt

# Initialize SQL database
export PGPASSWORD=postgres
export PGHOST=localhost
psql -U postgres -w -f $HOME/Workspace/enterovirus/test/$testcasename/$testcasename-config.sql

export PGPASSWORD=$password
export PGHOST=localhost
psql -U $username -d $dbname -w -f $HOME/Workspace/enterovirus/database/initiate_database.sql
psql -U $username -d $dbname -w -f $HOME/Workspace/enterovirus/test/$testcasename/$testcasename-data.sql
