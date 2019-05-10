variable "env_prefix" {
  description = "Prefix to distinguish different environments. E.g., `qa`, `staging`, `prod`."
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
  aws_postgres_security_group = "${local.aws_resource_prefix}-postgres-sg"
  aws_db_instance_identifier = "${local.aws_resource_prefix}-postgres"
  aws_ecs_instance_profile = "${local.aws_resource_prefix}-ecs-instance-profile"
  aws_ecs_launch_configuration = "${local.aws_resource_prefix}-ecs-launch-configuration"
  aws_ecs_autoscaling_group = "${local.aws_resource_prefix}-ecs-asg"
}
