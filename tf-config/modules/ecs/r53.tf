data "aws_route53_zone" "main" {
  name = "gitenter.com"
}

resource "aws_route53_record" "main" {
  zone_id = "${data.aws_route53_zone.main.zone_id}"

  # TODO:
  # Use `www` if environment is production.
  name    = "${var.environment}.${data.aws_route53_zone.main.name}"

  type    = "A"
  ttl     = "300"
  records = ["10.0.0.1"]
}
