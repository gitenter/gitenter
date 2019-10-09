locals {
  eks_cluster_control_panel_security_group_name = "${local.main_resource_name}-eks-cluster-control-panel"
  eks_node_security_group_name = "${local.main_resource_name}-eks-node"
}

resource "aws_security_group" "eks_cluster_control_panel" {
  name        = "${local.eks_cluster_control_panel_security_group_name}"
  vpc_id      = "${aws_vpc.main.id}"

  tags = {
    Name = "${local.eks_cluster_control_panel_security_group_name}"
  }
}

# TODO:
# This should be used for enable `kubectl` locally?
resource "aws_security_group_rule" "eks_cluster_control_panel" {
  type              = "ingress"

  protocol          = "tcp"
  from_port         = 443
  to_port           = 443

  # TODO: `Custom IP` rather than `Anywhere`
  cidr_blocks       = ["0.0.0.0/0"]

  security_group_id = "${aws_security_group.eks_cluster_control_panel.id}"
}

# Allow pods to communicate with the cluster API Server
resource "aws_security_group_rule" "eks_cluster_control_panel_node_ingress" {
  type                     = "ingress"

  protocol                 = "tcp"
  from_port                = 443
  to_port                  = 443

  security_group_id        = "${aws_security_group.eks_cluster_control_panel.id}"
  source_security_group_id = "${aws_security_group.eks_node.id}"
}

# Allow the cluster control plane to communicate with worker Kubelet and pods
resource "aws_security_group_rule" "eks_cluster_control_panel_to_node_egress" {
  type                     = "egress"

  protocol                 = "tcp"
  from_port                = 1025
  to_port                  = 65535

  security_group_id        = "${aws_security_group.eks_cluster_control_panel.id}"
  source_security_group_id = "${aws_security_group.eks_node.id}"
}

# Allow the cluster control plane to communicate with pods running extension API servers on port 443
resource "aws_security_group_rule" "eks_cluster_control_panel_to_node_egress_on_443" {
  type                     = "egress"

  protocol                 = "tcp"
  from_port                = 443
  to_port                  = 443

  security_group_id        = "${aws_security_group.eks_cluster_control_panel.id}"
  source_security_group_id = "${aws_security_group.eks_node.id}"
}

# https://docs.aws.amazon.com/eks/latest/userguide/getting-started-console.html#eks-launch-workers
# https://amazon-eks.s3-us-west-2.amazonaws.com/cloudformation/2019-10-08/amazon-eks-nodegroup.yaml
resource "aws_security_group" "eks_node" {
  name        = "${local.eks_node_security_group_name}"
  vpc_id      = "${aws_vpc.main.id}"

  tags = "${
    map(
     "Name", "${local.eks_node_security_group_name}",
     "kubernetes.io/cluster/${local.eks_cluster_name}", "owned",
    )
  }"
}

# Allow node to communicate with each other
resource "aws_security_group_rule" "eks_node_self_ingress" {
  type                     = "ingress"

  protocol                 = "-1"
  from_port                = 0
  to_port                  = 65535

  security_group_id        = "${aws_security_group.eks_node.id}"
  self = true
}

# Allow worker Kubelets and pods to receive communication from the cluster control plane
resource "aws_security_group_rule" "eks_node_from_cluster_control_panel_ingress" {
  type                     = "ingress"

  protocol                 = "tcp"
  from_port                = 1025
  to_port                  = 65535

  security_group_id        = "${aws_security_group.eks_node.id}"
  source_security_group_id = "${aws_security_group.eks_cluster_control_panel.id}"
}

# Allow pods running extension API servers on port 443 to receive communication from cluster control plane
resource "aws_security_group_rule" "eks_node_from_cluster_control_panel_ingress_on_443" {
  type                     = "ingress"

  protocol                 = "tcp"
  from_port                = 443
  to_port                  = 443

  security_group_id        = "${aws_security_group.eks_node.id}"
  source_security_group_id = "${aws_security_group.eks_cluster_control_panel.id}"
}
