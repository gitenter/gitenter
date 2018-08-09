node.default['git_server']['username'] = 'git'
node.default['git_server']['home_dir'] = "/home/#{node.default['git_server']['username']}"

git_username = node.default['git_server']['username']
git_home_dir = node.default['git_server']['home_dir']

apt_package 'git' do
  action :install
end

user git_username do
  home git_home_dir
  shell '/bin/bash'
  #manage_home True
  action :create
end

directory git_home_dir do
  owner git_username
  group git_username
  mode '0755'
  action :create
end

# https://aws.amazon.com/premiumsupport/knowledge-center/new-user-accounts-linux-instance/
# Then "ssh git@52.41.66.37" works
directory "#{git_home_dir}/.ssh" do
  owner git_username
  group git_username
  mode '0700'
  action :create
end

file "#{git_home_dir}/.ssh/authorized_keys" do
  #content "ssh-rsa ..."
  mode '0600'
  owner git_username
  group git_username
end
