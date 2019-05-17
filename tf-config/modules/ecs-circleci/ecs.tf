variable "web_image" {
  default     = "tomcat:latest"
}

locals {
  # Needs to match EC2 `instance_type`.
  task_cpu = 256 # 1 vCPU = 1024 CPU units
  task_memory = 512 # in MiB
}

# The task definition. This is a simple metadata description of what
# container to run, and what resource requirements it has.
resource "aws_ecs_task_definition" "web" {
  family                   = "${local.aws_ecs_service_name}"
  network_mode             = "awsvpc"
  requires_compatibilities = ["EC2"]
  cpu                      = "${local.task_cpu}"
  memory                   = "${local.task_memory}"

  execution_role_arn       = "${data.aws_iam_role.ecs_task_execution.arn}"
  # TODO:
  # Sounds like there's no need to specify `task_role_arn`, which allows ECS
  # container task to make calls to other AWS services.
  # Originally
  /*
Parameters:
  Role:
    Type: String
    Default: ""
    Description: (Optional) An IAM role to give the service's containers if the code within needs to
                 access other AWS resources like S3 buckets, DynamoDB tables, etc
Conditions:
  HasCustomRole: !Not [ !Equals [!Ref 'Role', ''] ]
Resources:
  TaskDefinition:
    TaskRoleArn:
      Fn::If:
        - 'HasCustomRole'
        - !Ref 'Role'
        - !Ref "AWS::NoValue"
  */

  # Refer to: https://docs.aws.amazon.com/AmazonECS/latest/APIReference/API_ContainerDefinition.html
  # For `awsvpc` network mode, The `hostPort` can be left blank or it must be the
  # same value as the `containerPort`.
  container_definitions = <<DEFINITION
[
  {
    "name": "${local.aws_ecs_service_name}",
    "cpu": ${local.task_cpu},
    "memory": ${local.task_memory},
    "image": "${var.web_image}",
    "essential": true,
    "portMappings": [
      {
        "containerPort": ${var.tomcat_container_port}
      }
    ],
    "environment": [
      {
        "name": "VERSION_INFO",
        "value": "v0"
      },
      {
        "name": "BUILD_DATE",
        "value": "-"
      }
    ],
    "mountPoints": [
      {
        "sourceVolume": "${var.efs_docker_volumn_name}",
        "containerPath": "${var.container_path}",
        "readOnly": false
      }
    ]
  }
]
DEFINITION

  # Mount docker volume to EFS:
  # https://docs.aws.amazon.com/AmazonECS/latest/developerguide/docker-volumes.html
  #
  # Without any setup in here, if we
  # > docker run -v ${var.efs_mount_point}:/data/efs -it tomcat /bin/bash
  # we can see/edit the content of EFS by `cd /data/efs`.
  #
  # After we set up as this, if we `docker ps` and then
  # > docker exec -it <container-id> /bin/bash
  # `cd` to `var.container_path` and add something, that will be saved in EFS.
  volume {
    # After this setup, if we `docker volume ls` we'll see the volume name (currently
    # driver is `local`).
    name      = "${var.efs_docker_volumn_name}"

    # `host_path` is for defining "bind mount":
    # https://docs.aws.amazon.com/AmazonECS/latest/developerguide/bind-mounts.html
    # while `docker_volume_configuration` is for defining "docker volume":
    # https://docs.aws.amazon.com/AmazonECS/latest/developerguide/docker-volumes.html
    #
    # Cannot specify both, otherwise I'll get error:
    # > ClientException: When using the 'volume' parameter, either 'host' or
    # > 'dockerVolumeConfiguration' should be used but not both.
    #
    # TODO:
    # Based on docker, "docker volume" is the prefered way to use compare to "bind mount".
    # https://docs.docker.com/storage/volumes/
    # we should try to make it work.
    #
    # TODO:
    # If not specify `driver`, the default is `local` driver from `docker volume ls`.
    # Check the possibility to use another driver.
    # > ... through the use of Docker volume drivers and volume plugins such as Rex-Ray and Portworx.
    host_path = "${var.efs_mount_point}"

    # docker_volume_configuration {
    #   scope = "shared"
    #   autoprovision = true
    # }
  }

  # Seems no need to make it depends on `aws_launch_configuration.ecs`.
  # > Before the release of the Amazon ECS-optimized AMI version 2017.03.a, only
  # > file systems that were available when the Docker daemon was started are
  # > available to Docker containers. You can use the latest Amazon ECS-optimized
  # > AMI to avoid this limitation, or you can upgrade the docker package to the
  # > latest version and restart Docker.
  # https://docs.aws.amazon.com/AmazonECS/latest/developerguide/using_data_volumes.html
  # Actually it may be prefered for `aws_ecs_task_definition` and `aws_ecs_service`
  # to be executed before `aws_launch_configuration` and `aws_autoscaling_group`,
  # and then service will keep trying (at the beginning no available tasks) until
  # it gets the desired container instances to apply the task (a successful `apply`
  # is by this order).
}

# The service. The service is a resource which allows you to run multiple
# copies of a type of task, and gather up their logs and metrics, as well
# as monitor the number of running tasks and replace any that have crashed
#
# http://blog.shippable.com/setup-a-container-cluster-on-aws-with-terraform-part-2-provision-a-cluster
# https://github.com/Capgemini/terraform-amazon-ecs
resource "aws_ecs_service" "web" {
  name            = "${local.aws_ecs_service_name}"

  # No need to specify `iam_role` as we are using `awsvpc` network mode.
  # A service-linked role `AWSServiceRoleForECS` will be created automatically.
  # https://docs.aws.amazon.com/AmazonECS/latest/developerguide/using-service-linked-roles.html
  cluster         = "${aws_ecs_cluster.main.id}"
  task_definition = "${aws_ecs_task_definition.web.arn}"
  desired_count   = "${var.web_app_count}"
  launch_type     = "EC2"

  deployment_maximum_percent = 200
  deployment_minimum_healthy_percent = 75

  network_configuration {
    # TODO:
    # After setting up private subnets and NAT gateway, here should be replaced
    # by private subnet.
    # That may break SSH access defined in `aws_security_group.ecs_tasks` but
    # needs to double check.
    security_groups = ["${aws_security_group.ecs_tasks.id}"]
    subnets         = ["${aws_subnet.public.*.id}"]

    # `assign_public_ip = true` is not supported for this launch type
  }

  load_balancer {
    container_name   = "${local.aws_ecs_service_name}"
    container_port   = "${var.tomcat_container_port}"
    target_group_arn = "${aws_alb_target_group.app.id}"
  }

  # TODO:
  # Current deployment is through rolling update (`ECS`). Consider to change
  # to blue/green (`CODE_DEPLOY`) deployment.
  # https://docs.aws.amazon.com/AmazonECS/latest/APIReference/API_DeploymentController.html
  deployment_controller {
    type = "ECS"
  }

  depends_on = [
    "aws_ecr_repository.app_repository",
    "aws_lb_listener_rule.all"
  ]
}

resource "aws_ecs_cluster" "main" {
  name = "${local.aws_ecs_cluster_name}"
}
