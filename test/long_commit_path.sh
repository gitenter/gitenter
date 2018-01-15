testcasename=long_commit_path

# Initialize git
. $HOME/Workspace/enterovirus/test/library/git-init.sh || exit 1
git_init_single_repo $testcasename

# setup branches
master=master
unmergebranch=unmergebranch
mergebranch=mergebranch

generate_a_git_commit () {
	branchname=$1

	cd $HOME/Workspace/enterovirus-test/$testcasename/org/repo
	currenttime="`date +%Y%m%d%H%M%S%s%N`"
	echo "file content of $currenttime" > filename-$currenttime
	git add -A
	git commit -m "commit message of "$currenttime
	git push origin $branchname

	export commit_id=$(git log -1 --pretty="%H")
	echo $commit_id >> $rootfilepath/commit-sha-list-$branchname.txt
}

# fake long commit path
#
# TODO:
# There seems a bug in git. The topology of the commits in the
# following fake series is not correct (both using "git log", or
# shown in the graph of "gitg" -- that should be gotten by
# something like "rev.parent()") at least for this case that 
# the commit time are really near each other.
# Need to simplify this fake case, and locate the problem later.
for i in 1 2 3 4 5
do
	generate_a_git_commit $master
done

git branch $mergebranch
git checkout $mergebranch
for i in 1 2 3 4
do
	generate_a_git_commit $mergebranch
done

git checkout $master
for i in 1 2
do
	generate_a_git_commit $master
done
git merge $mergebranch
git branch -d $mergebranch
git tag merged
git push origin --tags

git branch $unmergebranch
git checkout $unmergebranch
for i in 1 2 3 4 5 6 7
do
	generate_a_git_commit $unmergebranch
done

git checkout $master
for i in 1 2 3
do
	generate_a_git_commit $master
done
git tag newest
git push origin --tags

# Initialize SQL database
. $HOME/Workspace/enterovirus/test/library/sql-init.sh || exit 1
sql_init_single_repo $testcasename
