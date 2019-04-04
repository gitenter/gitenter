# ALB Security Group: Edit this to restrict access to the application
resource "aws_security_group" "lb" {
  name        = "terraform-ecs-lb"
  description = "controls access to the ALB"
  vpc_id      = "${aws_vpc.main.id}"

  ingress {
    protocol    = "tcp"
    from_port   = 80
    to_port     = 80
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    protocol    = "-1"
    from_port   = 0
    to_port     = 0
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Traffic to the ECS cluster should only come from the ALB
resource "aws_security_group" "ecs_tasks" {
  name        = "terraform-ecs-task"
  description = "allow inbound access from the ALB only"
  vpc_id      = "${aws_vpc.main.id}"

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
    from_port = "${var.app_port}"
    to_port = "${var.app_port}"
    protocol = "tcp"
    # TODO: Below two lines should be able to be removed, as only load balancer is public facing.
    cidr_blocks = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"] # May not be needed as the associated VPC is without an IPv6 CIDR block
    security_groups = ["${aws_security_group.lb.id}"]
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
}
