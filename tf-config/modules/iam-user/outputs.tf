output "access_key" {
  value = "${aws_iam_access_key.main.id}"
}

output "secret_key" {
  value = "${aws_iam_access_key.main.secret}"
}
