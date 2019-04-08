resource "aws_key_pair" "terraform-seashore" {
  key_name   = "terraform-key_pair-seashore"
  public_key = "${file("~/.ssh/id_rsa.pub")}"
}

output "key_pair_fingerprint" {
  value = "${aws_key_pair.terraform-seashore.fingerprint}"
  description = "Key pair ${aws_key_pair.terraform-seashore.key_name} fingerprint"
}
