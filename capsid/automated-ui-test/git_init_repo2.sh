gitorigin=$1

clientroot=$HOME/Workspace/enterovirus-test/fake_client
cd $clientroot

mkdir repo2
cd repo2
git init
git remote add origin $1

cd $clientroot/repo2
mkdir doc
cat > $clientroot/repo2/doc/test.md <<- EOM
# Test

- [tag] content
EOM

git add -A
git commit -m "add a document file but with no setup file"
git push origin master

cat > $clientroot/repo2/gitenter.properties <<- EOM
# ---------------------------
# GitEnter configuration file
# ---------------------------

# Value on/off. Set it as off will overwrite all further setups.
enable_systemwide = on

# Paths one in each line:
# > include_paths = folder1
# > include_paths = folder2
# > include_paths = folder3
#
# Or paths have comma in between:
# > include_paths = folder1,folder2,folder3
#
# If nothing is specified, then all files are  included.
include_paths = doc
EOM

git add -A
git commit -m "add setup file"
git push origin master
