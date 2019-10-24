output "web_lb_hostname" {
  value = "${module.ecs.web_lb_hostname}"
}

output "git_lb_hostname" {
  value = "${module.ecs.git_lb_hostname}"
}

output "postgres_endpoint" {
  value = "${module.ecs.postgres_endpoint}"
}

output "redis_session_endpoint" {
  value = "${module.ecs.redis_session_endpoint}"
}
