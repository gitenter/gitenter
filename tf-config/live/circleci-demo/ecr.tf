locals {
  # The name of the ECR repository to be created
  aws_ecr_repository_name = "${var.aws_resource_prefix}"
}

resource "aws_ecr_repository" "app_repository" {
  name = "${local.aws_ecr_repository_name}"
}
