cd /home/beta/Workspace/enterovirus-dummy/client
# Setup git through git local protocol
git clone /home/beta/Workspace/enterovirus-dummy/server/org1/repo1.git
cd repo1
git remote -v

touch test-add-a-file
git add -A
git commit -m "test add a file"
git push origin master

touch test-add-another-file
git add -A
git commit -m "test add another file"
git push origin master

