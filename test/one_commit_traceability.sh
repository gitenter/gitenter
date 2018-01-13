testcasename=one_commit_traceability

# Initialize git
. $HOME/Workspace/enterovirus/test/library/git-init.sh || exit 1
git_init_single_repo $testcasename

# Fake a commit with two document files with traceable items inside
cd $gitclientfilepath
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
echo $commit_id >> $rootfilepath/commit-sha-list.txt

# Initialize SQL database
. $HOME/Workspace/enterovirus/test/library/sql-init.sh || exit 1
sql_init_single_repo $testcasename
