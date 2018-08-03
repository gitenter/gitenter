#
# Cookbook:: postgres_init
# Recipe:: default
#
# Copyright:: 2018, The Authors, All Rights Reserved.

apt_update 'all platforms' do
  frequency 86400
  action :periodic
end

# Consider using "postgresql" cookbook from Chef Supermarket.
# However, that one only includes resources so not sure exactly
# how to use.

# apt_package 'postgresql' do
#   action :install
# end
#
# apt_package 'postgresql-contrib' do
#   action :install
# end

# include_recipe 'postgresql::default'

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

postgresql_database 'gitenter' do
  # user 'gitenter'
  # owner 'gitenter'
  user 'postgres'
  owner 'postgres'
  action :create
end
