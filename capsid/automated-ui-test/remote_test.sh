# Shall first setup the server
#cd /home/beta/Workspace/enterovirus/deployment/ec2
#sh scp.sh
#sh ssh.sh
#cd /tmp/
#sh reset.sh

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
python3 ui_init.py http://52.41.66.37
sh git_init.sh 52.41.66.37