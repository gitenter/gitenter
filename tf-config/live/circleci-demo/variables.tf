variable "aws_access_key" {}
variable "aws_secret_key" {}
variable "aws_account_id" {}
variable "aws_region" {
  default = "us-east-1"
  description = "AWS region e.g. us-east-1 (Please specify a region supported by the Fargate launch type)"
}

variable "aws_resource_prefix" {
  default = "circleci-demo"
  description = "Prefix to be used in the naming of some of the created AWS resources e.g. demo-webapp"
}
variable "aws_vpc_stack_name" {
  # The name of the CloudFormation stack to be created for the VPC and related resources
  #
  # TODO:
  # Cannot use variables in default, as "default may not contain interpolations"
  # May use `locals` but this variable is used throughout different terraform modules.
  default = "circleci-demo-vpc-stack"
}
variable "aws_role_stack_name" {
  # The name of the CloudFormation stack to be created for the role
  default = "circleci-demo-role-stack"
}
