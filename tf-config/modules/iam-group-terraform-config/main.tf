resource "aws_iam_group" "terraform" {
  name = "${var.group_name}"
}

# This is for defining service-linked roles for ECS.
# https://docs.aws.amazon.com/IAM/latest/UserGuide/using-service-linked-roles.html
# Concrete example why we need to use it:
# https://github.com/CircleCI-Public/circleci-demo-aws-ecs-ecr/blob/019bc8804587727f2c67bf8535b36794d541593f/terraform_setup/cloudformation-templates/public-vpc.yml#L191-L257
# TODO:
# Wonder if it is possible to reduce it from `IAMFullAccess` to a smaller set.
data "aws_iam_policy" "terraform-iam" {
  arn = "arn:aws:iam::aws:policy/IAMFullAccess"
}

resource "aws_iam_group_policy_attachment" "terraform-iam" {
  group = "${aws_iam_group.terraform.id}"
  policy_arn = "${data.aws_iam_policy.terraform-iam.arn}"
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
# https://docs.aws.amazon.com/AmazonECS/latest/developerguide/ecs_managed_policies.html
data "aws_iam_policy" "terraform-ecs_container_instance" {
  arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"
}

resource "aws_iam_group_policy_attachment" "terraform-ecs_container_instance" {
  group = "${aws_iam_group.terraform.id}"
  policy_arn = "${data.aws_iam_policy.terraform-ecs_container_instance.arn}"
}

# `AmazonECS_FullAccess` fully covers `AmazonEC2ContainerServiceRole`,
# and the following link suggest to use full access.
# https://docs.aws.amazon.com/AmazonECS/latest/developerguide/ECS_GetStarted.html
# If using EC2 launch type, then `AmazonEC2ContainerServiceRole` is enough.
# https://docs.aws.amazon.com/AmazonECS/latest/developerguide/ECS_GetStarted_EC2.html
data "aws_iam_policy" "terraform-ecs" {
  arn = "arn:aws:iam::aws:policy/AmazonECS_FullAccess"
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

resource "aws_iam_policy" "terraform-ecr" {
  name        = "AmazonECRConfigPolicy"
  path        = "/"

  # https://docs.aws.amazon.com/AmazonECR/latest/userguide/RepositoryPolicyExamples.html
  # `ecr:CreateRepository` and `ecr:ListTagsForResource` is used by `resource "aws_ecr_repository"`
  # `ecr:DescribeRepositories` is used by `data "aws_ecr_repository"`
  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "DenyPull",
            "Effect": "Allow",
            "Action": [
                "ecr:CreateRepository",
                "ecr:DeleteRepository",
                "ecr:ListTagsForResource",
                "ecr:DescribeRepositories"
            ],
            "Resource": "*"
        }
    ]
}
EOF
}

resource "aws_iam_group_policy_attachment" "terraform-ecr" {
  group = "${aws_iam_group.terraform.id}"
  policy_arn = "${aws_iam_policy.terraform-ecr.arn}"
}

data "aws_iam_policy" "terraform-rds" {
  arn = "arn:aws:iam::aws:policy/AmazonRDSFullAccess"
}

resource "aws_iam_group_policy_attachment" "terraform-rds" {
  group = "${aws_iam_group.terraform.id}"
  policy_arn = "${data.aws_iam_policy.terraform-rds.arn}"
}
