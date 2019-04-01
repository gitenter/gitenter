output "key_pair_fingerprint" {
  value = "${aws_key_pair.terraform-seashore.fingerprint}"
  description = "Key pair ${aws_key_pair.terraform-seashore.key_name} fingerprint"
}

# Used by `docker push`
output "ecr_repository_url" {
  value = "${aws_ecr_repository.terraform-ecs.repository_url}"
}
