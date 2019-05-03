output "alb_hostname" {
  value = "http://${aws_alb.main.dns_name}"
}
