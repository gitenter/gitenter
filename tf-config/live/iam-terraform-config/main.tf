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

variable username {
  type = "string"
  default = "terraform-config"
}

# Create the necessary roles needed to assign to AWS resources (EC2/ECS/...)
module "iam_roles" {
  source = "../../modules/iam-terraform-roles-setup"
}

# TODO:
# Right now there are 10 policies in this group. In case we need more, AWS will
# raise an error:
# > aws_iam_group_policy_attachment.efs: Error attaching policy
# > arn:aws:iam::aws:policy/AmazonElasticFileSystemFullAccess to IAM group
# > terraform-config: LimitExceeded: Cannot exceed quota for PoliciesPerGroup: 10
#
# Need a split to multiple policies to add to this role to bypass this problem.
module "iam_config_group" {
  source = "../../modules/iam-terraform-config-group"
}

# TODO:
# Create a series of users from list variable. One for each deployment environment,
# e.g. `terraform-user-local`, `terraform-user-circleci`, ... Straightforward, can follow:
# https://medium.com/devopslinks/aws-iam-user-and-policy-creation-using-terraform-7cd781e06c97
# However, it seems not that easy to create `aws_iam_access_key` for that
# series: `aws_iam_access_key.terraform: user must be a single value, not a list`
resource "aws_iam_user" "main" {
  name = "${var.username}"
}

resource "aws_iam_access_key" "main" {
  user = "${aws_iam_user.main.name}"
}

resource "aws_iam_group_membership" "stateless" {
  name = "${module.iam_config_group.stateless_group_name}-membership"
  group = "${module.iam_config_group.stateless_group_name}"

  users = [
    "${aws_iam_user.main.name}",
  ]
}

resource "aws_iam_group_membership" "storage" {
  name = "${module.iam_config_group.storage_group_name}-membership"
  group = "${module.iam_config_group.storage_group_name}"

  users = [
    "${aws_iam_user.main.name}",
  ]
}

resource "aws_iam_group_membership" "accessary" {
  name = "${module.iam_config_group.accessary_group_name}-membership"
  group = "${module.iam_config_group.accessary_group_name}"

  users = [
    "${aws_iam_user.main.name}",
  ]
}
