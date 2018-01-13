# git constants
testcasename=one-repo-fix-commit

# Database constants
username=one_repo_fix_commit_username
password=one_repo_fix_commit_password
dbname=one_repo_fix_commit_dbname

# Clean up
rm -rf $HOME/Workspace/enterovirus-test/$testcasename/
cd $HOME/Workspace/enterovirus-test/
mkdir $testcasename
cd $testcasename
touch commit-sha-list.txt
mkdir org
cd $HOME/Workspace/enterovirus/test/$testcasename
rm $testcasename-data.sql
cp $testcasename-data-template.sql $testcasename-data.sql

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

# Fake client side git 1st commit
cd $HOME/Workspace/enterovirus-test/$testcasename/org/repo
touch 1st-commit-file-under-root
echo "1st-commit-file-under-root" > 1st-commit-file-under-root
touch 1st-commit-file-to-be-change-in-the-2nd-commit
echo "1st-commit-file-to-be-change-in-the-2nd-commit:\nbefore change" > 1st-commit-file-to-be-change-in-the-2nd-commit
mkdir 1st-commit-folder
touch 1st-commit-folder/1st-commit-file-under-1st-commit-folder
echo "1st-commit-folder/1st-commit-file-under-1st-commit-folder" > 1st-commit-folder/1st-commit-file-under-1st-commit-folder
git add -A
git commit -m "1st commit"
git push origin master

# Update commit sha in SQL script for for the 1st commit
export commit_id=$(git log -1 --pretty="%H")
echo $commit_id >> $HOME/Workspace/enterovirus-test/$testcasename/commit-sha-list.txt
sed -i "s/\t(1, 1, TO-BE-1ST-COMMIT-SHA),/\t(1, 1, '"$commit_id"'),/g" $HOME/Workspace/enterovirus/test/$testcasename/$testcasename-data.sql

# Fake client side git 2nd commit
cd $HOME/Workspace/enterovirus-test/$testcasename/org/repo
echo "1st-commit-file-to-be-change-in-the-2nd-commit:\nchanged" > 1st-commit-file-to-be-change-in-the-2nd-commit
touch 2nd-commit-file-under-root
echo "2nd-commit-file-under-root" > 2nd-commit-file-under-root
touch 1st-commit-folder/2nd-commit-file-under-1st-commit-folder
echo "1st-commit-folder/2nd-commit-file-under-1st-commit-folder" > 1st-commit-folder/2nd-commit-file-under-1st-commit-folder
mkdir 2nd-commit-folder
touch 2nd-commit-folder/2nd-commit-file-under-2nt-commit-folder
echo "2nd-commit-folder/2nd-commit-file-under-2nt-commit-folder" > 2nd-commit-folder/2nd-commit-file-under-2nt-commit-folder
git add -A
git commit -m "2nd commit"
git push origin master

# Update commit sha in SQL script for the 2nd commit
export commit_id=$(git log -1 --pretty="%H")
echo $commit_id >> $HOME/Workspace/enterovirus-test/$testcasename/commit-sha-list.txt
sed -i "s/\t(2, 1, TO-BE-2ND-COMMIT-SHA);/\t(2, 1, '"$commit_id"');/g" $HOME/Workspace/enterovirus/test/$testcasename/$testcasename-data.sql

# Initialize SQL database
export PGPASSWORD=postgres
export PGHOST=localhost
psql -U postgres -w -f $HOME/Workspace/enterovirus/test/$testcasename/$testcasename-config.sql

export PGPASSWORD=$password
export PGHOST=localhost
psql -U $username -d $dbname -w -f $HOME/Workspace/enterovirus/database/initiate_database.sql
psql -U $username -d $dbname -w -f $HOME/Workspace/enterovirus/test/$testcasename/$testcasename-data.sql
