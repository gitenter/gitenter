# This role is defined in live/iam-terraform-config
data "aws_iam_role" "ecs_instance" {
  name = "AmazonEC2ContainerServiceforEC2Role"
}

# This role is defined in live/iam-terraform-config
data "aws_iam_role" "ecs_task_execution" {
  name = "AmazonECSTaskExecutionRole"
}

# Every role by default have a instance profile with the same name.
# `AmazonEC2ContainerServiceforEC2Role` has been customized with this
# instance profile.
# https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_use_switch-role-ec2_instance-profiles.html
#
# TODO:
# As instance profile has nothing to do per environment, we should
# move this resource to `iam-terraform-roles-setup` and try to get
# `data.aws_iam_instance_profile` in here.
resource "aws_iam_instance_profile" "main" {
    name = "${local.main_resource_name}"
    path = "/"
    role = "${data.aws_iam_role.ecs_instance.id}"
    provisioner "local-exec" {
      command = "sleep 10"
    }
}
