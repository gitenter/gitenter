sudo apt-get install postgresql postgresql-contrib
sudo -u postgres psql postgres
\password postgres

cd /tmp/database
sh setup.sh

sudo apt-get install tomcat8
sudo chmod 777 /var/lib/tomcat8/webapps
rm -rf /var/lib/tomcat8/webapps/ROOT

#sudo sed -i "s/    <Connector port=\"8080\" protocol=\"HTTP\/1.1\"/    <Connector port=\"80\" protocol=\"HTTP\/1.1\"/g" /var/lib/tomcat8/conf/server.xml
#sudo service tomcat8 restart
