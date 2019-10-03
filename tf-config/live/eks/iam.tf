# This role is defined in live/iam-terraform-config
data "aws_iam_role" "eks" {
  name = "AmazonEksRole"
}

# This role is defined in live/iam-terraform-config
data "aws_iam_role" "eks_node" {
  name = "AmazonEKSforEC2Role"
}

resource "aws_iam_instance_profile" "eks_node" {
  name = "${local.aws_eks_instance_profile_name}"
  role = "${data.aws_iam_role.eks_node.name}"
}
