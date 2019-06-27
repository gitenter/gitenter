output "ecs_instance_role_name" {
  value = "${module.iam_roles.ecs_instance_role_name}"
}

output "ecs_task_execution_role_name" {
  value = "${module.iam_roles.ecs_task_execution_role_name}"
}

# TODO:
# Use "remote state data provider" so downstream Terraform configs
# can use the output of this module.
# https://www.terraform.io/docs/providers/terraform/d/remote_state.html
output "terraform_config_user_access_key" {
  value = "${aws_iam_access_key.main.id}"
}

output "terraform_config_user_secret_key" {
  value = "${aws_iam_access_key.main.secret}"
}
