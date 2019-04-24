resource "aws_cloudformation_stack" "vpc" {
  name = "${var.aws_vpc_stack_name}"
  template_body = "${file("cloudformation-templates/public-vpc.yml")}"
  capabilities = ["CAPABILITY_NAMED_IAM"]
  depends_on = [
    "aws_ecs_cluster.main"
  ]

  parameters {
    ClusterName = "${var.aws_ecs_cluster_name}"
    VpcId = "${aws_vpc.main.id}"
    PublicSubnetOneId = "${aws_subnet.public.0.id}"
    PublicSubnetTwoId = "${aws_subnet.public.1.id}"
    PublicLoadBalancerSGId = "${aws_security_group.lb.id}"
    PublicLoadBalancerDNSName = "${aws_alb.main.dns_name}"
    PublicLoadBalancerListenerArn = "${aws_alb_listener.front_end.arn}"
    FargateContainerSecurityGroupId = "${aws_security_group.ecs_tasks.id}"
  }
}

output "vpc_outputs" {
  # TODO:
  # Currently request `ExternalUrl` from this output will gives 503 error.
  # Ideally it should get Nginx after deployment.
  # Thinking it is related to network setting. Probably roll back to
  # master and check if it works or not.
  value = "${aws_cloudformation_stack.vpc.outputs}"
}
