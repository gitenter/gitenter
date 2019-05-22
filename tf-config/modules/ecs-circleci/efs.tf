# May refer to:
# https://cwong47.gitlab.io/technology-terraform-aws-efs/

resource "aws_efs_file_system" "git" {
  creation_token = "${local.aws_git_efs}"

  performance_mode = "generalPurpose"
  # TODO:
  # `encrypted = true` for production. Keep false for now for debugging needs.

  tags = {
    Name = "${local.aws_git_efs}"
  }
}

resource "aws_efs_mount_target" "git" {
  file_system_id = "${aws_efs_file_system.git.id}"

  count          = "${var.az_count}"
  subnet_id      = "${element(aws_subnet.public.*.id, count.index)}"
  security_groups = ["${aws_security_group.efs.id}"]
}
