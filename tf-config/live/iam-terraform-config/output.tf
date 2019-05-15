output "ecs_instance_role_name" {
  value = "${module.iam_roles.ecs_instance_role_name}"
}

output "ecs_task_execution_role_name" {
  value = "${module.iam_roles.ecs_task_execution_role_name}"
}

output "terraform_user_access_key" {
  value = "${module.iam_user.access_key}"
}

output "terraform_user_secret_key" {
  value = "${module.iam_user.secret_key}"
}
