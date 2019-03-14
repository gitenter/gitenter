terraform {
  required_version = "> 0.11.12"
}

variable "access_key" {}
variable "secret_key" {}

variable "server_port" {
  description = "The port the server will use for HTTP requests"
  default = 8080
}

provider "aws" {
  access_key = "${var.access_key}"
  secret_key = "${var.secret_key}"
  region = "us-east-1"
}

resource "aws_security_group" "web_access" {
  name = "capsid-web-access"
  ingress {
    from_port = "${var.server_port}"
    to_port = "${var.server_port}"
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_instance" "capsid" {
  ami = "ami-0a313d6098716f372" # Ubuntu Server 18.04 LTS
  instance_type = "t2.micro"
  vpc_security_group_ids = ["${aws_security_group.web_access.id}"]

  user_data = <<-EOF
                #!/bin/bash
                echo "Hello, Capsid" > index.html
                nohup busybox httpd -f -p "${var.server_port}" &
                EOF

  tags {
    Name = "capsid"
  }
}

output "public_ip" {
  value = "${aws_instance.capsid.public_ip}"
}
