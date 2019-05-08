# This role is defined in live/iam-terraform-config
data "aws_iam_role" "ecs_instance" {
  name = "AmazonEC2ContainerServiceforEC2Role"
}

resource "aws_iam_instance_profile" "ecs_instance" {
    name = "${local.aws_ecs_instance_profile}"
    path = "/"
    role = "${data.aws_iam_role.ecs_instance.id}"
    provisioner "local-exec" {
      command = "sleep 10"
    }
}

# This role is defined in live/iam-terraform-config
data "aws_iam_role" "ecs_task_execution" {
  name = "AmazonECSTaskExecutionRole"
}
