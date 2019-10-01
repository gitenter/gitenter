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
# No actual instance (target) is registered in this target group.
#
# TODO:
# Not sure why it is then necessary, as the priority=1 `aws_lb_listener_rule`
# consume all path patterns.
resource "aws_lb_target_group" "web_dummy" {
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

# Register all the web app instance/container into this target group.
resource "aws_lb_target_group" "web_app" {
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

  health_check {
    interval = 60
    # All below health_check endpoints need a healthy Spring Tomcat state.
    #
    # TODO:
    # The initial tomcat image cannot pass the `/health_check` healthcheck, as there's
    # no `/health_check` endpoint. It will just keep the infinite loop of
    # register/deregister.
    # However, the actual `web` image cannot pass `/` healthcheck, because `/` has been
    # redirect to `/login`.
    # Using `/health_check` is fine right now for CI pipeline (as we don't have any
    # test on the initial Tomcat is deployed successfully -- which is not). But
    # we may want a better solution so both works.
    # path = "/"
    path = "/health_check"
    # path = "/resources/static_health_check.html"
    protocol = "HTTP"
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
    timeout = 59
    healthy_threshold = "${var.web_app_count}"
    unhealthy_threshold = 2
  }
}

resource "aws_lb_target_group" "web_static" {
  name        = "${local.aws_ecs_web_static_service_name}"
  port        = "${var.http_port}"
  protocol    = "HTTP"
  vpc_id      = "${aws_vpc.main.id}"
  target_type = "ip"
  deregistration_delay = 20

  health_check {
    interval = 60
    path = "/about/"
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

  # Here defines the default rule. Default rules can't have conditions.
  # More rules with various priority can be defined later using `aws_lb_listener_rule`.
  # https://docs.aws.amazon.com/elasticloadbalancing/latest/application/load-balancer-listeners.html#listener-rules
  default_action {
    type             = "forward"
    target_group_arn = "${aws_lb_target_group.web_dummy.id}"
  }
}

# Load balancer listener can have multiple rules. Rules are evaluated in priority
# order, (based on whether the `condition` is satisfied or not) from the lowest
# value to the highest value. The default rule is evaluated last.
# https://docs.aws.amazon.com/elasticloadbalancing/latest/application/load-balancer-listeners.html#listener-rules
#
# TODO:
# We may consider having paths `/about`, `/documentation` in different servers
# (different ECS containers for which the service may be implemented in different
# frameworks).
# If that is the case, we'll need multiple rules with different priority and
# non-trivial path pattern.
resource "aws_lb_listener_rule" "web_app" {
  listener_arn = "${aws_lb_listener.web_front_end.arn}"
  priority     = 1

  action {
    type             = "forward"
    target_group_arn = "${aws_lb_target_group.web_app.arn}"
  }

  condition {
    field  = "path-pattern"
    values = ["*"]
  }
}

variable "path_patterns" {
  default = ["/about/*", "/contact/*", "/pricing/*", "/help/*"]
}

resource "aws_lb_listener_rule" "web_static" {
  count        = "${length(var.path_patterns)}"
  listener_arn = "${aws_lb_listener.web_front_end.arn}"
  priority     = "${count.index + 100}"

  action {
    type             = "forward"
    target_group_arn = "${aws_lb_target_group.web_static.arn}"
  }

  condition {
    field  = "path-pattern"
    values = ["${element(var.path_patterns, count.index)}"]
  }
}
