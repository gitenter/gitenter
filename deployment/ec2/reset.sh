echo "\n"
echo "Reset/Initialize the database"
echo "============================="
cd /tmp/database
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
sudo -H -u git bash -c 'cp /tmp/git-authorization.sh /home/git'
sudo -H -u git bash -c 'chmod +x /home/git/git-authorization.sh'
sudo -H -u git bash -c 'cp /tmp/immunessh-0.0.1-prototype-jar-with-dependencies.jar /home/git'

sudo rm -rf /var/lib/tomcat8/webapps/ROOT.war
sudo -H -u git bash -c 'cp /tmp/ROOT.war /var/lib/tomcat8/webapps'
