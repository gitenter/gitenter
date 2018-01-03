cd /home/beta/Workspace/enterovirus/database
sh setup.sh

cd /home/beta/Workspace/enterovirus-dummy/server
rm -rf *

cd /home/beta/Workspace/enterovirus-dummy/client
rm -rf *

# Should execute when the capsid site is on.
cd /home/beta/Workspace/enterovirus/capsid/automated-ui-test
sh setup.sh


