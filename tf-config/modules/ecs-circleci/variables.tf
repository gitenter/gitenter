variable "env_prefix" {
  description = "Prefix to distinguish different environments. E.g., `qa`, `staging`, `prod`."
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
# containers in it. However, it is posslbe we'll run out of elastic network interface (ENI).
# For example, if we deploy 2 different tasks to `t2.small` instance, then when
# in re-deployment we'll get the folloing error:
# > service qa-web-app-service was unable to place a task because no container instance
# > met all of its requirements. The closest matching container-instance ... encountered
# > error "RESOURCE:ENI". For more information, see the Troubleshooting section.
# It doesn't help to increase it to `t2.medium` as they have the same number of ENIs:
# https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/using-eni.html#AvailableIpPerENI
#
# AWS has special treatment to increase ENI for EC2 instances, but that's only for large
# instances right now. We may use that feature in the future, but right now we'll just
# have more instances and only have one container per instance.
# https://aws.amazon.com/about-aws/whats-new/2019/06/Amazon-ECS-Improves-ENI-Density-Limits-for-awsvpc-Networking-Mode/
# https://docs.aws.amazon.com/AmazonECS/latest/developerguide/container-instance-eni.html
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
  # Prefix to be used in the naming of some of the created AWS resources
  aws_resource_prefix = "${var.env_prefix}"

  # These names are used by CircleCI orbs
  aws_ecr_repository_name = "${local.aws_resource_prefix}-repository"
  aws_ecs_cluster_name = "${local.aws_resource_prefix}-cluster"
  aws_ecs_web_app_service_name = "${local.aws_resource_prefix}-web-app-service"
  aws_ecs_git_service_name = "${local.aws_resource_prefix}-git-service"

  # Internal reference only
  aws_vpc_name = "${local.aws_resource_prefix}-vpc"
  aws_web_lb_name = "${local.aws_resource_prefix}-web-alb"
  aws_git_lb_name = "${local.aws_resource_prefix}-git-nlb"
  aws_web_alb_security_group = "${local.aws_resource_prefix}-web-alb-sg"
  aws_web_app_security_group = "${local.aws_resource_prefix}-web-app-sg"
  aws_git_security_group = "${local.aws_resource_prefix}-git-sg"
  aws_efs_security_group = "${local.aws_resource_prefix}-efs-sg"
  aws_postgres_security_group = "${local.aws_resource_prefix}-postgres-sg"
  aws_postgres = "${local.aws_resource_prefix}-postgres"
  aws_redis_security_group = "${local.aws_resource_prefix}-redis-sg"
  aws_redis_session = "${local.aws_resource_prefix}-redis-session" # Need to <=20 characters
  aws_ecs_instance_profile = "${local.aws_resource_prefix}-ecs-instance-profile"
  aws_web_app_launch_configuration = "${local.aws_resource_prefix}-web-app-launch-configuration"
  aws_web_app_autoscaling_group = "${local.aws_resource_prefix}-web-app-asg"
  aws_git_launch_configuration = "${local.aws_resource_prefix}-git-launch-configuration"
  aws_git_autoscaling_group = "${local.aws_resource_prefix}-git-asg"
  aws_git_efs = "${local.aws_resource_prefix}-git-efs"
}
