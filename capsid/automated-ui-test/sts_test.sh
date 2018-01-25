echo "\n"
echo "Reset/Initialize the database"
echo "============================="
cd /home/beta/Workspace/enterovirus/database
sh setup.sh

echo "\n"
echo "Reset/Initialize the git storage"
echo "================================"
cd /home/beta/Workspace/enterovirus-test/fake_server
rm -rf *
cd /home/beta/Workspace/enterovirus-test/fake_server/.ssh
rm -rf *
touch authorized_keys

echo "\n"
echo "Reset/Initialize fake client"
echo "============================"
cd /home/beta/Workspace/enterovirus-test/fake_client
rm -rf *

echo "\n"
echo "Run automatic UI test"
echo "====================="
cd /home/beta/Workspace/enterovirus/capsid/automated-ui-test

# UI automatic test.
# URL without the "/" at the end of it
#
# TODO:
# This will write the .ssh/autorized_keys to a fake position.
# But it doesn't matter, since "git clone" is by local protocol.
python3 ui_init.py http://localhost:8888

# "git clone" by local protocol
#sh git_init.sh /home/beta/Workspace/enterovirus-test/fake_server/org1/repo1.git
