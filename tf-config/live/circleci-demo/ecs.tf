locals {
  # The name of the CloudFormation stack to be created for the ECS service and related resources
  aws_ecs_service_stack_name = "${var.aws_resource_prefix}-svc-stack"
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
    "aws_iam_role.ecs",
    "aws_iam_role.ecs_task_execution",
    "aws_ecr_repository.demo-app-repository",
    "aws_ecs_cluster.main",
    "aws_lb_listener_rule.all"
  ] # AWS::ECS::Service depends on LoadBalancerRule

  parameters {
    ContainerPort = "${var.container_port}"
    StackName = "${var.aws_vpc_stack_name}"
    PublicSubnetOneId = "${aws_subnet.public.0.id}"
    PublicSubnetTwoId = "${aws_subnet.public.1.id}"
    TargetGroupArn = "${aws_alb_target_group.app.arn}"
    ExecutionRoleArn = "${aws_iam_role.ecs_task_execution.arn}"
    FargateContainerSecurityGroupId = "${aws_security_group.ecs_tasks.id}"
    RoleStackName = "${var.aws_role_stack_name}"
    ServiceName = "${var.aws_ecs_service_name}"
    ClusterName = "${var.aws_ecs_cluster_name}"
    # Note: Since ImageUrl parameter is not specified, the Service
    # will be deployed with the nginx image when created
  }
}
