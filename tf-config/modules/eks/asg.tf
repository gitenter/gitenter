locals {
  autoscaling_group_name = "${local.main_resource_name}"

  # https://github.com/awslabs/amazon-eks-ami/blob/master/files/bootstrap.sh
  bootstrap_arguments = ""
}

data "aws_ami" "eks_optimized_amis" {
  owners      = ["602401143452"] # Amazon EKS AMI Account ID
  most_recent = true

  filter {
    name   = "name"
    values = ["amazon-eks-node-${aws_eks_cluster.main.version}-v*"]
  }
}

# https://docs.aws.amazon.com/eks/latest/userguide/getting-started-console.html#eks-launch-workers
# https://amazon-eks.s3-us-west-2.amazonaws.com/cloudformation/2019-10-08/amazon-eks-nodegroup.yaml
resource "aws_launch_configuration" "main" {
  name_prefix                 = "${local.main_resource_name}-"
  iam_instance_profile        = "${aws_iam_instance_profile.main.name}"
  security_groups             = ["${aws_security_group.eks_node.id}"]

  image_id                    = "${data.aws_ami.eks_optimized_amis.id}"
  instance_type               = "t2.small"

  user_data                   = <<EOF
#!/bin/bash
set -o xtrace
/etc/eks/bootstrap.sh ${local.eks_cluster_name} ${local.bootstrap_arguments}
/opt/aws/bin/cfn-signal --exit-code $? \
         --stack ${local.eks_cluster_name}-worker-nodes \
         --resource NodeGroup  \
         --region ${var.aws_region}
EOF

  lifecycle {
    create_before_destroy = true
  }

  associate_public_ip_address = true
  key_name                    = "${aws_key_pair.terraform-seashore.key_name}"

  # TODO:
  # >      BlockDeviceMappings:
  # >        - DeviceName: /dev/xvda
  # >          Ebs:
  # >            DeleteOnTermination: true
  # >            VolumeSize: !Ref NodeVolumeSize
  # >            VolumeType: gp2

  depends_on = [
    "aws_eks_cluster.main"
  ]
}

resource "aws_autoscaling_group" "main" {
  name                 = "${local.autoscaling_group_name}"
  launch_configuration = "${aws_launch_configuration.main.id}"
  vpc_zone_identifier  = ["${aws_subnet.public.*.id}"]

  min_size             = 1
  max_size             = 2
  desired_capacity     = 2

  tag {
    key                 = "Name"
    value               = "${local.autoscaling_group_name}"
    propagate_at_launch = true
  }

  tag {
    key                 = "kubernetes.io/cluster/${local.eks_cluster_name}"
    value               = "owned"
    propagate_at_launch = true
  }

  # TODO:
  # >    UpdatePolicy:
  # >      AutoScalingRollingUpdate:
  # >        MaxBatchSize: "1"
  # >        MinInstancesInService: !Ref NodeAutoScalingGroupDesiredCapacity
  # >        PauseTime: PT5M
}
