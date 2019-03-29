output "access_key" {
  value = "${aws_iam_access_key.terraform.id}"
  description = "User ${aws_iam_user.terraform.name} access_key"
}

output "secret_key" {
  value = "${aws_iam_access_key.terraform.secret}"
  description = "User ${aws_iam_user.terraform.name} secret_key"
}
