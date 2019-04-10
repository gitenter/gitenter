# https://github.com/gruntwork-io/terratest/blob/master/examples/terraform-ssh-example/main.tf
#
# Connected by (note the username is `ubuntu`):
# ssh ubuntu@ec2-54-173-132-206.compute-1.amazonaws.com

terraform {
  required_version = "> 0.11.12"
}

variable "access_key" {}
variable "secret_key" {}

provider "aws" {
  access_key = "${var.access_key}"
  secret_key = "${var.secret_key}"
  region = "us-east-1"
}

resource "aws_security_group" "main" {
  name = "terraform-test"

  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # SSH
  ingress {
    from_port = 22
    to_port = 22
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # TODO: `Custom IP` rather than `Anywhere`
    ipv6_cidr_blocks = ["::/0"]
  }
}

data "aws_ami" "ubuntu" {
  most_recent = true

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-bionic-18.04-amd64-server-*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }

  owners = ["099720109477"] # Canonical
}

resource "aws_key_pair" "terraform-seashore" {
  key_name   = "terraform-key_pair-seashore"
  public_key = "${file("~/.ssh/id_rsa.pub")}"
}

resource "aws_instance" "main" {
  ami           = "${data.aws_ami.ubuntu.id}"
  instance_type = "t2.micro"
  associate_public_ip_address = true
  key_name = "${aws_key_pair.terraform-seashore.key_name}"
  vpc_security_group_ids = ["${aws_security_group.main.id}"]

  tags = {
    Name = "terraform-test"
  }
}

output "public_dns" {
  value = "${aws_instance.main.public_dns}"
}
