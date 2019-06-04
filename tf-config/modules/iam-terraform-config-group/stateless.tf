resource "aws_iam_group" "stateless" {
  name = "${local.stateless_group_name}"
}

data "aws_iam_policy" "ec2" {
  arn = "arn:aws:iam::aws:policy/AmazonEC2FullAccess"
}

resource "aws_iam_group_policy_attachment" "ec2" {
  group = "${aws_iam_group.stateless.id}"
  policy_arn = "${data.aws_iam_policy.ec2.arn}"
}

# Policy suggested:
# https://docs.aws.amazon.com/AmazonECS/latest/developerguide/ECS_GetStarted.html
# https://docs.aws.amazon.com/AmazonECS/latest/developerguide/ECS_GetStarted_EC2.html
#
# TODO:
# `AmazonECS_FullAccess` fully covers `AmazonEC2ContainerServiceRole`, and it was
# suggested that for EC2 launch type, `AmazonEC2ContainerServiceRole` is enough.
# However, actually then it will fail `aws_ecs_task_definition` by lack of
# `ecs:RegisterTaskDefinition` and fail `aws_ecs_cluster` by lack of `ecs:DescribeClusters`.
data "aws_iam_policy" "ecs" {
  arn = "arn:aws:iam::aws:policy/AmazonECS_FullAccess"
}

resource "aws_iam_group_policy_attachment" "ecs" {
  group = "${aws_iam_group.stateless.id}"
  policy_arn = "${data.aws_iam_policy.ecs.arn}"
}

resource "aws_iam_policy" "ecs_service_linked" {
  name        = "AWSServiceRoleForECSServiceLinkedPolicy"
  path        = "/"

  # This is the service-linked role automatically created by `aws_ecs_service`
  # https://docs.aws.amazon.com/IAM/latest/UserGuide/using-service-linked-roles.html
  # `AWSServiceRoleForECS` service role will be created automatically when we use
  # `awsvpn` network mode, so there's no need to create it by ourselves.
  #
  # We don't have `data "aws_iam_role" "ecs_service"` because the role may not
  # exist yet.`
  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "iam:CreateServiceLinkedRole"
            ],
            "Resource": "arn:aws:iam::*:role/aws-service-role/ecs.amazonaws.com/AWSServiceRoleForECS*",
            "Condition": {"StringLike": {"iam:AWSServiceName": "ecs.amazonaws.com"}}
        }
    ]
}
EOF
}

resource "aws_iam_group_policy_attachment" "ecs_service_linked_attach" {
  group = "${aws_iam_group.stateless.id}"
  policy_arn = "${aws_iam_policy.ecs_service_linked.arn}"
}

# Policy suggested:
# https://docs.aws.amazon.com/AmazonECS/latest/developerguide/get-set-up-for-amazon-ecs.html
# https://docs.aws.amazon.com/AmazonECS/latest/developerguide/ecs_managed_policies.html
data "aws_iam_policy" "ecs_instance" {
  arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"
}

resource "aws_iam_group_policy_attachment" "ecs_instance_attach" {
  group = "${aws_iam_group.stateless.id}"
  policy_arn = "${data.aws_iam_policy.ecs_instance.arn}"
}

# This is used to get and pass service roles to AWS resources (EC2, ECS, ...)
# basically all roles defined in `modules/iam-terraform-roles.setup`.
resource "aws_iam_policy" "role_linked" {
  name        = "AmazonEC2ContainerServiceforEC2RoleLinkedPolicy"
  path        = "/"

  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "iam:GetRole",
                "iam:PassRole"
            ],
            "Resource": "*"
        }
    ]
}
EOF
}

resource "aws_iam_group_policy_attachment" "role_linked_attach" {
  group = "${aws_iam_group.stateless.id}"
  policy_arn = "${aws_iam_policy.role_linked.arn}"
}

data "aws_iam_policy" "ecs_task_execution" {
  arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_group_policy_attachment" "ecs_task_execution_attach" {
  group = "${aws_iam_group.stateless.id}"
  policy_arn = "${data.aws_iam_policy.ecs_task_execution.arn}"
}

resource "aws_iam_policy" "instance_profile" {
  name        = "AmazonIAMInstanceProfilePolicy"
  path        = "/"

  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "iam:CreateInstanceProfile",
                "iam:GetInstanceProfile",
                "iam:RemoveRoleFromInstanceProfile",
                "iam:DeleteInstanceProfile",
                "iam:AddRoleToInstanceProfile"
            ],
            "Resource": "*"
        }
    ]
}
EOF
}

resource "aws_iam_group_policy_attachment" "instance_profile_attach" {
  group = "${aws_iam_group.stateless.id}"
  policy_arn = "${aws_iam_policy.instance_profile.arn}"
}
