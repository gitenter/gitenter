Fork from https://github.com/CircleCI-Public/circleci-demo-aws-ecs-ecr

This Terraform config is a pipeline pre-setup which needs to executed locally. Then by using this setup, the `aws-ecr` orb upload/push the docker image, and `aws-ecs` pull the docker images out and deploy it. Deployment itself is basically just [`aws ecs update-service`](https://docs.aws.amazon.com/cli/latest/reference/ecs/update-service.html) (even for blue/green deployment) so no need for it to know the Terraform setup.

For the orbs to work, we need to modify environment variables in https://circleci.com/gh/gitenter/gitenter/edit#env-vars using the user created by `iam-terraform-deploy`.

TODO: Right now `aws-ecs` orb is using `iam-terraform-deploy` IAM user. It should be able to be much much simpler than the current setup (which is basically an extension of `iam-terraform-config`).

### CloudFormation Notes

- `"Ref":` = `!Ref`
- `Fn::` (JSON).
    - `Fn::GetAtt` = `!GetAtt`
    - `Fn::Join` = `!Join`
- Parameters

Parameter types can be (1) String, (2) Number, or (3) an AWS-specific type. Actually in CloudFormation, the String/number will be an input field (and the default is filling in in case applicable). Can provide default value (or pesudo-default values such as `AWS::Region`). The AWS-specific types will be a search/dropdown list so the input can always be valid.
