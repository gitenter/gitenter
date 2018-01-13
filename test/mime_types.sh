testcasename=mime_types

# Initialize git
. $HOME/Workspace/enterovirus/test/library/git-init.sh || exit 1
git_init_single_repo $testcasename

# Fake a commit with two document files with traceable items inside
cd $gitclientfilepath

echo $gitclientfilepath

cp $HOME/Workspace/enterovirus/test/files/sample.jpg $gitclientfilepath
cp $HOME/Workspace/enterovirus/test/files/sample.png $gitclientfilepath
cp $HOME/Workspace/enterovirus/test/files/sample.gif $gitclientfilepath
cp $HOME/Workspace/enterovirus/test/files/sample.md $gitclientfilepath
cp $HOME/Workspace/enterovirus/test/files/sample.html $gitclientfilepath
cp $HOME/Workspace/enterovirus/test/files/sample.pdf $gitclientfilepath
cp $HOME/Workspace/enterovirus/test/files/Sample.java $gitclientfilepath

git add -A
git commit -m "Commit of two documents with traceability relationship in between."
git push origin master

export commit_id=$(git log -1 --pretty="%H")
echo $commit_id >> $rootfilepath/commit-sha-list.txt

# Initialize SQL database
. $HOME/Workspace/enterovirus/test/library/sql-init.sh || exit 1
sql_init_single_repo $testcasename
