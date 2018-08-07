#
# Cookbook:: git_server
# Recipe:: default
#
# Copyright:: 2018, The Authors, All Rights Reserved.

apt_update 'all platforms' do
  frequency 86400
  action :periodic
end

include_recipe 'git_server::openssh_server_setup'
include_recipe 'git_server::git_server_setup'
