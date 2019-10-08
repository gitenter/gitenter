# May refer to:
# https://cwong47.gitlab.io/technology-terraform-aws-efs/

resource "aws_efs_file_system" "main" {
  creation_token = "${local.main_resource_name}"

  performance_mode = "generalPurpose"
  # TODO:
  # `encrypted = true` for production. Keep false for now for debugging needs.

  tags = {
    Name = "${local.main_resource_name}"
  }
}

resource "aws_efs_mount_target" "main" {
  file_system_id = "${aws_efs_file_system.main.id}"

  count          = "${var.az_count}"
  subnet_id      = "${element(aws_subnet.public.*.id, count.index)}"
  security_groups = ["${aws_security_group.efs.id}"]
}
