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
