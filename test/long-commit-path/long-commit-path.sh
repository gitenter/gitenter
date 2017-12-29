# git constants
testcasename=long-commit-path
unmergebranch=unmergebranch
mergebranch=mergebranch

# Database constants
username=long_commit_path_username
password=long_commit_path_password
dbname=long_commit_path_dbname

# Clean up
rm -rf $HOME/Workspace/enterovirus-test/$testcasename/
cd $HOME/Workspace/enterovirus-test/
mkdir $testcasename
cd $testcasename
touch commit-sha-list-master.txt
touch commit-sha-list-$unmergebranch.txt
touch commit-sha-list-$mergebranch.txt
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

generate_a_git_commit () {
	branchname=$1

	cd $HOME/Workspace/enterovirus-test/$testcasename/org/repo
	currenttime="`date +%Y%m%d%H%M%S%s%N`"
	echo "file content of $currenttime" > filename-$currenttime
	git add -A
	git commit -m "commit message of "$currenttime
	git push origin $branchname

	export commit_id=$(git log -1 --pretty="%H")
	echo $commit_id >> $HOME/Workspace/enterovirus-test/$testcasename/commit-sha-list-$branchname.txt
}

# fake long commit path
for i in 1 2 3 4 5
do
	generate_a_git_commit master
done

git branch $mergebranch
git checkout $mergebranch
for i in 1 2 3 4
do
	generate_a_git_commit $mergebranch
done

git checkout master
for i in 1 2
do
	generate_a_git_commit master
done
git merge $mergebranch
git branch -d $mergebranch

git branch $unmergebranch
git checkout $unmergebranch
for i in 1 2 3 4 5 6 7
do
	generate_a_git_commit $unmergebranch
done

git checkout master
for i in 1 2 3
do
	generate_a_git_commit master
done

# Initialize SQL database
export PGPASSWORD=postgres
export PGHOST=localhost
psql -U postgres -w -f $HOME/Workspace/enterovirus/test/$testcasename/$testcasename-config.sql

export PGPASSWORD=$password
export PGHOST=localhost
psql -U $username -d $dbname -w -f $HOME/Workspace/enterovirus/database/initiate_database.sql
psql -U $username -d $dbname -w -f $HOME/Workspace/enterovirus/test/$testcasename/$testcasename-data.sql
