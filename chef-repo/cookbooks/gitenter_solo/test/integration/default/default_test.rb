# # encoding: utf-8

# Inspec test for recipe gitenter_solo::default

describe directory('/var/log/tomcat8') do
  it { should exist }
  its('owner') { should eq 'git' }
  its('group') { should eq 'adm' }
end

# TODO:
# Seems we cannot recursively check in InSpec??
# Iteration get nothing in loop...
# Dir.glob('/var/log/tomcat8/**/*') do |path|
#   next if path == '.' or path == '..'
#   describe file(path) do
#     its('owner') { should eq 'git' }
#     its('group') { should eq 'adm' }
#   end if File.file?(path)
#   describe directory(path) do
#     its('owner') { should eq 'git' }
#     its('group') { should eq 'adm' }
#   end if File.directory?(path)
# end

describe file('/var/log/tomcat8/catalina.out') do
  it { should exist }
  its('owner') { should eq 'git' }
  its('group') { should eq 'adm' }
end

describe directory('/var/lib/tomcat8/webapps') do
  it { should exist }
  its('owner') { should eq 'git' }
  its('group') { should eq 'git' }
end

describe directory('/var/lib/tomcat8/lib') do
  it { should exist }
  its('owner') { should eq 'git' }
  its('group') { should eq 'git' }
end

describe directory('/var/cache/tomcat8') do
  it { should exist }
  its('owner') { should eq 'git' }
  its('group') { should eq 'adm' }
end

describe directory('/var/cache/tomcat8/Catalina') do
  it { should exist }
  its('owner') { should eq 'git' }
  its('group') { should eq 'adm' }
end

describe directory('/var/cache/tomcat8/Catalina/localhost') do
  it { should exist }
  its('owner') { should eq 'git' }
  its('group') { should eq 'adm' }
end

# Need to login and manually run the following command:
# (It is NOT idempotent, and shouldN'T be run everytime when
# there are changes on the chef recipe.)
#
# $ export PGPASSWORD=zooo
# $ export PGHOST=localhost
# $ psql -U gitenter -d gitenter -w -f /tmp/initiate_database.sql
