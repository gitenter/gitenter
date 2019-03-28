terraform {
  required_version = "> 0.11.12"
}

provider "aws" {
  access_key = "${var.access_key}"
  secret_key = "${var.secret_key}"
  region = "us-east-1"
}

resource "aws_key_pair" "terraform-seashore" {
  key_name   = "terraform-key_pair-seashore"
  public_key = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQC6vmegnYSA36MiMa/miDe/qQQSAFT6o2pWgoz07gDozY/6eSLVWJ8j3BtfC/ykAQJ5yp1J9TZSsE0VX+MWZtZpm4stGOnQqiTNWwKoS3bfTnubopW9eF+Fk7kNy6gZWhf7bqU7gSk+497vx7kBgwjDRUB82AovAG/aGtxl2CATZzh3ylh5OhHXjUtv+1gWPZZcDkADLjtvNtCKiDGBV5ERiy7hPk70scmVLtakFUqhhwmw1cV3wtLsK5egttffZLxXJVC6A1RG1ysb5p11SnUny5hTYl0ZpoM+ZfQeupM0HGKdAXJXPqKQ6Przn3BG7e8DDdwOlUQ+8VXwclvmmppr seashore"
}

resource "aws_vpc" "ecs" {
  cidr_block       = "10.0.0.0/16"
  assign_generated_ipv6_cidr_block = false
  # instance_tenancy = "dedicated"
  enable_dns_support = true
  enable_dns_hostnames = true

  tags = {
    Name = "ecs-vpc"
  }
}

resource "aws_security_group" "terraform-ecs" {
  name = "ecs-instances-default-cluster"

  # Inbound and outbound rules matches what is suggested by the development guide
  # and followed by the AWS console UI.
  # https://docs.aws.amazon.com/AmazonECS/latest/developerguide/get-set-up-for-amazon-ecs.html

  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # HTTP
  ingress {
    from_port = 80
    to_port = 80
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"] # May not be needed as the associated VPC is without an IPv6 CIDR block
  }

  # HTTPS
  ingress {
    from_port = 443
    to_port = 443
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }

  # SSH
  ingress {
    from_port = 22
    to_port = 22
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # TODO: `Custom IP` rather than `Anywhere`
    ipv6_cidr_blocks = ["::/0"]
  }

  vpc_id = "${aws_vpc.ecs.id}"
}

resource "aws_ecr_repository" "terraform-ecs" {
  name = "terraform-ecs-repository"
}

# TODO:
# Push images into ECR repository is through docker command `docker tag` and `docker push`
# in an environment with that docker image. It is a non-idempotent operation through command
# line. Not a duty through Terraform.
# `aws_ecr_repository.terraform-ecs.repository_url` is needed for this option.
#
# Consider create the image through an Amazon Linux machine, guided by
# https://docs.aws.amazon.com/AmazonECS/latest/developerguide/docker-basics.html
