# Service-linked role
# https://docs.aws.amazon.com/IAM/latest/UserGuide/using-service-linked-roles.html
#
# This is a role which is used by the ECS tasks themselves.
resource "aws_iam_role" "ecs_task_execution" {
  name               = "${local.aws_ecs_execution_role_name}"
  path               = "/"
  assume_role_policy = "${data.aws_iam_policy_document.ecs_task_execution.json}"
}

data "aws_iam_policy_document" "ecs_task_execution" {
  statement {
    actions = ["sts:AssumeRole"]
    effect  = "Allow"

    principals {
      type        = "Service"
      identifiers = ["ecs-tasks.amazonaws.com"]
    }
  }
}

data "aws_iam_policy" "ecs_task_execution" {
  arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution_attach" {
  role       = "${aws_iam_role.ecs_task_execution.name}"
  policy_arn = "${data.aws_iam_policy.ecs_task_execution.arn}"
}
