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

resource "aws_lb_target_group" "git" {
  name        = "${local.aws_ecs_git_service_name}"
  port        = 22
  protocol    = "TCP"
  vpc_id      = "${aws_vpc.main.id}"
  target_type = "ip" # targets are specified by IP address

  # `health_check.path` and `timeout` are not supported for target_groups with TCP protocol
  #
  # TODO:
  # Currently cannot pass healthcheck with the below error message:
  # > service qa-git-service (instance 10.0.0.104) (port 22) is unhealthy in
  # > target-group qa-git-service due to (reason Health checks failed)
  # Need to understand why that happens.
  health_check {
    interval = 10
    protocol = "TCP"
    healthy_threshold = "${var.git_count}"
    unhealthy_threshold = 2
  }
}

# Notice that for NLB there cannot be multiple listener rules. As it doesn't support
# path-based/host-based routing, there's no need to do so.
resource "aws_lb_listener" "git_front_end" {
  load_balancer_arn = "${aws_lb.git.id}"
  port              = 22
  protocol          = "TCP"

  default_action {
    target_group_arn = "${aws_lb_target_group.git.id}"
    type             = "forward"
  }
}
