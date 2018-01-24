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

# To make the new added user accessable by ssh, follow the
# link of: https://aws.amazon.com/premiumsupport/knowledge-center/new-user-accounts-linux-instance/

sudo adduser git --disabled-password
sudo su - git
mkdir .ssh
chmod 700 .ssh
touch .ssh/authorized_keys
chmod 600 .ssh/authorized_keys
#echo -e "[my id_rsa.pub]" >> ~/.ssh/authorized_keys 
#Then "ssh git@52.41.66.37" works. But I seems cannot change the authorization of its root.

# chmod and chown of the "/home/git" folder and its subfolders, so
# both user "tomcat8" (from the UI) and user "git" (from git server)
# can work on it.
#
# It is quite dirty, since you also need to change everything writen 
# to in "capsid" ("mkdir" of creating new repository, and "git init").
#
# The other possibility (maybe neater) is to let tomcat to run under
# the user git, but I can't make it succeed.
#
#sudo sed -i "s/TOMCAT8_USER=tomcat8/TOMCAT8_USER=git/g" /etc/default/tomcat8
#sudo sed -i "s/TOMCAT8_GROUP=tomcat8/TOMCAT8_GROUP=git/g" /etc/default/tomcat8
#sudo server tomcat8 restart
# > Job for tomcat8.service failed because the control process exited with error code. See "systemctl status tomcat8.service" and "journalctl -xe" for details.
# $ systemctl status tomcat8.service
# ● tomcat8.service - LSB: Start Tomcat.
#    Loaded: loaded (/etc/init.d/tomcat8; bad; vendor preset: enabled)
#    Active: failed (Result: exit-code) since Wed 2018-01-24 13:59:38 EST; 38s ago
#      Docs: man:systemd-sysv-generator(8)
#   Process: 5673 ExecStop=/etc/init.d/tomcat8 stop (code=exited, status=0/SUCCESS
#   Process: 5682 ExecStart=/etc/init.d/tomcat8 start (code=exited, status=1/FAILU
#     Tasks: 35
#    Memory: 666.2M
#       CPU: 200ms
#    CGroup: /system.slice/tomcat8.service
#            └─23168 /usr/lib/jvm/default-java/bin/java -Djava.util.logging.config
# 
# Jan 24 13:59:33 landslide systemd[1]: Stopped LSB: Start Tomcat..
# Jan 24 13:59:33 landslide systemd[1]: Starting LSB: Start Tomcat....
# Jan 24 13:59:33 landslide tomcat8[5682]:  * Starting Tomcat servlet engine tomca
# Jan 24 13:59:38 landslide tomcat8[5682]:    ...fail!
# Jan 24 13:59:38 landslide systemd[1]: tomcat8.service: Control process exited, c
# Jan 24 13:59:38 landslide systemd[1]: Failed to start LSB: Start Tomcat..
# Jan 24 13:59:38 landslide systemd[1]: tomcat8.service: Unit entered failed state
# Jan 24 13:59:38 landslide systemd[1]: tomcat8.service: Failed with result 'exit-
# ...skipping...


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
