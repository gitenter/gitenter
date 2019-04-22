locals {
  # The name of the execution role to be created
  aws_ecs_execution_role_name = "${var.aws_resource_prefix}-ecs-execution-role"
}

# TODO:
# Write role in Terraform rather than CloudFormation
resource "aws_cloudformation_stack" "role" {
  name = "${var.aws_role_stack_name}"
  template_body = "${file("role-cloudformation-template.yml")}"
  capabilities = ["CAPABILITY_NAMED_IAM"]
  parameters {
    ExecutionRoleName = "${local.aws_ecs_execution_role_name}"
    # `ExecutionRoleName` are defined as parameters of `role-cloudformation-template.yml`.
  }
}
