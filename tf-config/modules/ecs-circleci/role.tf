# This is the service-linked role automatically created by `aws_ecs_service`
data "aws_iam_role" "ecs_instance" {
  name = "AWSServiceRoleForECS"

  # TODO:
  # It is right now in the setup (and will not be destroied by `terraform destroy`)
  # but may need some `dependency` in here for save.
}

resource "aws_iam_instance_profile" "ecs_instance" {
    name = "ecs-instance-profile"
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
