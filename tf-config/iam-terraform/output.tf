output "User access_key" {
  value = "${aws_iam_access_key.terraform.id}"
  description = "User ${aws_iam_user.terraform.name} access_key"
}

output "User secret_key" {
  value = "${aws_iam_access_key.terraform.secret}"
  description = "User ${aws_iam_user.terraform.name} secret_key"
}

output "Key pair fingerprint" {
  value = "${aws_key_pair.terraform-seashore.fingerprint}"
  description = "Key pair ${aws_key_pair.terraform-seashore.key_name} fingerprint"
}
