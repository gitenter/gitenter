# TODO:
# Separate Terraform folders per component ((1)Virtual Private Cloud (VPC)
# setup and (2) capsid service are different components).

terraform {
  required_version = "> 0.11.12"

  # TODO:
  # If deploy multiple copies, we should have have seperated backends.
  # Consider using a variable and pass it into the bucket(?)/key(?) name.
  # E.g. `"terraform/remote-state-storage/${env}"`
  # The final aim is per *.tfstate file per environment.
  # (Gruntwork suggests one folder per environment. That means duplicated
  # code? https://blog.gruntwork.io/how-to-manage-terraform-state-28f5697e68fa)
  # backend "s3" {
  #   bucket = "gitenter-config"
  #   key    = "terraform/remote-state-storage"
  #   region = "us-east-1"
  # }
}

# TODO:
# May seperate AWS accounts for testing environment (clean up deployment
# completely after test is done) and lnog-stay environment (prod/...).
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

resource "aws_security_group" "tomcat" {
  name = "tomcat"

  ingress {
    from_port = "${var.server_port}"
    to_port = "${var.server_port}"
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  lifecycle {
    create_before_destroy = true
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

resource "aws_launch_configuration" "capsid" {
  name = "capsid"
  image_id = "${data.aws_ami.ubuntu.id}"
  instance_type = "t2.micro"
  vpc_classic_link_security_groups = ["${aws_security_group.tomcat.id}"]

  user_data = <<-EOF
                #!/bin/bash
                echo "Hello, Capsid" > index.html
                nohup busybox httpd -f -p "${var.server_port}" &
                EOF

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_security_group" "elb" {
  name = "elb"

  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port = 80
    to_port = 80
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

data "aws_availability_zones" "all" {}

resource "aws_elb" "capsid" {
  name = "capsid-elb"
  security_groups = ["${aws_security_group.elb.id}"]
  availability_zones = ["${data.aws_availability_zones.all.names}"]

  health_check {
    healthy_threshold = 2
    unhealthy_threshold = 2
    timeout = 3
    interval = 30
    target = "HTTP:${var.server_port}/"
  }

  listener {
    lb_port = 80
    lb_protocol = "http"
    instance_port = "${var.server_port}"
    instance_protocol = "http"
  }
}

resource "aws_autoscaling_group" "capsid" {
  launch_configuration = "${aws_launch_configuration.capsid.name}"
  availability_zones = ["${data.aws_availability_zones.all.names}"]

  min_size = 1
  max_size = 2

  load_balancers = ["${aws_elb.capsid.name}"]
  health_check_type = "ELB"

  tag {
    key = "Name"
    value = "capsid-asg"
    propagate_at_launch = true
  }
}

output "elb_dns_name" {
  value = "${aws_elb.capsid.dns_name}"
}
