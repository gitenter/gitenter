terraform {
  required_version = "> 0.11.12"
}

variable "access_key" {}
variable "secret_key" {}

provider "aws" {
  access_key = "${var.access_key}"
  secret_key = "${var.secret_key}"
  region = "us-east-1"
}

resource "aws_iam_group" "terraform" {
  name = "terraform"
}

# TODO:
# Create a series of users from list variable. Straightforward, can follow e.g.
# https://medium.com/devopslinks/aws-iam-user-and-policy-creation-using-terraform-7cd781e06c97
# However, it seems not that easy to create `aws_iam_access_key` for that
# series: `aws_iam_access_key.terraform: user must be a single value, not a list`
resource "aws_iam_user" "terraform" {
  name = "terraform"
}

data "aws_iam_policy" "terraform-ec2" {
  arn = "arn:aws:iam::aws:policy/AmazonEC2FullAccess"
}

resource "aws_iam_group_policy_attachment" "terraform-ec2" {
  group = "${aws_iam_group.terraform.id}"
  policy_arn = "${data.aws_iam_policy.terraform-ec2.arn}"
}

# Multiple roles are added for ECS propose:
# - `AmazonEC2ContainerServiceforEC2Role`
# - `AmazonEC2ContainerServiceRole`
# - `AmazonECSTaskExecutionRolePolicy`
# https://docs.aws.amazon.com/AmazonECS/latest/developerguide/get-set-up-for-amazon-ecs.html
data "aws_iam_policy" "terraform-ecs_container_instance" {
  arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"
}

resource "aws_iam_group_policy_attachment" "terraform-ecs_container_instance" {
  group = "${aws_iam_group.terraform.id}"
  policy_arn = "${data.aws_iam_policy.terraform-ecs_container_instance.arn}"
}

# May be fully covered by `AmazonEC2FullAccess` but still add it in here.
data "aws_iam_policy" "terraform-ecs" {
  arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceRole"
}

resource "aws_iam_group_policy_attachment" "terraform-ecs" {
  group = "${aws_iam_group.terraform.id}"
  policy_arn = "${data.aws_iam_policy.terraform-ecs.arn}"
}

data "aws_iam_policy" "terraform-ecs_fargate" {
  arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_group_policy_attachment" "terraform-ecs_fargate" {
  group = "${aws_iam_group.terraform.id}"
  policy_arn = "${data.aws_iam_policy.terraform-ecs_fargate.arn}"
}

resource "aws_iam_access_key" "terraform" {
  user = "${aws_iam_user.terraform.name}"
}

resource "aws_iam_group_membership" "terraform" {
  name = "terraform"

  users = [
    "${aws_iam_user.terraform.name}",
  ]

  group = "${aws_iam_group.terraform.name}"
}
