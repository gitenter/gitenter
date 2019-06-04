resource "aws_iam_group" "accessary" {
  name = "${local.accessary_group_name}"
}

resource "aws_iam_policy" "ecr" {
  name        = "AmazonECRConfigPolicy"
  path        = "/"

  # https://docs.aws.amazon.com/AmazonECR/latest/userguide/RepositoryPolicyExamples.html
  # `ecr:CreateRepository` and `ecr:ListTagsForResource` is used by `resource "aws_ecr_repository"`
  # `ecr:DescribeRepositories` is used by `data "aws_ecr_repository"`
  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "DenyPull",
            "Effect": "Allow",
            "Action": [
                "ecr:CreateRepository",
                "ecr:DeleteRepository",
                "ecr:ListTagsForResource",
                "ecr:DescribeRepositories"
            ],
            "Resource": "*"
        }
    ]
}
EOF
}

resource "aws_iam_group_policy_attachment" "ecr" {
  group = "${aws_iam_group.accessary.id}"
  policy_arn = "${aws_iam_policy.ecr.arn}"
}
