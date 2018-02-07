echo "\n"
echo "Copy executable files"
echo "====================="

sudo -H -u git bash -c 'cp /tmp/git-authorization.sh /home/git'
sudo -H -u git bash -c 'chmod +x /home/git/git-authorization.sh'
sudo -H -u git bash -c 'cp /tmp/immunessh-0.0.1-prototype-jar-with-dependencies.jar /home/git'

sudo rm -rf /var/lib/tomcat8/webapps/ROOT.war
sudo -H -u git bash -c 'cp /tmp/ROOT.war /var/lib/tomcat8/webapps'
