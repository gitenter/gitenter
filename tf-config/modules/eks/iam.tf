# This role is defined in live/iam-terraform-config
data "aws_iam_role" "eks_service" {
  name = "serviceRoleForEKS"
}

# This role is defined in live/iam-terraform-config
data "aws_iam_role" "eks_node_instance" {
  name = "serviceRoleForEKSNodeInstanceForEC2"
}

resource "aws_iam_instance_profile" "main" {
  name = "${local.main_resource_name}"
  role = "${data.aws_iam_role.eks_node_instance.name}"
}
