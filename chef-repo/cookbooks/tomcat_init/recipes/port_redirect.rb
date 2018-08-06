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
