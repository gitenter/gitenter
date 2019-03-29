resource "aws_iam_user" "terraform" {
  name = "${var.username}"
}

resource "aws_iam_access_key" "terraform" {
  user = "${aws_iam_user.terraform.name}"
}

resource "aws_iam_group_membership" "terraform" {
  name = "terraform-group-membership"

  users = [
    "${aws_iam_user.terraform.name}",
  ]

  group = "${var.group_name}"
}
