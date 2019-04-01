# TODO:
# Seems a module for `terraform` and `provider` doesn't work.
terraform {
  required_version = "> 0.11.12"
}

provider "aws" {
  access_key = "${var.access_key}"
  secret_key = "${var.secret_key}"
  region = "us-east-1"
}

variable group_name {
  type = "string"
  default = "terraform-deploy"
}

variable username {
  type = "string"
  default = "terraform-deploy"
}

module "iam_group" {
  source = "../../modules/iam-group-terraform-deploy"
  group_name = "${var.group_name}"
}

# TODO:
# Create a series of users from list variable. One for each deployment environment,
# e.g. `terraform-user-local`, `terraform-user-circleci`, ... Straightforward, can follow:
# https://medium.com/devopslinks/aws-iam-user-and-policy-creation-using-terraform-7cd781e06c97
# However, it seems not that easy to create `aws_iam_access_key` for that
# series: `aws_iam_access_key.terraform: user must be a single value, not a list`
module "iam_user" {
  source = "../../modules/iam-user"
  group_name = "${var.group_name}"
  username = "${var.username}"
}
