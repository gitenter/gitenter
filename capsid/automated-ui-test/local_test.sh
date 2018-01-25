echo "\n"
echo "Reset/Initialize the database"
echo "============================="
cd /home/beta/Workspace/enterovirus/database
sh setup.sh

echo "\n"
echo "Reset/Initialize the git storage"
echo "================================"
sudo -H -u git bash -c 'rm -rf /home/git/*' 
sudo -H -u git bash -c 'rm -rf /home/git/.ssh/*'
sudo -H -u git bash -c 'touch /home/git/.ssh/authorized_keys' 
sudo -H -u git bash -c 'chmod 600 /home/git/.ssh/authorized_keys' 

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
