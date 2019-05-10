# For Amazon Linux 2, ssh using `ssh ec2-user@<ip-address>`
#
# TODO:
# Should setup another jobs to add key pairs from different machines. May
# associate with login username. Also, should gather key pairs and all of them
# under a group can SSH into a particular set of machines created by
# `aws_launch_configuration`.
resource "aws_key_pair" "terraform-seashore" {
  key_name   = "terraform-key_pair-seashore"
  public_key = "${file("~/.ssh/id_rsa.pub")}"
}

output "key_pair_fingerprint" {
  value = "${aws_key_pair.terraform-seashore.fingerprint}"
  description = "Key pair ${aws_key_pair.terraform-seashore.key_name} fingerprint"
}
