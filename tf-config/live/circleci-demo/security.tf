# ALB Security Group: Edit this to restrict access to the application
resource "aws_security_group" "lb" {
  name        = "terraform-ecs-lb"
  description = "controls access to the ALB"
  vpc_id      = "${aws_vpc.main.id}"

  egress {
    protocol    = "-1"
    from_port   = 0
    to_port     = 0
    cidr_blocks = ["0.0.0.0/0"]
  }
}
