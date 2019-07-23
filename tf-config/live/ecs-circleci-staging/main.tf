terraform {
  required_version = "> 0.11.12"
}

provider "aws" {
  access_key = "${var.aws_access_key}"
  secret_key = "${var.aws_secret_key}"
  region     = "${var.aws_region}"
  version = "~> 1.35"
}

module "ecs_circleci" {
  source = "../../modules/ecs-circleci"
  environment = "staging"
}
