testcasename=one_repo_fix_commit

# Initialize git
. $HOME/Workspace/enterovirus/test/library/git-init.sh || exit 1
git_init_single_repo $testcasename

cd $HOME/Workspace/enterovirus/test
rm $testcasename-data.sql
cp $testcasename-data-template.sql $testcasename-data.sql

# Fake client side git 1st commit
cd $gitclientfilepath
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
echo $commit_id >> $rootfilepath/commit-sha-list.txt
sed -i "s/\t(1, 1, TO-BE-1ST-COMMIT-SHA),/\t(1, 1, '"$commit_id"'),/g" $HOME/Workspace/enterovirus/test/$testcasename-data.sql

# Fake client side git 2nd commit
cd $gitclientfilepath
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
echo $commit_id >> $rootfilepath/commit-sha-list.txt
sed -i "s/\t(2, 1, TO-BE-2ND-COMMIT-SHA);/\t(2, 1, '"$commit_id"');/g" $HOME/Workspace/enterovirus/test/$testcasename-data.sql

# Initialize SQL database
. $HOME/Workspace/enterovirus/test/library/sql-init.sh || exit 1
sql_init_customized_data $testcasename
