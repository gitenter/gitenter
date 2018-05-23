# After change the tomcat user to be "git",
# tomcat sometimes (not sure yet) cannot do auto-deployment. 
# So we need to manually
# rm /var/lib/tomcat8/webapps/capsid-0.0.2-prototype.war
# rm -rf /var/lib/tomcat8/webapps/capsid-0.0.2-prototype
# before copy the war in.

echo "\n"
echo "Reset/Initialize the database"
echo "============================="
cd $HOME/Workspace/enterovirus/database
sh setup.sh

echo "\n"
echo "Reset/Initialize the git storage"
echo "================================"
# As the SSH force commands need to stay,
# for the home folder, remove subfolders, but keep the files. 
sudo -H -u git bash -c 'rm -rf /home/git/*/'
sudo -H -u git bash -c 'rm -rf /home/git/.ssh/*'
sudo -H -u git bash -c 'touch /home/git/.ssh/authorized_keys' 
sudo -H -u git bash -c 'chmod 600 /home/git/.ssh/authorized_keys' 

echo "\n"
echo "Reset/Initialize fake client"
echo "============================"
mkdir $HOME/Workspace/enterovirus-test/fake_client
cd $HOME/Workspace/enterovirus-test/fake_client
rm -rf *

echo "\n"
echo "Run automatic UI test"
echo "====================="
cd $HOME/Workspace/enterovirus/capsid/automated-ui-test

# UI automatic test.
# URL without the "/" at the end of it
python3 ui_init.py http://localhost:8080/capsid-0.0.2-prototype

# Git automatic test
# "git clone" by SSH protocol
sh git_init_repo1.sh git@localhost:org1/repo1.git
sh git_init_repo2.sh git@localhost:org1/repo2.git
