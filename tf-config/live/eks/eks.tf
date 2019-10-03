resource "aws_eks_cluster" "main" {
  name            = "${local.aws_eks_cluster_name}"
  role_arn        = "${data.aws_iam_role.eks.arn}"

  vpc_config {
    security_group_ids = ["${aws_security_group.eks_cluster.id}"]
    subnet_ids         = ["${aws_subnet.public.*.id}"]
  }
}
