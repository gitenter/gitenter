# If it returns a non-qualified AMI (maybe because the search is not tight
# enough), AWS will return error when applying `aws_autoscaling_group`:
# > The requested configuration is currently not supported. Please check
# > the documentation for supported configurations. Launching EC2 instance failed.
# and the following debugging image explanation doesn't give any useful info:
# https://docs.aws.amazon.com/autoscaling/ec2/userguide/ts-as-instancelaunchfailure.html#ts-as-instancelaunchfailure-3
#
# Debugging tips:
# In https://console.aws.amazon.com/ec2/ we can read out the AMI id from
# launch configuration, and check what it is in `IMAGES > AMIs`.
#
# TODO:
# This search is sometimes fragile. When our constrain is not strong enough,
# sometimes later on when AWS provide newer images, the search gives something
# not qualified and breaks initialization. May consider just hard code the AMI
# but that breaks the flexibility.
data "aws_ami" "ecs_optimized_amis" {
  owners           = ["amazon"]
  most_recent      = true

  # Needs to use "Amazon ECS-optimized AMIs" for which the "ECS container agent"
  # is pre-installed.
  # https://docs.aws.amazon.com/AmazonECS/latest/developerguide/ecs-optimized_AMI.html
  filter {
    name   = "name"
    values = ["amzn2-ami-ecs-hvm-*-x86_64-ebs"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }

  filter {
    name   = "root-device-type"
    values = ["ebs"]
  }
}

resource "aws_launch_configuration" "main" {
  name                        = "${local.aws_launch_configuration}"
  iam_instance_profile        = "${aws_iam_instance_profile.ecs_instance.id}"
  security_groups             = [
    "${aws_security_group.web_app.id}",
    "${aws_security_group.web_static.id}",
    "${aws_security_group.git.id}"
  ]

  image_id                    = "${data.aws_ami.ecs_optimized_amis.id}"
  # `instance_type` needs to match CPU/memory defined in `aws_ecs_task_definition`
  # to make sure there are surficient.
  # If too low, may face error when trying to `aws ecs update-service`:
  # > (service ecs-qa-service) was unable to place a task because no container
  # > instance met all of its requirements. The closest matching (container-instance
  # > ...) has insufficient memory available. For more information, see the
  # > Troubleshooting section of the Amazon ECS Developer Guide.
  # https://docs.aws.amazon.com/AmazonECS/latest/developerguide/service-event-messages.html
  #
  # For example, `t2.micro` has 1024 MiB memory. Typically register 983 and available 471.
  # However, ECS task definition at least needs 512 memory. So it will be insurfficient.
  instance_type               = "t2.small"

  # The content of `user_data` is executed by `sudo`.
  #
  # Regarding `user_data`, the first part is to mount EFS volume.
  # https://docs.aws.amazon.com/efs/latest/ug/gs-step-three-connect-to-ec2-instance.html
  # https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/AmazonEFS.html#efs-mount-file-system
  # (The second URL uses just `nfs-utils`, and it doesn't work for Amazon Linux 2)
  # Should use `amazon-efs-utils` which includes more dependencies:
  # https://docs.aws.amazon.com/efs/latest/ug/using-amazon-efs-utils.html#overview-amazon-efs-utils
  #
  # The other way is to mount EFS after instance has been created. One may login and
  # > sudo mount -t efs fs-cc964c2f:/ /mnt/efs
  # Then the mounted one will be showing by `df -T` and file can be shared by
  # multiple EC2 instances. Also `mount` needs to be done with `sudo` so in `user_data`
  # `chown` needs to be done after `mount`.
  #
  # The second part is to register the cluster name with ecs-agent which will in turn coordinate
  # with the AWS api about the cluster.
  # No need to `mkdir /etc/ecs` because it is pre-setup for AMIs with "ECS
  # container agent".
  # Notice that multiple ECS services (under the same cluster) are using the same
  # set of EC2 instances.
  #
  # Debugging if the instances cannot be registered:
  # https://docs.aws.amazon.com/AmazonECS/latest/developerguide/launch_container_instance.html
  user_data                   = <<EOF
#!/bin/bash
yum update -y
yum install -y amazon-efs-utils
mkdir -p ${var.efs_mount_point}
mount -t efs ${aws_efs_file_system.git.id}:/ ${var.efs_mount_point}
chown ec2-user:ec2-user ${var.efs_mount_point}

echo ECS_CLUSTER=${local.aws_ecs_cluster_name} >> /etc/ecs/ecs.config
EOF

  root_block_device {
    # This is the EBS volume attached to the EC2 instance. It is good for hosting the drivers
    # of that instance (in our case, docker/image/...).
    # https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/block-device-mapping-concepts.html
    #
    # Types:
    # `standard`: Magnetic
    # `gp2`: General Purpose SSD
    # `io1`: Provisioned IOPS SSD
    # https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/EBSVolumes.html
    #
    # TODO:
    # Understand which one to use based on IOPS. At least it shouldn't be `standard`
    # for production.
    volume_type = "standard"
    # `volume_size` needs to be >=30, otherwise error:
    # > StatusMessage: "Volume of size _GB is smaller than snapshot 'snap-0a2a6b21a0c4cda56',
    # > expect size >= 30GB. Launching EC2 instance failed."
    volume_size = 30 # in gigabytes
    delete_on_termination = true
  }

  lifecycle {
    create_before_destroy = true
  }

  associate_public_ip_address = true
  key_name                    = "${aws_key_pair.terraform-seashore.key_name}"

  # Depends on `aws_efs_mount_target.git` because the current resource only depends on
  # `aws_efs_file_system.git`. By the time `user_data` is executed, `aws_efs_mount_target`
  # may not be available yet.
  #
  # May actually needs to be executed after `aws_ecs_service` (one successful `apply`
  # is of that order).
  depends_on = [
    "aws_ecs_cluster.main",
    "aws_efs_mount_target.git"
  ]
}

resource "aws_autoscaling_group" "main" {
  name                        = "${local.aws_autoscaling_group}"
  launch_configuration        = "${aws_launch_configuration.main.name}"
  vpc_zone_identifier         = ["${aws_subnet.public.*.id}"]

  min_size                    = "${var.ec2_instance_count}"
  max_size                    = "${var.ec2_instance_count}"
  desired_capacity            = "${var.ec2_instance_count}"

  # To register instances to load balancer, if the `target_type` is `instance` then
  # we can either specify `target_group_arns` in here, or use `aws_lb_target_group_attachment`
  # to attach them later.
  # If the `target_type` is `ip` then no need to specify `target_group_arns`.
  # As far as the target is in some subnet it can be found successfully.
  #
  # Terraform document said the target groups here are only for ALB.
  # It seems actually work for NLB.
  # https://www.terraform.io/docs/providers/aws/r/autoscaling_group.html#target_group_arns
  #
  # However we cannot use `instance` target type (hence specify `target_group_arns`
  # in here) because it is conflict with `awsvpc` network mode for ECS task edefinition.
  #
  # In AWS console "Load Balancing > Target Groups" we can check `Targets`
  # of each single target group to see if the targets are successfully registered.
  # target_group_arns           = [
  #   "${aws_lb_target_group.web_dummy.arn}",
  #   "${aws_lb_target_group.web_app.arn}",
  #   "${aws_lb_target_group.git.arn}"
  # ]
  health_check_type           = "ELB"

  lifecycle {
    create_before_destroy = true
  }

  # Below tags apply to EC2 instances (not ASG). Guide on using EC2 instance tags:
  # https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/Using_Tags.html
  tag {
    # TODO:
    # Refer to a unique name. May be the URL prefix to log into that instance.
    key                 = "Name"
    value               = "${local.aws_resource_prefix}-instance"
    propagate_at_launch = true
  }

  tag {
    key                 = "Environment"
    value               = "${var.environment}"
    propagate_at_launch = true
  }
}
