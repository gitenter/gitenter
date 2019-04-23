resource "aws_cloudformation_stack" "vpc" {
  name = "${var.aws_vpc_stack_name}"
  template_body = "${file("cloudformation-templates/public-vpc.yml")}"
  capabilities = ["CAPABILITY_NAMED_IAM"]
  parameters {
    VpcId = "${aws_vpc.main.id}"
    PublicSubnetOneId = "${aws_subnet.public.0.id}"
    PublicSubnetTwoId = "${aws_subnet.public.1.id}"
    # `ClusterName` are defined as parameters of `cloudformation-templates/public-vpc.yml`.
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
