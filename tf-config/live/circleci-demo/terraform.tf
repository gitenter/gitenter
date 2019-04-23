locals {
  # The name of the CloudFormation stack to be created for the ECS service and related resources
  aws_ecs_service_stack_name = "${var.aws_resource_prefix}-svc-stack"
  # The name of the ECS cluster to be created
  aws_ecs_cluster_name = "${var.aws_resource_prefix}-cluster"
  # The name of the ECS service to be created
  aws_ecs_service_name = "${var.aws_resource_prefix}-service"
}

resource "aws_cloudformation_stack" "vpc" {
  name = "${var.aws_vpc_stack_name}"
  template_body = "${file("cloudformation-templates/public-vpc.yml")}"
  capabilities = ["CAPABILITY_NAMED_IAM"]
  parameters {
    ClusterName = "${local.aws_ecs_cluster_name}"
    VpcId = "${aws_vpc.main.id}"
    PublicSubnetOneId = "${aws_subnet.public.0.id}"
    PublicSubnetTwoId = "${aws_subnet.public.1.id}"
    # `ClusterName` are defined as parameters of `cloudformation-templates/public-vpc.yml`.
  }
}

# Note: creates task definition and task definition family with the same name as the ServiceName parameter value
resource "aws_cloudformation_stack" "ecs_service" {
  name = "${local.aws_ecs_service_stack_name}"
  template_body = "${file("cloudformation-templates/public-service.yml")}"
  depends_on = ["aws_cloudformation_stack.vpc", "aws_cloudformation_stack.role", "aws_ecr_repository.demo-app-repository"]

  parameters {
    ContainerMemory = 1024
    ContainerPort = 8080
    StackName = "${var.aws_vpc_stack_name}"
    RoleStackName = "${var.aws_role_stack_name}"
    ServiceName = "${local.aws_ecs_service_name}"
    # Note: Since ImageUrl parameter is not specified, the Service
    # will be deployed with the nginx image when created
  }
}
