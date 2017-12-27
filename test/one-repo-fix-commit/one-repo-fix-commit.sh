# Clean up
rm -rf $HOME/Workspace/enterovirus-test/one-repo-fix-commit/
cd $HOME/Workspace/enterovirus-test/
mkdir one-repo-fix-commit
cd one-repo-fix-commit
mkdir org
cd $HOME/Workspace/enterovirus/test/one-repo-fix-commit
rm one-repo-fix-commit-data.sql
cp one-repo-fix-commit-data-template.sql one-repo-fix-commit-data.sql


# Initialize server side git repo
cd $HOME/Workspace/enterovirus-test/one-repo-fix-commit/org/
mkdir repo.git
cd repo.git
git init --bare

# Initialize client side git repo
cd $HOME/Workspace/enterovirus-test/one-repo-fix-commit/org/
mkdir repo
cd repo
git init
git remote add origin $HOME/Workspace/enterovirus-test/one-repo-fix-commit/org/repo.git

# Fake client side git 1st commit
cd $HOME/Workspace/enterovirus-test/one-repo-fix-commit/org/repo
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
sed -i "s/\t(1, 1, TO-BE-1ST-COMMIT-SHA),/\t(1, 1, '"$commit_id"'),/g" $HOME/Workspace/enterovirus/test/one-repo-fix-commit/one-repo-fix-commit-data.sql

# Fake client side git 2nd commit
cd $HOME/Workspace/enterovirus-test/one-repo-fix-commit/org/repo
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
sed -i "s/\t(2, 1, TO-BE-2ND-COMMIT-SHA);/\t(2, 1, '"$commit_id"');/g" $HOME/Workspace/enterovirus/test/one-repo-fix-commit/one-repo-fix-commit-data.sql

# Update SQL database
export PGPASSWORD=postgres
export PGHOST=localhost
psql -U postgres -w -f $HOME/Workspace/enterovirus/test/one-repo-fix-commit/one-repo-fix-commit-config.sql

export PGPASSWORD=zooo
export PGHOST=localhost
psql -U enterovirus_test -d one_repo_fix_commit -w -f $HOME/Workspace/enterovirus/database/initiate_database.sql
psql -U enterovirus_test -d one_repo_fix_commit -w -f $HOME/Workspace/enterovirus/test/one-repo-fix-commit/one-repo-fix-commit-data.sql
