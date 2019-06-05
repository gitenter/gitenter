output "web_lb_hostname" {
  value = "${module.ecs_circleci.web_lb_hostname}"
}

output "git_lb_hostname" {
  value = "${module.ecs_circleci.git_lb_hostname}"
}

output "postgres_endpoint" {
  value = "${module.ecs_circleci.postgres_endpoint}"
}

output "redis_session_endpoint" {
  value = "${module.ecs_circleci.redis_session_endpoint}"
}
