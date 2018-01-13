testcasename=user_auth

# Initialize SQL database
. $HOME/Workspace/enterovirus/test/library/sql-init.sh || exit 1
sql_init_customized_data $testcasename
