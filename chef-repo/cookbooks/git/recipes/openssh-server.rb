apt_package 'openssh-server' do
  action :install
end

node.default['openssh']['password_authentication'] = 'no'

template '/etc/ssh/sshd_config' do
  source 'sshd_config.erb'
  mode "0644"
end

# sudo systemctl restart sshd.service
