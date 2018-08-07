apt_package 'openssh-server' do
  action :install
end

template '/etc/ssh/sshd_config' do
  source 'sshd_config.erb'
  variables ({
    :password_authentication => 'no'
  })
  mode "0644"
end

# TODO:
# Try to use openssh cookbook to set it up. Most people set it
# up on node, but I need to do it by a cross-cookbook override
# of attributes. It doesn't work right now.
#
# node.force_default['openssh']['server']['password_authentication'] = 'no'
# include_recipe 'openssh::default'
