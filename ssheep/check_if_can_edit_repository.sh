#!/bin/bash

git-upload-pack /home/git/rrr/raa.git
exit 0

# This is the SSH forced command file which finally stays at `/ssheep/check_if_can_edit_repository.sh`.

# Exclude all commands which is not comes from a git protocol.
if [[ -z "$SSH_ORIGINAL_COMMAND" ]];
then
  echo "Sorry, SSH shell connection is not allowed for this service."
  exit 0
fi

if [[ $SSH_ORIGINAL_COMMAND != git* ]];
then
  echo "Sorry, command $SSH_ORIGINAL_COMMAND is not allowed for this service."
  exit 0
fi

# Git the first parameter, which is in ".ssh/authorized_keys"
# pre-saved by enterovirus.protease.database.SshKeyRepository.class

username=$1
echo "username: $username" >> /tmp/stdout.txt

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
# protocol needs the first four bytes to be the line length.
# If so, we'll get the error message (where XXXX is the first four
# characters of the first echo of this script):
#
# > It replies error "fatal: protocol error: bad line length character: XXXX"
# > fatal: ''/home/git/org1/repo1.git'' does not appear to be a git repository
#
# Refer to this link for more details:
# https://stackoverflow.com/questions/8170436/git-remote-error-fatal-protocol-error-bad-line-length-character-unab

echo "SSH_ORIGINAL_COMMAND: $SSH_ORIGINAL_COMMAND" >> /tmp/stdout.txt

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
echo "tr_command: $tr_command" >> /tmp/stdout.txt

# Need to use "bash" rather than "sh" when execute this script.
# Otherwise it gives error
# > Syntax error: redirection unexpected
# Refer to https://stackoverflow.com/questions/2462317/bash-syntax-error-redirection-unexpected

# TODO:
# Right now only work for `git clone ssh://git@localhost:8822/home/git/org/repo.git`
# Need to change to `-f1` and `-f2` if `git clone git@localhost:rrr/raa.git`
# Should have a compatible way of doing it.
original_arg=$(cut -d' ' -f2 <<< "$tr_command")
org_name=$(cut -d'/' -f4 <<< "$original_arg")
repo_full_name=$(cut -d'/' -f5 <<< "$original_arg")
repo_name=$(cut -d'.' -f1 <<< "$repo_full_name")
echo "org_name: $org_name" >> /tmp/stdout.txt
echo "repo_full_name: $repo_full_name" >> /tmp/stdout.txt
echo "repo_name: $repo_name" >> /tmp/stdout.txt

# Execute `check_if_can_edit_repository.py` and get the result. Based on
# the result, decide whether the $SSH_ORIGINAL_COMMAND should be executed
# or not.
#
# TODO:
#
# It is really tricky that when you use "git ..." (rather than
# "ssh ...", etc) to trigger this force command, it is already in
# git protocol, so you have no way to echo any customized message
# in the user shell. If you do any, they'll give error (XXXX is the
# first four characters of your message, see privious comments).
# > fatal: protocol error: bad line length character: XXXX
#
# But if nothing is done, it will give error code
# > fatal: Could not read from remote repository.
# >
# > Please make sure you have the correct access rights
# > and the repository exists.
# Which is also not clear to show the user what is happening.

cd /ssheep
output=$(python3 check_if_can_edit_repository.py $username $org_name $repo_name)
echo "output: $output" >> /tmp/stdout.txt
if [[ "$output" == "True" ]];
then
  echo "bingo" >> /tmp/stdout.txt
  $tr_command
#else
#	echo "User $username is not authorized to do git operations for repository $org_name/$repo_name."
fi
