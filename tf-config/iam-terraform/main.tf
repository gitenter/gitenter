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
  name = "terraform-group"
}

# TODO:
# Create a series of users from list variable. Straightforward, can follow e.g.
# https://medium.com/devopslinks/aws-iam-user-and-policy-creation-using-terraform-7cd781e06c97
# However, it seems not that easy to create `aws_iam_access_key` for that
# series: `aws_iam_access_key.terraform: user must be a single value, not a list`
resource "aws_iam_user" "terraform" {
  name = "terraform-user"
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
  name = "terraform-group-membership"

  users = [
    "${aws_iam_user.terraform.name}",
  ]

  group = "${aws_iam_group.terraform.name}"
}

resource "aws_key_pair" "terraform-seashore" {
  key_name   = "terraform-key_pair-seashore"
  public_key = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQC6vmegnYSA36MiMa/miDe/qQQSAFT6o2pWgoz07gDozY/6eSLVWJ8j3BtfC/ykAQJ5yp1J9TZSsE0VX+MWZtZpm4stGOnQqiTNWwKoS3bfTnubopW9eF+Fk7kNy6gZWhf7bqU7gSk+497vx7kBgwjDRUB82AovAG/aGtxl2CATZzh3ylh5OhHXjUtv+1gWPZZcDkADLjtvNtCKiDGBV5ERiy7hPk70scmVLtakFUqhhwmw1cV3wtLsK5egttffZLxXJVC6A1RG1ysb5p11SnUny5hTYl0ZpoM+ZfQeupM0HGKdAXJXPqKQ6Przn3BG7e8DDdwOlUQ+8VXwclvmmppr seashore"
}
