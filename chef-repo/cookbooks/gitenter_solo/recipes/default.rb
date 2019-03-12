#
# Cookbook:: gitenter_solo
# Recipe:: default
#
# Copyright:: 2018, The Authors, All Rights Reserved.

# TODO:
# Right now rerun this cookbook will sometimes raises an
# error saying we can't 'git_server::default' to (re)create
# user "git", because
# > STDERR: usermod: user git is currently used by process 6384
#
# It should be tomcat, but simple "sudo service tomcat8 stop"
# will not solve the problem.

# TODO:
# In case that the server is in wrong state (owner has been changed
# by a part of the setups), "kitchen converge" while fail on the
# step of "sudo tomcat service restart" in "tomcat_init".

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
  action :create
end

# Recursive doesn't work. It is for create PARENTS rather
# than recursive children, and it only works with :create.
# And chef doesn't support "chown -R" because it is not
# idempotent.
# Ref:
# https://tickets.opscode.com/browse/CHEF-690
# https://tickets.opscode.com/browse/CHEF-1621
execute "chown -R git:adm /var/log/tomcat8"
# Dir.glob('/var/log/tomcat8/**/*') do |path|
#   print path
#   next if path == '.' or path == '..'
#   file path do
#     owner git_username
#     group "adm"
#   end if File.file?(path)
# end

directory '/var/lib/tomcat8/webapps' do
  owner git_username
  group git_username
end

execute "chown -R git:git /var/lib/tomcat8/webapps"
# Dir.glob('/var/lib/tomcat8/webapps/**/*') do |path|
#   next if path == '.' or path == '..'
#   file path do
#     owner git_username
#     group git_username
#   end if File.file?(path)
#   directory path do
#     owner git_username
#     group git_username
#   end if File.directory?(path)
# end

directory '/var/lib/tomcat8/lib' do
  owner git_username
  group git_username
end

execute "chown git:adm /var/cache/tomcat8"
execute "chown -R git:git /var/cache/tomcat8/Catalina"
# # glob will recursively do all nested subfolders.
# Dir.glob('/var/cache/tomcat8/**/*') do |path|
#   next if path == '..'
#   file path do
#     owner git_username
#     group 'adm'
#   end if File.file?(path)
#   directory path do
#     owner git_username
#     group 'adm'
#   end if File.directory?(path)
# end

execute "chown -R :git /var/lib/tomcat8/conf/*"
# Dir.glob('/var/lib/tomcat8/conf/**/*') do |path|
#   next if path == '.' or path == '..'
#   file path do
#     group git_username
#   end if File.file?(path)
#   directory path do
#     group git_username
#   end if File.directory?(path)
# end

service "tomcat8" do
  action :restart
end

cookbook_file '/var/lib/tomcat8/webapps/ROOT.war' do
  # source 'capsid-0.0.2-prototype.war'
  source 'trivial.war'
  mode '0755'
  force_unlink true
  manage_symlink_source false
  action :create
end
