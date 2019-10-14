data "aws_route53_zone" "main" {
  name = "gitenter.com"
}

resource "aws_route53_record" "main" {
  zone_id = "${data.aws_route53_zone.main.zone_id}"

  # TODO:
  # Use `www` if environment is production.
  name    = "${var.environment}.${data.aws_route53_zone.main.name}"

  type    = "A"

  # TODO:
  # Ideally we want it to redirect to ALB (web) for port 80, and NLB (git) to
  # port 22.
  #
  # Route53 doesn't support it, because DNS record just doesn't support port.
  #
  # One possibility is to have a NLB (all) in front of everything, and it
  # redirect to the web ALB and git NLB based on port. (The very front one need
  # to be a NLB, because ALB only supports 80 and 8080.)
  # The setup is messy because forwarding needs fixed IP while the ALB IP is
  # changing. Workaround:
  # https://stackoverflow.com/questions/50981864/aws-pass-traffic-from-nlb-to-an-alb
  # https://aws.amazon.com/blogs/networking-and-content-delivery/using-static-ip-addresses-for-application-load-balancers/
  #
  # Or we can have a Nginx in frout of them (one more kind of container + single
  # point failure).
  #
  # Or we just need a different subdomain for git access (e.g. `staging.git.gitenter.com`)
  alias {
    name                   = "${aws_lb.web.dns_name}"
    zone_id                = "${aws_lb.web.zone_id}"
    evaluate_target_health = false
  }
}
