output "ecs_instance_role_name" {
  value = "${aws_iam_role.ecs_instance.name}"
}

output "ecs_task_execution_role_name" {
  value = "${aws_iam_role.ecs_task_execution.name}"
}
