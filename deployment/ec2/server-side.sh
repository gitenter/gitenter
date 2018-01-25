############################### Install Packages ##############################

sudo apt-get install postgresql postgresql-contrib
sudo -u postgres psql postgres
\password postgres

sudo apt-get install tomcat8

sudo apt-get install git # It seems already installed by default.

################################# Environment #################################

# Add user "git"

sudo adduser git --disabled-password

# Make user "git" accessable by SSH, Then "ssh git@52.41.66.37" works.
#
# Note: That need the home folder of git has right access (the default one works).
# drwxr-xr-x  6 git   git   4096 Jan 25 12:11 git 
#
# Refer to:
# https://aws.amazon.com/premiumsupport/knowledge-center/new-user-accounts-linux-instance/

sudo su - git
mkdir .ssh
chmod 700 .ssh
touch .ssh/authorized_keys
chmod 600 .ssh/authorized_keys
#echo -e "[my id_rsa.pub]" >> ~/.ssh/authorized_keys 
exit

# Setup tomcat to be run under user "git" rather than "tomcat", so both the
# UI (tomcat) and the git server (work on /home/git) can access the same folder
# structure.
#
# The other possibility is to "chmod 775 /home/git" and 
# "chown git:enterovirus" the corresponding folders. 
# > sudo groupadd enterovirus
# > getent group # List all groups with corresponding information
# > sudo usermod -a -G enterovirus tomcat8 # Add users to the enterovirus group.
# > sudo usermod -a -G enterovirus git
# > groups tomcat8
# > groups git
# That's not working, because:
# (1) To make ssh work we need ".ssh/authorized_keys", but then tomcat
# cannot write to it.
# (2) We need to "chmod" and "chown" all the corresponding folders and files
# made by Java. It is extremely painful to let Java work with OS, as it is 
# supposed to be on JVM.
# 
# The third dirty solution is to let all "/home/git" folders to be with user
# tomcat8, or simply let the data to be saved at "/home/tomcat8". That makes
# the "ssh" and "git clone" commands weird, so maybe not a good solution.
#
# Setup methods refer to:
# https://askubuntu.com/questions/371809/run-tomcat7-as-tomcat7-or-any-other-user
# One problem of this solution is "apt-get update" may break the setups.
# However, since we are gonna deploy in some OS other than Debian/ubuntu,
# right now we will just keep it like this.

sudo service tomcat8 stop
sudo sed -i "s/TOMCAT8_USER=tomcat8/TOMCAT8_USER=git/g" /etc/default/tomcat8
sudo sed -i "s/TOMCAT8_GROUP=tomcat8/TOMCAT8_GROUP=git/g" /etc/default/tomcat8
sudo chown -R git:adm /var/log/tomcat8 # originally tomcat8:adm
sudo chown -R git:git /var/lib/tomcat8/webapps # originally tomcat8:tomcat8
sudo chown -R git:git /var/lib/tomcat8/lib # originally tomcat8:tomcat8
#sudo chown newuser /etc/authbind/byport/80 # Maybe needed, check later. Not appliable locally.
#sudo chown newuser /etc/authbind/byport/443
sudo chown git:adm /var/cache/tomcat8 # originally tomcat:adm
sudo chown -R git:git /var/cache/tomcat8/Catalina # originally tomcat8:tomcat8
sudo chown -R :git /var/lib/tomcat8/conf/* # originally :tomcat8
sudo service tomcat8 start

# Setup the port of tomcat server to be 80 rather than 8080.

sudo /sbin/iptables -t nat -I PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080
sudo apt-get install iptables-persistent
sudo netfilter-persistent save
sudo netfilter-persistent reload
sudo sed -i "s/    <Connector port=\"8080\" protocol=\"HTTP\/1.1\"/    <Connector port=\"8080\" proxyPort=\"80\" protocol=\"HTTP\/1.1\"/g" /var/lib/tomcat8/conf/server.xml

######################### Preparation for deployment ##########################

# Done before "scp":
#
# So then copy the .war file to ROOT.war will let the site stay at
# the root of the URL.

sudo chmod 777 /var/lib/tomcat8/webapps
rm -rf /var/lib/tomcat8/webapps/ROOT

# Done after "scp":
#
# Initialize the database

cd /tmp/database
sh setup.sh
