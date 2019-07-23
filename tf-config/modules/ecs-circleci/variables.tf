variable "environment" {
  description = "Prefix to distinguish different environments. E.g., `dev`, `test`, `staging`, `prod`."
}

variable "az_count" {
  # For both a private and a public subnets, this number needs
  # to be <=128 (65536/(256*2)=128), otherwise the subnet
  # `cidr_block` will be out of range.
  default = "2"
}

variable "http_port" {
  default = 80
}

variable "tomcat_container_port" {
  default = 8080
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
  default = 4
}

variable "web_app_count" {
  # Needs to be >=2, as in `aws_lb_target_group.health_check` the minimal has to
  # be >=2 and the max (this value) has to be > min.
  default = 2
}

variable "git_count" {
  default = 2
}

# Seems not useful outside of Terraform.
variable "efs_mount_point" {
  # Cannot use a relative localtion from `~`, as it is created in `aws_launch_configuration`
  # for which `root` (rather than `ec2-user`) created it and `chown` it to `ec2-user`.
  default = "/mnt/efs"
}

# Seems not useful outside of Terraform.
variable "efs_docker_volumn_name" {
  default = "efs-static-storage"
}

# This is the path need to be used in code (e.g. Java setup of `capsid` setup to
# touch the file system).
#
# TODO:
# Probably should set this as `/home/git` but in that case multiple git docker
# containers will need to share the same `.*` setup files, which is not doable.
# So probably needs `/home/git/data` or we do `/git` and `chown` it.
variable "efs_web_container_path" {
  default = "/data"
}

locals {
  aws_web_app_resource_infix = "web-app"
  aws_git_resource_infix = "git"

  # Prefix to be used in the naming of some of the created AWS resources
  aws_resource_prefix = "${var.environment}"
  aws_web_app_resource_prefix = "${local.aws_resource_prefix}-${local.aws_web_app_resource_infix}"
  aws_git_resource_prefix = "${local.aws_resource_prefix}-${local.aws_git_resource_infix}"

  # These names are used by CircleCI orbs
  aws_ecs_cluster_name = "${local.aws_resource_prefix}-cluster"
  aws_web_app_ecr_name = "${local.aws_web_app_resource_prefix}-repository"
  aws_ecs_web_app_service_name = "${local.aws_web_app_resource_prefix}-service"
  aws_git_ecr_name = "${local.aws_git_resource_prefix}-repository"
  aws_ecs_git_service_name = "${local.aws_git_resource_prefix}-service"

  # Internal reference only
  aws_vpc_name = "${local.aws_resource_prefix}-vpc"
  aws_postgres_name = "${local.aws_resource_prefix}-postgres"
  aws_redis_session_name = "${local.aws_resource_prefix}-redis-sess" # Need to <=20 characters
  aws_git_efs_name = "${local.aws_resource_prefix}-git-efs"

  aws_web_lb_name = "${local.aws_resource_prefix}-web-alb"
  aws_git_lb_name = "${local.aws_resource_prefix}-git-nlb"

  aws_web_alb_security_group = "${local.aws_resource_prefix}-web-alb-sg"
  aws_web_app_security_group = "${local.aws_resource_prefix}-web-app-sg"
  aws_git_security_group = "${local.aws_resource_prefix}-git-sg"
  aws_efs_security_group = "${local.aws_resource_prefix}-efs-sg"
  aws_postgres_security_group = "${local.aws_resource_prefix}-postgres-sg"
  aws_redis_security_group = "${local.aws_resource_prefix}-redis-sg"

  aws_ecs_instance_profile = "${local.aws_resource_prefix}-ecs-instance-profile"
  aws_launch_configuration = "${local.aws_resource_prefix}-launch-configuration"
  aws_autoscaling_group = "${local.aws_resource_prefix}-asg"
}
