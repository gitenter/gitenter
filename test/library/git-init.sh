#!/bin/sh

git_init_one_repo () {

	testcasename=$1

	# Clean up
	rm -rf $HOME/Workspace/enterovirus-test/$testcasename/
	cd $HOME/Workspace/enterovirus-test/
	mkdir $testcasename
	cd $testcasename
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

}

