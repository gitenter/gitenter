#
# Cookbook:: postgres_init
# Recipe:: default
#
# Copyright:: 2018, The Authors, All Rights Reserved.

apt_update 'all platforms' do
  frequency 86400
  action :periodic
end

postgresql_server_install 'postgresql server install' do
  password 'postgres'
  port 5432
  action :install
end

postgresql_user 'gitenter' do
  password 'zooo'
  createdb true
  action :create
end

# "user" cannot be "gitenter" because there should be a corresponding system user.
# "owner" as "gitenter" can pass "kitchen converge" but with deprecated warnings,
# also if log into the machine and check, it's owner is still "postgres"
postgresql_database 'gitenter' do
  user 'postgres'
  owner 'gitenter'
  action :create
end

# TODO:
# Should replace with remote_file in the future, or the future chef server need
# to hold the entire project folder (rather than just chef-repo). Don't know which
# is the more reasonable way?
cookbook_file '/tmp/initiate_database.sql' do
  source 'initiate_database.sql'
  mode '0755'
  force_unlink true
  manage_symlink_source false
  action :create
end

# Need to login and manually run the following command:
# (It is NOT idempotent, and shouldN'T be run everytime when
# there are changes on the chef recipe.)
#
# $ export PGPASSWORD=zooo
# $ export PGHOST=localhost
# $ psql -U gitenter -d gitenter -w -f /tmp/initiate_database.sql
