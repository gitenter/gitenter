#
# Cookbook:: gitenter_solo
# Recipe:: default
#
# Copyright:: 2018, The Authors, All Rights Reserved.

include_recipe 'git_server::default'
include_recipe 'postgresql_init::default'
include_recipe 'tomcat_init::default'

# TODO:
# Should we use the tomcat cookbook from Chef Supermarket so it can specify the
# user who's running tomcat? Therefore all these changes are unnecessary.

service "tomcat8" do
  action :stop
end

git_username = node.default['git_server']['username']

template '/etc/default/tomcat8' do
  source 'tomcat8.erb'
  variables ({
    :tomcat_user => git_username,
    :tomcat_group => git_username
  })
end

directory '/var/log/tomcat8' do
  owner git_username
  group 'adm'
  recursive True
  action :nothing
end

directory '/var/lib/tomcat8/webapps' do
  owner git_username
  group git_username
  recursive True
  action :nothing
end

directory '/var/lib/tomcat8/lib' do
  owner git_username
  group git_username
  recursive True
  action :nothing
end

directory '/var/cache/tomcat8' do
  owner git_username
  group 'adm'
  recursive True
  action :nothing
end

directory '/var/cache/tomcat8/Catalina' do
  owner git_username
  group 'adm'
  recursive True
  action :nothing
end

Dir.foreach('/var/lib/tomcat8/conf') do |item|
  next if item == '.' or item == '..'
  if File.directory?(item)
    directory item do
      group 'adm'
      recursive True
      action :nothing
    end
  elsif File.file?("name")
    file item do
      group 'adm'
      action :nothing
    end
  # do work on real items
end

service "tomcat8" do
  action :start
end

cookbook_file '/var/lib/tomcat8/webapps/ROOT.war' do
  source 'envelope-0.0.2-prototype.war'
  mode '0755'
  force_unlink true
  manage_symlink_source false
  action :create
end
