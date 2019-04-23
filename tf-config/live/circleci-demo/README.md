Fork from https://github.com/CircleCI-Public/circleci-demo-aws-ecs-ecr

This Terraform config is a pipeline pre-setup which needs to executed locally. Then the `aws-ecr` and `aws-ecs` orbs can use this setup to push docker images out. TODO: Extend the `iam-terraform-config` IAM user so it can work on this job.

Then the actual pushing part: Need to modify environment variables in https://circleci.com/gh/gitenter/gitenter/edit#env-vars Use the user created by `iam-terraform-deploy`.

TODO:

- Understand internet gateway, route, route table, ..., and understand the difference in between the `ecs-setup/network.tf` setup and the `cloudformation-template/public-vpc.yml` setup.
- Understand what is an AWS role. Why `cloudformation-template/public-vpc.yml` is first creating a role, and then `cloudformation-template/public-service.yml` is using it? What's the difference between it, or create a user with that permission and let that user to execute deploy of `cloudformation-template/public-service.yml`?
- Understand the reason why we need to have cloud formation inside of terraform. Looks like `cloudformation-template/public-vpc.yml` can be completely rewriting in Terraform. In `cloudformation-template/public-service.yml` the `ImageUrl` parameter is not defined in `.tf` nor have a default value in `public-service`, understand when/how it is passed in, and where that creating is executed (in orb execution)?
- Understand what the orbs are doing. What is absolutely necessary in the demo to make the deployment pipeline works.

CloudFormation Notes:

- `"Ref":` = `!Ref`
- `Fn::` (JSON).
    - `Fn::GetAtt` = `!GetAtt`
    - `Fn::Join` = `!Join`
- Parameters

Parameter types can be (1) String, (2) Number, or (3) an AWS-specific type. Actually in CloudFormation, the String/number will be an input field (and the default is filling in in case applicable). Can provide default value (or pesudo-default values such as `AWS::Region`). The AWS-specific types will be a search/dropdown list so the input can always be valid.
