output "alb_hostname" {
  value = "${module.ecs_circleci.alb_hostname}"
}

output "postgres_endpoint" {
  value = "${module.ecs_circleci.postgres_endpoint}"
}
