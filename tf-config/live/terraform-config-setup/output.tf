output "ecs_instance_role_name" {
  value = "${module.iam_roles.ecs_instance_role_name}"
}

output "ecs_task_execution_role_name" {
  value = "${module.iam_roles.ecs_task_execution_role_name}"
}

output "terraform_config_user_access_key" {
  value = "${aws_iam_access_key.main.id}"
}

output "terraform_config_user_secret_key" {
  value = "${aws_iam_access_key.main.secret}"
}
