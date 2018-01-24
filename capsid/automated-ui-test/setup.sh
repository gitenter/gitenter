echo "\n"
echo "Reset/Initialize the database"
echo "============================="
cd /home/beta/Workspace/enterovirus/database
sh setup.sh

echo "\n"
echo "Reset/Initialize the git storage"
echo "================================"
cd /home/git
sudo rm -rf *

cd /home/beta/Workspace/enterovirus-test/fake-client
rm -rf *

cd /home/beta/Workspace/enterovirus/capsid/automated-ui-test
python3 ui_init.py
sh git_init.sh
