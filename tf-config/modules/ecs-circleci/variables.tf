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

variable "web_app_count" {
  default = 2
}

variable "efs_mount_point" {
  # Cannot use a relative localtion from `~`, as it is created in `aws_launch_configuration`
  # for which `root` (rather than `ec2-user`) created it and `chown` it to `ec2-user`.
  default = "/mnt/efs"
}

locals {
  # Prefix to be used in the naming of some of the created AWS resources
  aws_resource_prefix = "ecs-circleci-${var.env_prefix}"

  # These names are used by CircleCI orbs
  aws_ecr_repository_name = "${local.aws_resource_prefix}-repository"
  aws_ecs_cluster_name = "${local.aws_resource_prefix}-cluster"
  aws_ecs_service_name = "${local.aws_resource_prefix}-service"

  # Internal reference only
  aws_vpc_name = "${local.aws_resource_prefix}-vpc"
  aws_alb_name = "${local.aws_resource_prefix}-alb"
  aws_alb_security_group = "${local.aws_resource_prefix}-alb-sg"
  aws_ecs_task_security_group = "${local.aws_resource_prefix}-ecs-task-sg"
  aws_efs_security_group = "${local.aws_resource_prefix}-efs-sg"
  aws_postgres_security_group = "${local.aws_resource_prefix}-postgres-sg"
  aws_db_instance_identifier = "${local.aws_resource_prefix}-postgres"
  aws_ecs_instance_profile = "${local.aws_resource_prefix}-ecs-instance-profile"
  aws_ecs_launch_configuration = "${local.aws_resource_prefix}-ecs-launch-configuration"
  aws_ecs_autoscaling_group = "${local.aws_resource_prefix}-ecs-asg"
  aws_git_efs = "${local.aws_resource_prefix}-git-efs"
}
