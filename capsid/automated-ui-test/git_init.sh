cd /home/beta/Workspace/enterovirus-dummy/client
# Setup git through git local protocol
git clone /home/beta/Workspace/enterovirus-dummy/server/org1/repo1.git
cd repo1
git remote -v

cat > requirement-1.md <<- EOM
# Requirement-1

- [requirement-1-tag-0001] requirement-1-0001-content
- [requirement-1-tag-0002] requirement-1-0002-content
- [requirement-1-tag-0003] requirement-1-0003-content
EOM
cat > requirement-2.md <<- EOM
# Requirement-2

- [requirement-2-tag-0001] requirement-2-0001-content
- [requirement-2-tag-0002]{requirement-1-tag-0001} requirement-2-0002-content
- [requirement-2-tag-0003]{requirement-1-tag-0001,requirement-1-tag-0002} requirement-2-0003-content
- [requirement-2-tag-0004]{requirement-2-tag-0001} requirement-2-0004-content
EOM

git add -A
git commit -m "Add two requirement files"
git push origin master

