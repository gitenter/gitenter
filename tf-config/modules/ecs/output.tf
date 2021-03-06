# Broswer access of the tomcat instance
#
# Both website and EFS docker mount-bind are only available a while after the
# Terraform job is done, as `aws_ecs_servive` (which is created first) is keeping
# seeking available EC2 instances and apply task to it.
output "web_lb_hostname" {
  value = "http://${aws_lb.web.dns_name}"
}

output "git_lb_hostname" {
  value = "http://${aws_lb.git.dns_name}"
}

output "postgres_endpoint" {
  value = "${aws_db_instance.postgres.endpoint}"
}

output "redis_session_endpoint" {
  value = "${aws_elasticache_replication_group.redis_session.primary_endpoint_address}"
}
