variable "task_cpu" {
  description = "Docker instance CPU units to provision (1 vCPU = 1024 CPU units)"
  default     = "256"
}

variable "task_memory" {
  description = "Docker instance memory to provision (in MiB)"
  default     = "512"
}

variable "app_image" {
  default     = "tomcat:latest"
}

variable "app_count" {
  description = "Number of docker containers to run"
  default     = 2
}

# TODO:
# Move this file to `live/ecs-deploy`
resource "aws_ecs_task_definition" "app" {
  family                   = "capsid-app"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "${var.task_cpu}"
  memory                   = "${var.task_memory}"

  container_definitions = <<DEFINITION
[
  {
    "image": "${var.app_image}",
    "cpu": ${var.task_cpu},
    "memory": ${var.task_memory},
    "name": "capsid-app",
    "networkMode": "awsvpc",
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
  desired_count   = "${var.app_count}"
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

  depends_on = [
    "aws_alb_listener.front_end",
  ]
}

resource "aws_ecs_cluster" "main" {
  name = "terraform-ecs"
}
