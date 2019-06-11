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

# TODO:
# Currently will fail this step by
/*
* module.ecs_circleci.aws_autoscaling_group.web_app: 1 error(s) occurred:

* aws_autoscaling_group.web_app: "qa-web-app-asg": Waiting up to 10m0s: Need at least 2 healthy instances in ASG, have 0. Most recent activity: {
  ActivityId: "ece5ad04-df35-38af-605b-a4d2527faee9",
  AutoScalingGroupName: "qa-web-app-asg",
  Cause: "At 2019-06-10T11:48:22Z an instance was started in response to a difference between desired and actual capacity, increasing the capacity from 0 to 2.",
  Description: "Launching a new EC2 instance.  Status Reason: The requested configuration is currently not supported. Please check the documentation for supported configurations. Launching EC2 instance failed.",
  Details: "{\"Subnet ID\":\"subnet-03c89947ac3d766bf\",\"Availability Zone\":\"us-east-1b\"}",
  EndTime: 2019-06-10 11:48:24 +0000 UTC,
  Progress: 100,
  StartTime: 2019-06-10 11:48:24.526 +0000 UTC,
  StatusCode: "Failed",
  StatusMessage: "The requested configuration is currently not supported. Please check the documentation for supported configurations. Launching EC2 instance failed."
}
*/
# Looks like has nothing to do with ECS setup (as create EC2 instance has not
# touching that part yet).
resource "aws_launch_configuration" "web_app" {
  name                        = "${local.aws_web_app_launch_configuration}"
  iam_instance_profile        = "${aws_iam_instance_profile.ecs_instance.id}"
  security_groups             = ["${aws_security_group.web_app.id}"]
#  security_groups             = ["${aws_security_group.web_app.id}", "${aws_security_group.git.id}"]

  image_id                    = "${data.aws_ami.ecs_optimized_amis.id}"
  # `instance_type` needs to match CPU/memory defined in `aws_ecs_task_definition`
  # to make sure there are surficient.
  # If too low, may face error when trying to `aws ecs update-service`:
  # > (service ecs-circleci-qa-service) was unable to place a task because no container
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

resource "aws_autoscaling_group" "web_app" {
  name                        = "${local.aws_web_app_autoscaling_group}"
  launch_configuration        = "${aws_launch_configuration.web_app.name}"
  vpc_zone_identifier         = ["${aws_subnet.public.*.id}"]

  min_size                    = "${var.web_app_count}"
  max_size                    = "${var.web_app_count}"
  desired_capacity            = "${var.web_app_count}"

  # TODO:
  # We don't need `load_balancers` because we are using `alb` rather than `elb`.
  # But if that's the case looks like we need to provide `target_group_arns`?
  health_check_type           = "ELB"

  lifecycle {
    create_before_destroy = true
  }
}
