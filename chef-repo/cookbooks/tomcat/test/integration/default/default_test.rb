# # encoding: utf-8

# Inspec test for recipe tomcat::default

describe package('tomcat8') do
  it { should be_installed }
end

describe package('iptables-persistent') do
  it { should be_installed }
end

# Port redirection is defined for outside usage. From inside it is still in port 8080.
describe command("curl http://localhost:8080") do
  its('stdout') { should match /Tomcat/ }
end
