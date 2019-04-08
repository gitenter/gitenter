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
