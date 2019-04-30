locals {
  # The name of the CloudFormation stack to be created for the ECS service and related resources
  aws_ecs_service_stack_name = "${var.aws_resource_prefix}-svc-stack"
  # The name of the ECS service to be created
  aws_ecs_service_name = "${var.aws_resource_prefix}-service"
}

resource "aws_ecs_cluster" "main" {
  name = "${var.aws_ecs_cluster_name}"
}

# TODO:
# Current deployment is through rolling update (`ECS`). Consider to change
# to blue/green (`CODE_DEPLOY`) deployment.
# https://docs.aws.amazon.com/AmazonECS/latest/APIReference/API_DeploymentController.html

# Note: creates task definition and task definition family with the same name as the ServiceName parameter value
resource "aws_cloudformation_stack" "ecs_service" {
  name = "${local.aws_ecs_service_stack_name}"
  template_body = "${file("cloudformation-templates/public-service.yml")}"
  depends_on = [
    "aws_cloudformation_stack.role",
    "aws_ecr_repository.demo-app-repository",
    "aws_ecs_cluster.main"
  ]

  parameters {
    ContainerMemory = 1024
    ContainerPort = 80
    StackName = "${var.aws_vpc_stack_name}"
    VpcId = "${aws_vpc.main.id}"
    PublicSubnetOneId = "${aws_subnet.public.0.id}"
    PublicSubnetTwoId = "${aws_subnet.public.1.id}"
    PublicLoadBalancerListenerArn = "${aws_alb_listener.front_end.arn}"
    FargateContainerSecurityGroupId = "${aws_security_group.ecs_tasks.id}"
    RoleStackName = "${var.aws_role_stack_name}"
    ServiceName = "${local.aws_ecs_service_name}"
    ClusterName = "${var.aws_ecs_cluster_name}"
    # Note: Since ImageUrl parameter is not specified, the Service
    # will be deployed with the nginx image when created
  }
}
