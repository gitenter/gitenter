# Policy suggested:
# https://docs.aws.amazon.com/AmazonECS/latest/developerguide/get-set-up-for-amazon-ecs.html
# https://docs.aws.amazon.com/AmazonECS/latest/developerguide/ecs_managed_policies.html
data "aws_iam_policy" "ecs_instance" {
  arn = "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role"
}

# Used for `aws_iam_instance_profile` to create ECS instances.
# Role suggested:
# https://docs.aws.amazon.com/AmazonECS/latest/developerguide/ECS_instances.html#container_instance_concepts
# https://docs.aws.amazon.com/AmazonECS/latest/developerguide/instance_IAM_role.html
resource "aws_iam_role" "ecs_instance" {
  name                = "AmazonEC2ContainerServiceforEC2Role"
  path                = "/"
  assume_role_policy  = "${data.aws_iam_policy_document.ecs_instance.json}"
}

data "aws_iam_policy_document" "ecs_instance" {
  statement {
    actions = ["sts:AssumeRole"]

    principals {
      type        = "Service"
      identifiers = ["ec2.amazonaws.com"]
    }
  }
}

resource "aws_iam_role_policy_attachment" "ecs_instance_attach" {
    role       = "${aws_iam_role.ecs_instance.name}"
    policy_arn = "${data.aws_iam_policy.ecs_instance.arn}"
}

data "aws_iam_policy" "ecs_task_execution" {
  arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

# This role is referred by `aws_ecs_task_definition` resource.
resource "aws_iam_role" "ecs_task_execution" {
  name               = "AmazonECSTaskExecutionRole"
  path               = "/"
  assume_role_policy = "${data.aws_iam_policy_document.ecs_task_execution.json}"
}

data "aws_iam_policy_document" "ecs_task_execution" {
  statement {
    actions = ["sts:AssumeRole"]
    effect  = "Allow"

    principals {
      type        = "Service"
      identifiers = ["ecs-tasks.amazonaws.com"]
    }
  }
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution_attach" {
  role       = "${aws_iam_role.ecs_task_execution.name}"
  policy_arn = "${data.aws_iam_policy.ecs_task_execution.arn}"
}

# https://learn.hashicorp.com/terraform/aws/eks-intro
# https://docs.aws.amazon.com/eks/latest/userguide/getting-started-console.html#role-create
resource "aws_iam_role" "eks_service" {
  name = "serviceRoleForEKS"

  assume_role_policy = <<POLICY
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "eks.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
POLICY
}

resource "aws_iam_role_policy_attachment" "eks_cluster_policy_attach" {
  role       = "${aws_iam_role.eks_service.name}"
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSClusterPolicy"
}

resource "aws_iam_role_policy_attachment" "els_service_policy_attach" {
  role       = "${aws_iam_role.eks_service.name}"
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSServicePolicy"
}

# https://docs.aws.amazon.com/eks/latest/userguide/getting-started-console.html#eks-launch-workers
# https://amazon-eks.s3-us-west-2.amazonaws.com/cloudformation/2019-10-08/amazon-eks-nodegroup.yaml
resource "aws_iam_role" "eks_node_instance" {
  name = "serviceRoleForEKSNodeInstanceForEC2"

  assume_role_policy = <<POLICY
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "ec2.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
POLICY
}

resource "aws_iam_role_policy_attachment" "eks_instance_AmazonEKSWorkerNodePolicy_attach" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSWorkerNodePolicy"
  role       = "${aws_iam_role.eks_node_instance.name}"
}

resource "aws_iam_role_policy_attachment" "eks_instance_AmazonEKS_CNI_Policy_attach" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKS_CNI_Policy"
  role       = "${aws_iam_role.eks_node_instance.name}"
}

resource "aws_iam_role_policy_attachment" "eks_instance_AmazonEC2ContainerRegistryReadOnly_attach" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly"
  role       = "${aws_iam_role.eks_node_instance.name}"
}
