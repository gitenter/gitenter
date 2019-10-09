resource "aws_eks_cluster" "main" {
  name            = "${local.eks_cluster_name}"
  role_arn        = "${data.aws_iam_role.eks_service.arn}"

  vpc_config {
    security_group_ids = ["${aws_security_group.eks_cluster_control_panel.id}"]
    subnet_ids         = ["${aws_subnet.public.*.id}"]
  }
}
