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

# resource "aws_iam_group" "terraform_deploy" {
#   name = "terraform_deploy"
# }

resource "aws_iam_user" "terraform_deploy" {
  name = "terraform_deploy"
}

resource "aws_iam_access_key" "terraform_deploy" {
  user = "${aws_iam_user.terraform_deploy.name}"
}

resource "aws_iam_user_policy" "terraform_deploy_ro" {
  user = "${aws_iam_user.terraform_deploy.name}"

  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Action": "ec2:*",
            "Effect": "Allow",
            "Resource": "*"
        },
        {
            "Effect": "Allow",
            "Action": "elasticloadbalancing:*",
            "Resource": "*"
        },
        {
            "Effect": "Allow",
            "Action": "cloudwatch:*",
            "Resource": "*"
        },
        {
            "Effect": "Allow",
            "Action": "autoscaling:*",
            "Resource": "*"
        },
        {
            "Effect": "Allow",
            "Action": "iam:CreateServiceLinkedRole",
            "Resource": "*",
            "Condition": {
                "StringEquals": {
                    "iam:AWSServiceName": [
                        "autoscaling.amazonaws.com",
                        "ec2scheduled.amazonaws.com",
                        "elasticloadbalancing.amazonaws.com",
                        "spot.amazonaws.com",
                        "spotfleet.amazonaws.com",
                        "transitgateway.amazonaws.com"
                    ]
                }
            }
        }
    ]
}
EOF
}

output "access_key" {
  value = "${aws_iam_access_key.terraform_deploy.id}"
  description = "access_key for user ${aws_iam_user.terraform_deploy.name}."
}

output "secret_key" {
  value = "${aws_iam_access_key.terraform_deploy.secret}"
  description = "secret_key for user ${aws_iam_user.terraform_deploy.name}."
}
