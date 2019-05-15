resource "aws_iam_user" "main" {
  name = "${var.username}"
}

resource "aws_iam_access_key" "main" {
  user = "${aws_iam_user.main.name}"
}

resource "aws_iam_group_membership" "main" {
  name = "${var.username}-${var.group_name}-membership"

  users = [
    "${aws_iam_user.main.name}",
  ]

  group = "${var.group_name}"
}
