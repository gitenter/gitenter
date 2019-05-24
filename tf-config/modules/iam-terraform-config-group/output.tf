output "stateless_group_name" {
  value = "${aws_iam_group.stateless.name}"
}

output "storage_group_name" {
  value = "${aws_iam_group.storage.name}"
}

output "accessary_group_name" {
  value = "${aws_iam_group.accessary.name}"
}
