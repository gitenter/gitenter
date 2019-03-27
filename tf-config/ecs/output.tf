output "Key pair fingerprint" {
  value = "${aws_key_pair.terraform-seashore.fingerprint}"
  description = "Key pair ${aws_key_pair.terraform-seashore.key_name} fingerprint"
}
