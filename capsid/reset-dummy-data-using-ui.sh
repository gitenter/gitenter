cd /home/beta/Workspace/enterovirus/database
sh setup.sh

cd /home/beta/Workspace/enterovirus/capsid/src/main/resources/sql
sh setup.sh

cd /home/beta/Workspace/enterovirus/capsid/dummy_resources
rm -rf *

cd /home/beta/Workspace/enterovirus/capsid/dummy-results
rm -rf *

# Should execute when the capsid site is on.
cd /home/beta/Workspace/enterovirus/capsid/src/test/resources/junit_init
python3 init.py

cd /home/beta/Workspace/enterovirus/capsid/dummy_resources
# Setup git through local protocol
git clone /home/beta/Workspace/enterovirus/capsid/dummy-results/org1/repo1.git
cd repo1
git remote -v
touch test-add-a-file
git add -A
git commit -m "test add file"
git push origin master

