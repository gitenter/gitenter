resource "aws_security_group" "eks_cluster" {
  name        = "${local.aws_eks_cluster_security_group}"
  vpc_id      = "${aws_vpc.main.id}"

  egress {
    protocol    = "-1"
    from_port   = 0
    to_port     = 0
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${local.aws_eks_cluster_security_group}"
  }
}

resource "aws_security_group_rule" "eks_cluster" {
  type              = "ingress"

  protocol          = "tcp"
  from_port         = 443
  to_port           = 443

  # TODO: `Custom IP` rather than `Anywhere`
  cidr_blocks       = ["0.0.0.0/0"]

  security_group_id = "${aws_security_group.eks_cluster.id}"
}

resource "aws_security_group_rule" "eks_cluster_node_ingress_https" {
  type                     = "ingress"

  protocol                 = "tcp"
  from_port                = 443
  to_port                  = 443

  security_group_id        = "${aws_security_group.eks_cluster.id}"
  source_security_group_id = "${aws_security_group.eks_node.id}"
}

resource "aws_security_group" "eks_node" {
  name        = "${local.aws_eks_node_security_group}"
  vpc_id      = "${aws_vpc.main.id}"

  egress {
    protocol    = "-1"
    from_port   = 0
    to_port     = 0
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = "${
    map(
     "Name", "${local.aws_eks_node_security_group}",
     "kubernetes.io/cluster/${local.aws_eks_cluster_name}", "owned",
    )
  }"
}

resource "aws_security_group_rule" "eks_node_self_ingress" {
  type                     = "ingress"

  protocol                 = "-1"
  from_port                = 0
  to_port                  = 65535

  security_group_id        = "${aws_security_group.eks_node.id}"
  self = true
}

resource "aws_security_group_rule" "eks_node_cluster_ingress" {
  type                     = "ingress"

  protocol                 = "tcp"
  from_port                = 1025
  to_port                  = 65535

  security_group_id        = "${aws_security_group.eks_node.id}"
  source_security_group_id = "${aws_security_group.eks_cluster.id}"
}
