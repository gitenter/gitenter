variable "app_image" {
  default     = "tomcat:latest"
}

locals {
  # Number of docker containers to run
  app_count = 2
  # Docker instance CPU units to provision (1 vCPU = 1024 CPU units)
  task_cpu = 256
  # Docker instance memory to provision (in MiB
  task_memory = 512
}

# TODO:
# Move this file to `live/ecs-deploy`
resource "aws_ecs_task_definition" "app" {
  family                   = "capsid-app"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "${local.task_cpu}"
  memory                   = "${local.task_memory}"

  container_definitions = <<DEFINITION
[
  {
    "name": "capsid-app",
    "networkMode": "awsvpc",
    "cpu": ${local.task_cpu},
    "memory": ${local.task_memory},
    "image": "${var.app_image}",
    "portMappings": [
      {
        "containerPort": ${var.app_port},
        "hostPort": ${var.app_port}
      }
    ]
  }
]
DEFINITION
}

resource "aws_ecs_service" "main" {
  name            = "terraform-ecs-service"
  cluster         = "${aws_ecs_cluster.main.id}"
  task_definition = "${aws_ecs_task_definition.app.arn}"
  desired_count   = "${local.app_count}"
  launch_type     = "FARGATE"

  network_configuration {
    security_groups = ["${aws_security_group.ecs_tasks.id}"]
    subnets         = ["${aws_subnet.private.*.id}"]
  }

  load_balancer {
    target_group_arn = "${aws_alb_target_group.app.id}"
    container_name   = "capsid-app"
    container_port   = "${var.app_port}"
  }

  # TODO:
  # Current deployment is through rolling update (`ECS`). Consider to change
  # to blue/green (`CODE_DEPLOY`) deployment.
  # https://docs.aws.amazon.com/AmazonECS/latest/APIReference/API_DeploymentController.html
  deployment_controller {
    type = "ECS"
  }

  depends_on = [
    "aws_alb_listener.front_end",
  ]
}

resource "aws_ecs_cluster" "main" {
  name = "terraform-ecs"
}
