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

# Generated from /iam Terraform module
#
# TODO:
# May seperate AWS accounts for testing environment (clean up deployment
# completely after test is done) and lnog-stay environment (prod/...).
variable "aws_access_key" {}
variable "aws_secret_key" {}

provider "aws" {
  access_key = "${var.aws_access_key}"
  secret_key = "${var.aws_secret_key}"
  region = "us-east-1"
}

resource "aws_security_group" "tomcat" {
  name = "tomcat"

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

  # SSH
  ingress {
    from_port = 22
    to_port = 22
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # TODO: `Custom IP` rather than `Anywhere`
    ipv6_cidr_blocks = ["::/0"]
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

resource "aws_key_pair" "terraform-seashore" {
  key_name   = "test_key_pair-seashore"
  public_key = "${file("~/.ssh/id_rsa.pub")}"
}

resource "aws_launch_configuration" "capsid" {
  name = "capsid"
  image_id = "${data.aws_ami.ubuntu.id}"
  instance_type = "t2.micro"
  # vpc_classic_link_security_groups = ["${aws_security_group.tomcat.id}"]
  security_groups = ["${aws_security_group.tomcat.id}"]

  user_data = <<-EOF
                #!/bin/bash
                echo "Hello, Capsid" > index.html
                nohup busybox httpd -f -p "80" &
                EOF

  lifecycle {
    create_before_destroy = true
  }

  # AssociatePublicIpAddress field can not be specified without a subnet id
  # associate_public_ip_address = true

  key_name                    = "${aws_key_pair.terraform-seashore.key_name}"
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

resource "aws_default_vpc" "default" {}

data "aws_subnet_ids" "all" {
  vpc_id = "${aws_default_vpc.default.id}"
}

resource "aws_lb" "capsid" {
  name = "capsid-elb"
  internal           = false
  load_balancer_type = "application"
  security_groups = ["${aws_security_group.elb.id}"]
  subnets = ["${data.aws_subnet_ids.all.ids}"]
}

resource "aws_lb_target_group" "capsid" {
  port        = 80
  protocol    = "HTTP"
  vpc_id      = "${aws_default_vpc.default.id}"
  target_type = "instance"

  health_check {
    interval = 30
    path = "/"
    protocol = "HTTP"
    timeout = 3
    healthy_threshold = 2
    unhealthy_threshold = 2
  }
}

resource "aws_lb_listener" "capsid" {
  load_balancer_arn = "${aws_lb.capsid.id}"
  port              = 80
  protocol          = "HTTP"

  # Here defines the default rule. Default rules can't have conditions.
  # More rules with various priority can be defined later using `aws_lb_listener_rule`.
  # https://docs.aws.amazon.com/elasticloadbalancing/latest/application/load-balancer-listeners.html#listener-rules
  default_action {
    type             = "forward"
    target_group_arn = "${aws_lb_target_group.capsid.id}"
  }
}

resource "aws_autoscaling_group" "capsid" {
  launch_configuration = "${aws_launch_configuration.capsid.name}"
  vpc_zone_identifier         = ["${data.aws_subnet_ids.all.ids}"]

  min_size = 1
  max_size = 2

  target_group_arns = ["${aws_lb_target_group.capsid.arn}"]
  health_check_type = "ELB"

  tag {
    key = "Name"
    value = "capsid-asg"
    propagate_at_launch = true
  }
}

output "tomcat_lb_dns_name" {
  value = "${aws_lb.capsid.dns_name}"
}
