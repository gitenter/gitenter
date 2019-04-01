resource "aws_key_pair" "terraform-seashore" {
  key_name   = "terraform-key_pair-seashore"
  public_key = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQC6vmegnYSA36MiMa/miDe/qQQSAFT6o2pWgoz07gDozY/6eSLVWJ8j3BtfC/ykAQJ5yp1J9TZSsE0VX+MWZtZpm4stGOnQqiTNWwKoS3bfTnubopW9eF+Fk7kNy6gZWhf7bqU7gSk+497vx7kBgwjDRUB82AovAG/aGtxl2CATZzh3ylh5OhHXjUtv+1gWPZZcDkADLjtvNtCKiDGBV5ERiy7hPk70scmVLtakFUqhhwmw1cV3wtLsK5egttffZLxXJVC6A1RG1ysb5p11SnUny5hTYl0ZpoM+ZfQeupM0HGKdAXJXPqKQ6Przn3BG7e8DDdwOlUQ+8VXwclvmmppr seashore"
}

output "key_pair_fingerprint" {
  value = "${aws_key_pair.terraform-seashore.fingerprint}"
  description = "Key pair ${aws_key_pair.terraform-seashore.key_name} fingerprint"
}
