#!/bin/sh

# This is the SSH forced command file which finally stays at `/home/git`.
#
# $SSH_ORIGINAL_COMMAND saves the original command. It has value:
# > ssh git@localhost uname -a ====> uname -a
# > ssh git@localhost git-receive-pack /home/git/org1/repo1.git ====> git-receive-pack /home/git/org1/repo1.git
#
# It is quite tricky that the naive "git clone" doesn't work.
# > git clone ssh://git@localhost:/home/git/org1/repo1.git
# > git clone ssh://git@localhost:org1/repo1.git
# It replies error "fatal: protocol error: bad line length character: XXX"
# where XXX is the first three characters of the first echo of this script.
# The following link gives clues what is happening.
# https://stackoverflow.com/questions/8170436/git-remote-error-fatal-protocol-error-bad-line-length-character-unab

# Could not "echo" to shell, as git protocol doesn't allow other reply rather than
# the pure git protocol. It cannot really display this on the screen,
# but reply with 
# > fatal: protocol error: bad line length character: the 
# > fatal: ''/home/git/org1/repo1.git'' does not appear to be a git repository
echo "the original SSH_ORIGINAL_COMMAND environment varible has value:" >> /tmp/stdout.txt
echo $SSH_ORIGINAL_COMMAND >> /tmp/stdout.txt # git-upload-pack 'org1/repo1.git'
#echo $SSH_ORIGINAL_COMMAND | tr -d "'" >> /tmp/stdout.txt

# This one currently also doesn't work. It seems accidentally makes double quote??
#
# $ git clone ssh://git@localhost:/home/git/org1/repo1.git
# or
# $ git clone ssh://git@localhost/org1/repo1.git
# or
# $ git clone git@localhost:org1/repo1.git
# > Cloning into 'repo1'...
# > fatal: ''/home/git/org1/repo1.git'' does not appear to be a git repository
# > fatal: Could not read from remote repository.
# >
# > Please make sure you have the correct access rights
# > and the repository exists.
tr_command=$(echo $SSH_ORIGINAL_COMMAND | tr -d "'")
echo "hello" >> /tmp/stdout.txt
echo $tr_command >> /tmp/stdout.txt
$tr_command

# This one is currently working.
# > ssh git@localhost git-receive-pack /home/git/org1/repo1.git
