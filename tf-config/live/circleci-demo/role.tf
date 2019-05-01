locals {
  # The name of the execution role to be created
  aws_ecs_execution_role_name = "${var.aws_resource_prefix}-ecs-execution-role"
}

# This is an IAM role which authorizes ECS to manage resources on your
# account on your behalf, such as updating your load balancer with the
# details of where your containers are, so that traffic can reach your
# containers.
resource "aws_iam_role" "ecs" {
  name = "ecs-role"
  path               = "/"
  assume_role_policy = "${data.aws_iam_policy_document.ecs.json}"
}

data "aws_iam_policy_document" "ecs" {
  statement {
    actions = ["sts:AssumeRole"]
    effect  = "Allow"

    principals {
      type        = "Service"
      identifiers = ["ecs.amazonaws.com"]
    }
  }
}

resource "aws_iam_policy" "ecs_service" {
  name        = "ecs-service"
  path        = "/"

  # `ec2:` parts:
  # Rules which allow ECS to attach network interfaces to instances
  # on your behalf in order for awsvpc networking mode to work right
  #
  # `elasticloadbalancing:` parts:
  # Rules which allow ECS to update load balancers on your behalf
  # with the information sabout how to send traffic to your containers
  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "ec2:AttachNetworkInterface",
        "ec2:CreateNetworkInterface",
        "ec2:CreateNetworkInterfacePermission",
        "ec2:DeleteNetworkInterface",
        "ec2:DeleteNetworkInterfacePermission",
        "ec2:Describe*",
        "ec2:DetachNetworkInterface",
        "elasticloadbalancing:DeregisterInstancesFromLoadBalancer",
        "elasticloadbalancing:DeregisterTargets",
        "elasticloadbalancing:Describe*",
        "elasticloadbalancing:RegisterInstancesWithLoadBalancer",
        "elasticloadbalancing:RegisterTargets"
      ],
      "Resource": "*",
      "Effect": "Allow"
    }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "ecs_attach" {
  role       = "${aws_iam_role.ecs.name}"
  policy_arn = "${aws_iam_policy.ecs_service.arn}"
}

# TODO:
# Write role in Terraform rather than CloudFormation
resource "aws_cloudformation_stack" "role" {
  name = "${var.aws_role_stack_name}"
  template_body = "${file("role-cloudformation-template.yml")}"
  capabilities = ["CAPABILITY_NAMED_IAM"]
  parameters {
    ExecutionRoleName = "${local.aws_ecs_execution_role_name}"
    # `ExecutionRoleName` are defined as parameters of `role-cloudformation-template.yml`.
  }
}
