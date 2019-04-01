variable "access_key" {}
variable "secret_key" {}

variable "aws_region" {
  default = "us-east-1"
}

terraform {
  required_version = "> 0.11.12"
}

provider "aws" {
  access_key = "${var.access_key}"
  secret_key = "${var.secret_key}"
  region = "${var.aws_region}"
}
