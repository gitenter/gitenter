# Overview

Every Chef installation needs a Chef Repository. This is the place where cookbooks, roles, config files and other artifacts for managing systems with Chef will live. We strongly recommend storing this repository in a version control system such as Git and treat it like source code.

While we prefer Git, and make this repository available via GitHub, you are welcome to download a tar or zip archive and use your favorite version control system to manage the code.

# Repository Directories

This repository contains several directories, and each directory contains a README file that describes what it is for in greater detail, and how to use it for managing your systems with Chef.

- `cookbooks/` - Cookbooks you download or create.
- `data_bags/` - Store data bags and items in .json in the repository.
- `roles/` - Store roles in .rb or .json in the repository.
- `environments/` - Store environments in .rb or .json in the repository.

# Configuration

The config file, `.chef/knife.rb` is a repository specific configuration file for knife. If you're using the Chef Platform, you can download one for your organization from the management console. If you're using the Open Source Chef Server, you can generate a new one with `knife configure`. For more information about configuring Knife, see the Knife documentation.

<https://docs.chef.io/knife.html>

# Next Steps

Read the README file in each of the subdirectories for more information about what goes in those directories.

## installation

[ChefDK](https://downloads.chef.io/chefdk) downloaded and installed. Then I have:

```
$ chef --version
Chef Development Kit Version: 3.0.36
chef-client version: 14.1.12
delivery version: master (7206afaf4cf29a17d2144bb39c55b7212cfafcc7)
berks version: 7.0.2
kitchen version: 1.21.2
inspec version: 2.1.72
```

Separately install `gem install chef-zero` (run `.kitchen.yml` with `chef_zero` setup doesn't need it to be installed; however, `chef-zero --port [1234]` need this separate installation). May need to update Ruby before doing that.

## Test

First need to setup `.chef` folder, with `knife.rb` and associated (fake) `*.pem` files.

Under this folder, may run

`chef-zero --port 9501`

Then various `knife` commands are available under this folder.
