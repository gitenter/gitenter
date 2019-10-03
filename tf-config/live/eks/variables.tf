variable "access_key" {}
variable "secret_key" {}
variable "aws_region" {
  default = "us-east-1"
  description = "AWS region e.g. us-east-1 (Please specify a region supported by the Fargate launch type)"
}

variable "az_count" {
  default = "2"
}

locals {
  aws_resource_prefix = "eks"
  aws_vpc_name = "${local.aws_resource_prefix}-vpc"
  aws_eks_cluster_security_group = "${local.aws_resource_prefix}-eks-sg"
  aws_eks_node_security_group = "${local.aws_resource_prefix}-eks-node-sg"

  aws_launch_configuration_name = "${local.aws_resource_prefix}-launch-configuration"
  aws_autoscaling_group_name = "${local.aws_resource_prefix}-asg"

  aws_eks_cluster_name = "${local.aws_resource_prefix}-cluster"
  aws_eks_instance_profile_name = "${local.aws_resource_prefix}-eks-instance-profile"
}
