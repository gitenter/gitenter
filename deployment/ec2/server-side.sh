sudo apt-get install git # It seems already installed by default.

sudo apt-get install postgresql postgresql-contrib
sudo -u postgres psql postgres
\password postgres

cd /tmp/database
sh setup.sh

sudo apt-get install tomcat8
sudo chmod 777 /var/lib/tomcat8/webapps
rm -rf /var/lib/tomcat8/webapps/ROOT

sudo /sbin/iptables -t nat -I PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080
sudo apt-get install iptables-persistent
sudo netfilter-persistent save
sudo netfilter-persistent reload
sudo sed -i "s/    <Connector port=\"8080\" protocol=\"HTTP\/1.1\"/    <Connector port=\"8080\" proxyPort=\"80\" protocol=\"HTTP\/1.1\"/g" /var/lib/tomcat8/conf/server.xml
