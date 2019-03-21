output "access_key" {
  value = "${aws_iam_access_key.terraform.id}"
  description = "access_key for user ${aws_iam_user.terraform.name}."
}

output "secret_key" {
  value = "${aws_iam_access_key.terraform.secret}"
  description = "secret_key for user ${aws_iam_user.terraform.name}."
}
