data "aws_ami" "ecs_optimized_amis" {
  most_recent = true

  # Needs to use "Amazon ECS-optimized AMIs" for which the "ECS container agent"
  # is pre-installed.
  # https://docs.aws.amazon.com/AmazonECS/latest/developerguide/ecs-optimized_AMI.html
  filter {
    name   = "name"
    values = ["amzn2-ami-ecs-hvm*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }

  filter {
    name   = "owner-alias"
    values = ["amazon"]
  }
}

resource "aws_launch_configuration" "ecs" {
  name                        = "${local.aws_ecs_launch_configuration}"
  iam_instance_profile        = "${aws_iam_instance_profile.ecs_instance.id}"
  security_groups             = ["${aws_security_group.ecs_tasks.id}"]

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

  # Register the cluster name with ecs-agent which will in turn coordinate
  # with the AWS api about the cluster.
  # No need to `mkdir /etc/ecs` because it is pre-setup for AMIs with "ECS
  # container agent".
  #
  # Debugging if the instances cannot be registered:
  # https://docs.aws.amazon.com/AmazonECS/latest/developerguide/launch_container_instance.html
  user_data                   = <<EOF
#!/bin/bash
echo ECS_CLUSTER=${local.aws_ecs_cluster_name} >> /etc/ecs/ecs.config
EOF

  root_block_device {
    # `volume_size` needs to be >=30, otherwise error:
    # > StatusMessage: "Volume of size _GB is smaller than snapshot 'snap-0a2a6b21a0c4cda56',
    # > expect size >= 30GB. Launching EC2 instance failed."
    volume_type = "standard"
    volume_size = 30 # in gigabytes
    delete_on_termination = true
  }

  lifecycle {
    create_before_destroy = true
  }

  associate_public_ip_address = true
  key_name                    = "${aws_key_pair.terraform-seashore.key_name}"

  depends_on = [
    "aws_ecs_cluster.main"
  ]
}

resource "aws_autoscaling_group" "ecs" {
  name                        = "${local.aws_ecs_autoscaling_group}"
  launch_configuration        = "${aws_launch_configuration.ecs.name}"
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
