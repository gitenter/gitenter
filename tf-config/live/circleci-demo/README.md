This Terraform config is a pipeline pre-setup which needs to executed locally. Then the `aws-ecr` and `aws-ecs` orbs can use this setup to push docker images out. TODO: Extend the `iam-terraform-config` IAM user so it can work on this job.

Then the actual pushing part: Need to modify environment variables in https://circleci.com/gh/gitenter/gitenter/edit#env-vars Use the user created by `iam-terraform-deploy`.

TODO:

- Understand internet gateway, route, route table, ..., and understand the difference in between the `ecs-setup/network.tf` setup and the `cloudformation-template/public-vpc.yml` setup.
- Understand what is an AWS role. Why `cloudformation-template/public-vpc.yml` is first creating a role, and then `cloudformation-template/public-service.yml` is using it? What's the difference between it, or create a user with that permission and let that user to execute deploy of `cloudformation-template/public-service.yml`?
- Understand the reason why we need to have cloud formation inside of terraform. Looks like `cloudformation-template/public-vpc.yml` can be completely rewriting in Terraform. In `cloudformation-template/public-service.yml` the `ImageUrl` parameter is not defined in `.tf` nor have a default value in `public-service`, understand when/how it is passed in, and where that creating is executed (in orb execution)?
