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
sh setup.sh


