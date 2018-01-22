sudo apt-get install postgresql postgresql-contrib
sudo -u postgres psql postgres
\password postgres

cd /tmp/database
sh setup.sh

sudo apt-get install tomcat8
sudo chmod 777 /var/lib/tomcat8/webapps
rm -rf /var/lib/tomcat8/webapps/ROOT

