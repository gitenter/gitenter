This Terraform config is a pipeline pre-setup which needs to executed locally. Then the `aws-ecr` and `aws-ecs` orbs can use this setup to push docker images out. TODO: Extend the `iam-terraform-config` IAM user so it can work on this job.

Then the actual pushing part: Need to modify environment variables in https://circleci.com/gh/gitenter/gitenter/edit#env-vars TODO: this user should be `iam-terraform-deploy` but temperarily we are using `iam-terraform-config` now.

TODO: Customized `Dockerfile` path, as by default `circleci/aws-ecr/build_and_push_image` orb is using the `Dockerfile` in the root folder.
