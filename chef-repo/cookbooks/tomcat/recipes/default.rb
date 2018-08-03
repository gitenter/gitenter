#
# Cookbook:: tomcat
# Recipe:: default
#
# Copyright:: 2018, The Authors, All Rights Reserved.

apt_update 'all platforms' do
  frequency 86400
  action :periodic
end

# Then http://192.168.33.7:8080/ works.
apt_package 'tomcat8' do
  action :install
end

include_recipe 'tomcat::port_redirect'

# TODO: Destroy and re-converge the kitchen to see if this file
# can be successfully deleted. (not sure whether we need to specify
# owner:group or not...)
directory '/var/lib/tomcat8/webapps/ROOT' do
  # owner 'tomcat8'
  # group 'tomcat8'
  recursive true
  action :delete
end

# This is a local dev solution. In production we should use remote_file
# to download the war from a web URL.
cookbook_file '/var/lib/tomcat8/webapps/ROOT.war' do
  source 'envelope-0.0.2-prototype.war'
  owner 'tomcat8'
  group 'tomcat8'
  mode '0755'
  action :create
end

service "tomcat8" do
  action :restart
end
