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

data "aws_iam_policy" "AmazonEC2FullAccess" {
  arn = "arn:aws:iam::aws:policy/AmazonEC2FullAccess"
}

resource "aws_iam_group" "terraform" {
  name = "terraform"
}

resource "aws_iam_group_policy_attachment" "terraform-ec2_attach" {
  group = "${aws_iam_group.terraform.id}"
  policy_arn = "${data.aws_iam_policy.AmazonEC2FullAccess.arn}"
}

resource "aws_iam_user" "terraform" {
  name = "terraform"
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
