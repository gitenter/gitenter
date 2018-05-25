# # encoding: utf-8

# Inspec test for recipe tomcat::default

# The Inspec reference, with examples and extensive documentation, can be
# found at http://inspec.io/docs/reference/resources/

describe package('tomcat8') do
  it { should be_installed }
end

describe command("curl http://localhost:8080") do
  its('stdout') { should match /Tomcat/ }
end
