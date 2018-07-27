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
