data "aws_route53_zone" "main" {
  name = "gitenter.com"
}

resource "aws_route53_record" "web" {
  zone_id = "${data.aws_route53_zone.main.zone_id}"
  type    = "A"

  name    = "${var.environment == "production" ? "www" : var.environment}.${data.aws_route53_zone.main.name}"

  alias {
    name                   = "${aws_lb.web.dns_name}"
    zone_id                = "${aws_lb.web.zone_id}"
    evaluate_target_health = false
  }
}

# TODO:
# Ideally we want both web (port 80) and git (port 22) to use
# `gitenter.com`/`staging.gitenter.com`. That basically need a master load
# balancer on top of everything (and redirect traffic to `aws_lb.web` and
# `aws_lb.git` accordingly). Reason is in DNS level we talked nothing about
# ports.
#
# It is hard to be done using existing load balancers in AWS, as ALB have
# better support (e.g. we need pattern matching forwarding) but it only supports
# port 80 and 8080. NLB (currently used for git access for us) supports all ports,
# but it only have limited functions. We may have a NLB on top of the current
# `aws_lb.web` and `aws_lb.git`, but it is hard for an NLB to route to ALB
# because the later one doesn't have a fixed IP. Workarounds:
# https://stackoverflow.com/questions/50981864/aws-pass-traffic-from-nlb-to-an-alb
# https://aws.amazon.com/blogs/networking-and-content-delivery/using-static-ip-addresses-for-application-load-balancers/
#
# Or we can have a Nginx in frout of them (one more kind of container + single
# point failure).
resource "aws_route53_record" "git" {
  zone_id = "${data.aws_route53_zone.main.zone_id}"
  type    = "A"

  # TODO:
  # A way to not `var.environment == "production"` two times?
  name    = "git.${var.environment == "production" ? "" : var.environment}${var.environment == "production" ? "" : "."}${data.aws_route53_zone.main.name}"

  alias {
    name                   = "${aws_lb.git.dns_name}"
    zone_id                = "${aws_lb.git.zone_id}"
    evaluate_target_health = false
  }
}
