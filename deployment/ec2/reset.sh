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
