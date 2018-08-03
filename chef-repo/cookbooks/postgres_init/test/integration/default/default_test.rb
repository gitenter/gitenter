# # encoding: utf-8

# Inspec test for recipe postgresql::default

# The Inspec reference, with examples and extensive documentation, can be
# found at http://inspec.io/docs/reference/resources/

describe package('postgresql') do
  it { should be_installed }
end

describe user('postgres') do
  it { should exist }
  its('home') { should eq '/var/lib/postgresql' }
end

describe command("psql --version") do
  its('stdout') { should match /10/ }
end

# Check database "gitenter" exists
# https://stackoverflow.com/questions/14549270/check-if-database-exists-in-postgresql-using-shell
describe command("sudo runuser -l postgres -c 'psql -lqt | cut -d \\| -f 1'") do
  its('stdout') { should match /gitenter/ }
end

# Check database "gitenter" is owned by ~"gitenter"~"postgres"
describe command("sudo runuser -l postgres -c 'psql -lqt | cut -d \\| -f 2'") do
  its('stdout') { should match /postgres/ }
  # its('stdout') { should match /gitenter/ }
end
