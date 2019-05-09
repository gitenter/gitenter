data "aws_ami" "ubuntu" {
  most_recent = true

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-bionic-18.04-amd64-server-*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }

  owners = ["099720109477"] # Canonical
}

resource "aws_launch_configuration" "ecs" {
  name                        = "${local.aws_ecs_launch_configuration}"
  image_id                    = "${data.aws_ami.ubuntu.id}"
  instance_type               = "t2.micro"
  iam_instance_profile        = "${aws_iam_instance_profile.ecs_instance.id}"
  security_groups             = ["${aws_security_group.ecs_tasks.id}"]

  # register the cluster name with ecs-agent which will in turn coordinate
  # with the AWS api about the cluster
  user_data                   = <<EOF
#!/bin/bash
echo ECS_CLUSTER=${local.aws_ecs_cluster_name} >> /etc/ecs/ecs.config
EOF

  root_block_device {
    volume_type = "standard"
    volume_size = 8 # in gigabytes
    delete_on_termination = true
  }

  lifecycle {
    create_before_destroy = true
  }

  associate_public_ip_address = true
  key_name                    = "${aws_key_pair.terraform-seashore.key_name}"
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
}
