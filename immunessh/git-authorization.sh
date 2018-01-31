#!/bin/sh

# This is the SSH forced command file which finally stays at `/home/git`.

# $SSH_ORIGINAL_COMMAND saves the original command. It has value:
#
# > $ ssh git@localhost uname -a
# > uname -a
#
# > $ ssh git@localhost git-receive-pack /home/git/org1/repo1.git
# > git-receive-pack /home/git/org1/repo1.git
#  
# > $ git clone ssh://git@localhost:/home/git/org1/repo1.git
# > $ git clone ssh://git@localhost/org1/repo1.git
# > $ git clone git@localhost:org1/repo1.git
# > git-upload-pack 'org/repo1.git'
#
# NOTE:
#
# We cannot "echo $SSH_ORIGINAL_COMMAND" to the shell, as the git
# protocol needs the first fouor bytes to be the line length.
# If so, we'll get the error message (where XXX is the first three 
# characters of the first echo of this script):
#
# > It replies error "fatal: protocol error: bad line length character: XXX"
# > fatal: ''/home/git/org1/repo1.git'' does not appear to be a git repository
#
# Refer to this link for more details:
# https://stackoverflow.com/questions/8170436/git-remote-error-fatal-protocol-error-bad-line-length-character-unab
echo $SSH_ORIGINAL_COMMAND >> /tmp/stdout.txt

# By some weird reasons, for git commands the string saved to 
# $SSH_ORIGINAL_COMMAND have extra quotation marks for the 
# filepath. So if we execcute $SSH_ORIGINAL_COMMAND naively, 
# it will give back erro message:
#
# > $ git clone git@localhost:org1/repo1.git
# > Cloning into 'repo1'...
# > fatal: ''/home/git/org1/repo1.git'' does not appear to be a git repository
# > fatal: Could not read from remote repository.
# >
# > Please make sure you have the correct access rights
# > and the repository exists.
#
# So to execute it, we need to manually remove the quotation
# marks.
tr_command=$(echo $SSH_ORIGINAL_COMMAND | tr -d "'")
echo $tr_command >> /tmp/stdout.txt
$tr_command

# TODO:
# Add username as the input parameter of the forced command.
# In the forced command, run the java script to check whether
# the user has the authorization to edit the repository.
