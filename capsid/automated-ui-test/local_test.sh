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
cd /home/git/.ssh
sudo rm -rf *

echo "\n"
echo "Reset/Initialize fake client"
echo "============================"
cd /home/beta/Workspace/enterovirus-test/fake_client
rm -rf *

echo "\n"
echo "Run automatic UI test"
echo "====================="
cd /home/beta/Workspace/enterovirus/capsid/automated-ui-test

# without the "/" at the end of it
python3 ui_init.py http://localhost:8080/capsid-0.0.1-prototype
#python3 ui_init.py http://localhost:8888
sh git_init.sh localhost
