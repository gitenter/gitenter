resource "aws_alb" "main" {
  name               = "terraform-ecs"
  internal           = false
  load_balancer_type = "application"
  security_groups    = ["${aws_security_group.lb.id}"]
  subnets            = ["${aws_subnet.public.*.id}"]

  idle_timeout       = 60

  # TODO:
  # Change to `true` for production
  enable_deletion_protection = false
}

resource "aws_alb_target_group" "app" {
  name        = "terraform-ecs"
  port        = 80
  protocol    = "HTTP"
  vpc_id      = "${aws_vpc.main.id}"
  target_type = "ip"

  health_check {
    interval = 30
    path = "/"
    protocol = "HTTP"
    timeout = 6
    healthy_threshold = 3
    unhealthy_threshold = 3
  }
}

# Redirect all traffic from the ALB to the target group
resource "aws_alb_listener" "front_end" {
  load_balancer_arn = "${aws_alb.main.id}"
  port              = "80"
  protocol          = "HTTP"

  default_action {
    target_group_arn = "${aws_alb_target_group.app.id}"
    type             = "forward"
  }
}

output "alb_hostname" {
  value = "${aws_alb.main.dns_name}"
}
