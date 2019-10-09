variable "environment" {
  description = "Prefix to distinguish different environments. E.g., `dev`, `test`, `staging`, `prod`."
}

variable "aws_region" {
  default = "us-east-1"
}

variable "az_count" {
  default = 2
}

locals {
  name_prefix = "${var.environment}-eks"
  main_resource_name = "${local.name_prefix}"

  # Below variables are cross-used in different Terraform file.
  eks_cluster_name = "${local.main_resource_name}"
}
