resource "aws_iam_group" "storage" {
  name = "${local.storage_group_name}"
}

data "aws_iam_policy" "rds" {
  arn = "arn:aws:iam::aws:policy/AmazonRDSFullAccess"
}

resource "aws_iam_group_policy_attachment" "rds" {
  group = "${aws_iam_group.storage.id}"
  policy_arn = "${data.aws_iam_policy.rds.arn}"
}

data "aws_iam_policy" "elasticache" {
  arn = "arn:aws:iam::aws:policy/AmazonElastiCacheFullAccess"
}

resource "aws_iam_group_policy_attachment" "elasticache" {
  group = "${aws_iam_group.storage.id}"
  policy_arn = "${data.aws_iam_policy.elasticache.arn}"
}

# TODO:
# Probably only need `elasticfilesystem:CreateFileSystem` for EFS operations,
# but I'll just give full access for now.
data "aws_iam_policy" "efs" {
  arn = "arn:aws:iam::aws:policy/AmazonElasticFileSystemFullAccess"
}

resource "aws_iam_group_policy_attachment" "efs" {
  group = "${aws_iam_group.storage.id}"
  policy_arn = "${data.aws_iam_policy.efs.arn}"
}
