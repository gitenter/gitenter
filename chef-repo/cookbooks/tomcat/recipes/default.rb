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

apt_package 'iptables-persistent' do
  action :install
end

include_recipe 'iptables::default'

iptables_rule 'http_8080' do
  lines '-I PREROUTING -p tcp --dport 80 -j REDIRECT --to-port 8080'
  table :nat
end

# Not idempotent but no longer needed
# execute "netfilter-persistent save"
# execute "netfilter-persistent reload"

node.default['tomcat']['proxyPort'] = "80"

template '/var/lib/tomcat8/conf/server.xml' do
  source 'server.xml.erb'
end

service "tomcat8" do
  action :restart
end
