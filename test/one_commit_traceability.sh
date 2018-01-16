testcasename=one_commit_traceability

# Initialize git
. $HOME/Workspace/enterovirus/test/library/git-init.sh || exit 1
git_init_single_repo $testcasename

# Fake a commit with two document files with traceable items inside
cd $gitclientfilepath
cat > enterovirus.properties <<- EOM
# ------------------------------
# Enterovirus configuration file
# ------------------------------

enable_systemwide = on
include_paths = requirement
EOM

cat > requirement/R1.md <<- EOM
# Requirement-1

- [R1T1] Content of R1T1.
- [R1T2] Content of R1T2.
- [R1T3] Content of R1T3.
EOM

cat > requirement/R2.md <<- EOM
# Requirement-2

- [R2T1] Content of R2T1.
- [R2T2]{R1T1} Content of R2T2.
- [R2T3]{R1T1,R1T2} Content of R2T3.
- [R2T4]{R2T1} Content of R2T4.
- [R2T5]{R1T1,R2T1} Content of R2T5
EOM

cat > design/D1.md <<- EOM
# Design-1

- [D1T1]{R1T1,R2T1} Content of D1T1
- [D2T2] Content of D2T2.
EOM

git add -A
git commit -m "Commit with traceability relationship in between."
git push origin master

export commit_id=$(git log -1 --pretty="%H")
echo $commit_id >> $rootfilepath/commit-sha-list.txt

# Initialize SQL database
. $HOME/Workspace/enterovirus/test/library/sql-init.sh || exit 1
sql_init_single_repo $testcasename
