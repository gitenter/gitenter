# Below ECR names are used in CircleCI orbs
resource "aws_ecr_repository" "web_app" {
  name = "${local.web_app_resource_name}"
}

resource "aws_ecr_repository" "web_static" {
  name = "${local.web_static_resource_name}"
}

resource "aws_ecr_repository" "git" {
  name = "${local.git_resource_name}"
}
