#!/bin/sh

# Ensure that the current working directory is the
# directory of this script itself.
cd "$(dirname "$0")"

. $HOME/Workspace/enterovirus/test-setup/library/load_all.sh

get_testcase_name
