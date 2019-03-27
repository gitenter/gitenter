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

resource "aws_vpc" "terraform" {
  cidr_block       = "10.0.0.0/16"
  assign_generated_ipv6_cidr_block = false
  # instance_tenancy = "dedicated"
  enable_dns_support = true
  enable_dns_hostnames = true

  tags = {
    Name = "terraform-vpc"
  }
}

resource "aws_security_group" "tomcat" {
  name = "tomcat"

  ingress {
    from_port = "${var.server_port}"
    to_port = "${var.server_port}"
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  vpc_id = "${aws_vpc.terraform.id}"
}
