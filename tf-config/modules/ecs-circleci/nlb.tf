# ALB only support HTTP and HTTPS protocol. To use git/SSH (TCP port 22)
# we need to use network load balancer.
resource "aws_lb" "git" {
  name               = "${local.aws_git_lb_name}"
  internal           = false
  load_balancer_type = "network"
  subnets            = ["${aws_subnet.public.*.id}"]

  # Always true for ALB. Default false for NLB.
  # https://docs.aws.amazon.com/elasticloadbalancing/latest/userguide/how-elastic-load-balancing-works.html#cross-zone-load-balancing
  enable_cross_zone_load_balancing = true

  idle_timeout       = 30

  # TODO:
  # Change to `true` for production
  enable_deletion_protection = false

  depends_on = [
    "aws_internet_gateway.gw"
  ]
}

resource "aws_alb_target_group" "git_dummy" {
  port        = 22
  protocol    = "TCP"
  vpc_id      = "${aws_vpc.main.id}"
  target_type = "ip" # targets are specified by IP address

  # `health_check.path` and `timeout` are not supported for target_groups with TCP protocol
  health_check {
    interval = 10
    protocol = "TCP"
    healthy_threshold = "${var.web_app_count}"
    unhealthy_threshold = 2
  }
}

resource "aws_lb_listener" "git_front_end" {
  load_balancer_arn = "${aws_lb.git.id}"
  port              = 22
  protocol          = "TCP"

  default_action {
    target_group_arn = "${aws_alb_target_group.git_dummy.id}"
    type             = "forward"
  }
}

# TODO:
# A similar one for `aws_lb_listener_rule.git_all` for real target group.
