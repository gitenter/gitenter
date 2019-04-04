output "access_key" {
  value = "${module.iam_user.access_key}"
}

output "secret_key" {
  value = "${module.iam_user.secret_key}"
}
