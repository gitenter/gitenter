#
# Cookbook:: gitenter_solo
# Recipe:: default
#
# Copyright:: 2018, The Authors, All Rights Reserved.

include_recipe 'git_server::default'
include_recipe 'postgres_init::default'
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
end

# Recursive doesn't work. It is for create PARENTS rather
# than recursive children, and it only works with :create.
# And chef doesn't support "chown -R" because it is not
# idempotent.
# Ref:
# https://tickets.opscode.com/browse/CHEF-690
# https://tickets.opscode.com/browse/CHEF-1621
Dir.glob('/var/log/tomcat8/**/*') do |path|
  next if path == '.' or path == '..'
  file path do
    owner git_username
    group "adm"
  end if File.file?(path)
end

directory '/var/lib/tomcat8/webapps' do
  owner git_username
  group git_username
end

Dir.glob('/var/lib/tomcat8/webapps/**/*') do |path|
  next if path == '.' or path == '..'
  file path do
    owner git_username
    group git_username
  end if File.file?(path)
  directory path do
    owner git_username
    group git_username
  end if File.directory?(path)
end

directory '/var/lib/tomcat8/lib' do
  owner git_username
  group git_username
end

# glob will recursively do all nested subfolders.
Dir.glob('/var/cache/tomcat8/**/*') do |path|
  next if path == '..'
  file path do
    owner git_username
    group 'adm'
  end if File.file?(path)
  directory path do
    owner git_username
    group 'adm'
  end if File.directory?(path)
end

Dir.glob('/var/lib/tomcat8/conf/**/*') do |path|
  next if path == '.' or path == '..'
  file path do
    group git_username
  end if File.file?(path)
  directory path do
    group git_username
  end if File.directory?(path)
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
