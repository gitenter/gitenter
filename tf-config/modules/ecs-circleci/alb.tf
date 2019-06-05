resource "aws_lb" "web" {
  name               = "${local.aws_web_lb_name}"
  internal           = false
  load_balancer_type = "application"
  security_groups    = ["${aws_security_group.web_alb.id}"]
  subnets            = ["${aws_subnet.public.*.id}"]

  idle_timeout       = 30

  # TODO:
  # Change to `true` for production
  enable_deletion_protection = false

  depends_on = [
    "aws_internet_gateway.gw"
  ]
}

# A dummy target group is used to setup the ALB to just drop traffic
# initially, before any real service target groups have been added.
resource "aws_alb_target_group" "web_app_dummy" {
  port        = "${var.http_port}"
  protocol    = "HTTP"
  vpc_id      = "${aws_vpc.main.id}"
  target_type = "ip"

  health_check {
    interval = 60
    path = "/"
    protocol = "HTTP"
    timeout = 59
    healthy_threshold = "${var.web_app_count}"
    unhealthy_threshold = 2
  }
}

# A target group. This is used for keeping track of all the tasks, and
# what IP addresses / port numbers they have. You can query it yourself,
# to use the addresses yourself, but most often this target group is just
# connected to an application load balancer, or network load balancer, so
# it can automatically distribute traffic across all the targets.
resource "aws_alb_target_group" "web_app" {
  name        = "${local.aws_ecs_web_app_service_name}"
  port        = "${var.http_port}"
  protocol    = "HTTP"
  vpc_id      = "${aws_vpc.main.id}"
  target_type = "ip"
  deregistration_delay = 20

  # Although we are using Spring Session (therefore no absolute need for sticky sessions
  # and/or session replication), we still enable it, as it can open the possibility
  # for local RAM caching.
  #
  # It is relatively easy to setup sticky sessions in AWS. For ALB, it is defined in target group
  # https://docs.aws.amazon.com/elasticloadbalancing/latest/application/load-balancer-target-groups.html#sticky-sessions
  # while if we use ELB, it is defined inside of the load balancer
  # https://docs.aws.amazon.com/elasticloadbalancing/latest/classic/elb-sticky-sessions.html
  # https://www.terraform.io/docs/providers/aws/r/lb_cookie_stickiness_policy.html
  stickiness {
    type = "lb_cookie"
    # `cookie_duration` cannot be the same with Spring "remember me" duration defined
    # in `SecurityConfig.java`, as it needs to be in between 1 second and 1 week.
    cookie_duration = 86400
    enabled = true
  }

  # `timeout` cannot be too small, otherwise when deploying the real service
  # system will error out. Then the newly created task will be killed and start
  # over (forever).
  # > service ecs-circleci-qa-service (instance 10.0.0.29) (port 8080) is unhealthy in
  # > target-group ecs-circleci-qa-service due to (reason Request timed out)
  # > Task failed ELB health checks in (target-group ...)
  #
  # After increasing timeout period (and check `/`) I am getting
  # > service ecs-circleci-qa-service (instance 10.0.1.241) (port 8080) is unhealthy in
  # > target-group ecs-circleci-qa-service due to (reason Health checks failed with these codes: [404])
  # Therefore, I added and use the `/health_check` endpoint.
  #
  # TODO:
  # Probably should pass a different path for health check.
  health_check {
    interval = 60
    path = "/health_check"
    # path = "/resources/static_health_check.html" # Both health_check endpoint needs a healthy Spring Tomcat state.
    protocol = "HTTP"
    timeout = 59
    healthy_threshold = "${var.web_app_count}"
    unhealthy_threshold = 2
  }
}

# Redirect all traffic from the ALB to the target group
resource "aws_lb_listener" "web_front_end" {
  load_balancer_arn = "${aws_lb.web.id}"
  port              = "${var.http_port}"
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = "${aws_alb_target_group.web_app_dummy.id}"
  }
}

# Create a rule on the load balancer for routing traffic to the target group
resource "aws_lb_listener_rule" "web_all" {
  listener_arn = "${aws_lb_listener.web_front_end.arn}"
  priority     = 1

  action {
    type             = "forward"
    target_group_arn = "${aws_alb_target_group.web_app.arn}"
  }

  condition {
    field  = "path-pattern"
    values = ["*"]
  }
}
