resource "aws_ecr_repository" "web_app" {
  name = "${local.aws_web_app_ecr_name}"
}

resource "aws_ecr_repository" "web_static" {
  name = "${local.aws_web_static_ecr_name}"
}

resource "aws_ecr_repository" "git" {
  name = "${local.aws_git_ecr_name}"
}
