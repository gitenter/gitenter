#################################### Server ###################################

sudo apt-get install tomcat8
sudo chmod 777 /var/lib/tomcat8/webapps
rm -rf /var/lib/tomcat8/webapps/ROOT # So then copy the .war file to ROOT.war will let the site stay at the root of the URL.

# Setup the port of tomcat server to be 80 rather than 8080.
sudo /sbin/iptables -t nat -I PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080
sudo apt-get install iptables-persistent
sudo netfilter-persistent save
sudo netfilter-persistent reload
sudo sed -i "s/    <Connector port=\"8080\" protocol=\"HTTP\/1.1\"/    <Connector port=\"8080\" proxyPort=\"80\" protocol=\"HTTP\/1.1\"/g" /var/lib/tomcat8/conf/server.xml

################################### Database ##################################

sudo apt-get install postgresql postgresql-contrib
sudo -u postgres psql postgres
\password postgres

cd /tmp/database
sh setup.sh

###################################### Git ####################################

sudo apt-get install git # It seems already installed by default.

adduser git # Current set password "git".

sudo groupadd enterovirus
#getent group # List all groups with corresponding information

sudo usermod -a -G enterovirus tomcat8 # Add users to the enterovirus group.
sudo usermod -a -G enterovirus git
#groups tomcat8
#groups git
sudo chown -R git:enterovirus /home/git # Change the group of "/home/git" to enterovirus
sudo chmod 775 /home/git
sudo service tomcat8 restart # When adding a user to a new group, that won't be applied in any currently-running processes, only new ones. So one need to logout and login again to make the group changes.

sudo mkdir /home/git/.ssh/
sudo chown -R git:enterovirus /home/git/.ssh
sudo chmod 775 /home/git/.ssh
