# git constants
testcasename=hook-fake-update

# one commit
cd $HOME/Workspace/enterovirus-test/$testcasename/org/repo
file="blank-file-add-by-`date +%Y%m%d%H%M%S`"
echo "$file" > $file
git add -A
git commit -m "add file "$file
git push origin master

export commit_id=$(git log -1 --pretty="%H")
rm $HOME/Workspace/enterovirus-test/$testcasename/old_commit_sha.txt
mv $HOME/Workspace/enterovirus-test/$testcasename/new_commit_sha.txt $HOME/Workspace/enterovirus-test/$testcasename/old_commit_sha.txt
echo $commit_id > $HOME/Workspace/enterovirus-test/$testcasename/new_commit_sha.txt

