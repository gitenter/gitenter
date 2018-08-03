#
# Cookbook:: git
# Recipe:: default
#
# Copyright:: 2018, The Authors, All Rights Reserved.

apt_update 'all platforms' do
  frequency 86400
  action :periodic
end

include_recipe 'git::openssh-server'
include_recipe 'git::git-server'
