resource "aws_iam_group" "terraform" {
  name = "${var.group_name}"
}

# TODO:
# Basically just need ECR push/pull and `aws ecs update-service`, so should be
# even thiner.
data "aws_iam_policy" "terraform-ecs" {
  arn = "arn:aws:iam::aws:policy/AmazonECS_FullAccess"
}

resource "aws_iam_group_policy_attachment" "terraform-ecs" {
  group = "${aws_iam_group.terraform.id}"
  policy_arn = "${data.aws_iam_policy.terraform-ecs.arn}"
}

data "aws_iam_policy" "terraform-ecs_fargate" {
  arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_group_policy_attachment" "terraform-ecs_fargate" {
  group = "${aws_iam_group.terraform.id}"
  policy_arn = "${data.aws_iam_policy.terraform-ecs_fargate.arn}"
}

resource "aws_iam_policy" "terraform-ecr" {
  name        = "AmazonECRDeployPolicy"
  path        = "/"

  # https://docs.aws.amazon.com/AmazonECR/latest/userguide/RepositoryPolicyExamples.html
  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "AllowPushPull",
            "Effect": "Allow",
            "Action": [
                "ecr:GetDownloadUrlForLayer",
                "ecr:BatchGetImage",
                "ecr:BatchCheckLayerAvailability",
                "ecr:PutImage",
                "ecr:InitiateLayerUpload",
                "ecr:UploadLayerPart",
                "ecr:CompleteLayerUpload"
            ],
            "Resource": "*"
        }
    ]
}
EOF
}

resource "aws_iam_group_policy_attachment" "terraform-ecr" {
  group = "${aws_iam_group.terraform.id}"
  policy_arn = "${aws_iam_policy.terraform-ecr.arn}"
}
