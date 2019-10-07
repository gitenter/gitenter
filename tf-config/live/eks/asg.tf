data "aws_ami" "eks_optimized_amis" {
  owners      = ["602401143452"] # Amazon EKS AMI Account ID
  most_recent = true

  filter {
    name   = "name"
    values = ["amazon-eks-node-${aws_eks_cluster.main.version}-v*"]
  }
}

resource "aws_launch_configuration" "main" {
  name_prefix                 = "${local.aws_launch_configuration_name}"
  iam_instance_profile        = "${aws_iam_instance_profile.eks_node.name}"
  security_groups             = ["${aws_security_group.eks_node.id}"]

  image_id                    = "${data.aws_ami.eks_optimized_amis.id}"
  instance_type               = "t2.small"

  user_data                   = <<EOF
  #!/bin/bash
  set -o xtrace
  /etc/eks/bootstrap.sh --apiserver-endpoint '${aws_eks_cluster.main.endpoint}' --b64-cluster-ca '${aws_eks_cluster.main.certificate_authority.0.data}' '${local.aws_eks_cluster_name}'
EOF

  lifecycle {
    create_before_destroy = true
  }

  associate_public_ip_address = true
  key_name                    = "${aws_key_pair.terraform-seashore.key_name}"

  depends_on = [
    "aws_eks_cluster.main"
  ]
}

resource "aws_autoscaling_group" "main" {
  name                 = "${local.aws_autoscaling_group_name}"
  launch_configuration = "${aws_launch_configuration.main.id}"
  vpc_zone_identifier  = ["${aws_subnet.public.*.id}"]

  min_size             = 1
  max_size             = 2
  desired_capacity     = 2

  tag {
    key                 = "Name"
    value               = "${local.aws_autoscaling_group_name}"
    propagate_at_launch = true
  }

  tag {
    key                 = "kubernetes.io/cluster/${local.aws_eks_cluster_name}"
    value               = "owned"
    propagate_at_launch = true
  }
}
