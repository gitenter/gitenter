variable "environment" {
  description = "Prefix to distinguish different environments. E.g., `dev`, `test`, `staging`, `prod`."
}

variable "az_count" {
  # For both a private and a public subnets, this number needs
  # to be <=128 (65536/(256*2)=128), otherwise the subnet
  # `cidr_block` will be out of range.
  default = 2
}

# Ideally what we want is a small number of instances, and each of them has multiple
# containers in it. However, it is possible we'll run out of elastic network interface (ENI).
# One `t2.small`/`t2.medium` container only allow 3 ENIs in it, so the first
# deployment goes fine but when we re-deploy we'll get the folloing error:
# > service qa-web-app-service was unable to place a task because no container instance
# > met all of its requirements. The closest matching container-instance ... encountered
# > error "RESOURCE:ENI". For more information, see the Troubleshooting section.
#
# It doesn't help to increase it to `t2.medium` as they have the same number of ENIs:
# https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/using-eni.html#AvailableIpPerENI
# Technically we can try to only re-deploy web-app/git container so it may be
# works, but it is in general just troublesome to be in there any the system is
# fragile.
#
# AWS has special treatment to increase ENI for EC2 instances, but that's only for large
# instances right now. We may use that feature in the future, but right now we'll just
# have more instances and only have one container per instance.
# https://aws.amazon.com/about-aws/whats-new/2019/06/Amazon-ECS-Improves-ENI-Density-Limits-for-awsvpc-Networking-Mode/
# https://docs.aws.amazon.com/AmazonECS/latest/developerguide/container-instance-eni.html
#
# Also, after re-deployment finish, we can manually goes to ASG console to change the
# number from 4 to 2. ECS will re-organize the running images so they can stay in these
# smaller number of EC2 instances.
variable "ec2_instance_count" {
  default = 6
}

variable "web_app_count" {
  # Needs to be >=2, as in `aws_lb_target_group.health_check` the minimal has to
  # be >=2 and the max (this value) has to be > min.
  default = 2
}

variable "web_static_count" {
  default = 2
}

variable "git_count" {
  default = 2
}

locals {
  name_prefix = "${var.environment}-ecs"

  main_resource_name = "${local.name_prefix}"
  web_entrace_resource_name = "${local.name_prefix}-web-all"
  web_app_resource_name = "${local.name_prefix}-web-app"
  web_static_resource_name = "${local.name_prefix}-web-static"
  git_resource_name = "${local.name_prefix}-git"

  # Below variables are cross-used in different Terraform files.
  ecs_cluster_name = "${local.main_resource_name}"
}

locals {
  # Seems not useful outside of Terraform.
  # Cannot use a relative localtion from `~`, as it is created in `aws_launch_configuration`
  # for which `root` (rather than `ec2-user`) created it and `chown` it to `ec2-user`.
  efs_mount_point = "/mnt/efs"

  http_port = 80
  web_app_export_port = 8080
  web_static_export_port = 80
}
