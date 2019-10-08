locals {
  web_app_ecs_service_name = "${local.web_app_resource_name}"
  web_static_ecs_service_name = "${local.web_static_resource_name}"
  git_ecs_service_name = "${local.git_resource_name}"

  # Seems not useful outside of Terraform.
  efs_docker_volumn_name = "${local.main_resource_name}"

  # This is the path need to be used in code (e.g. Java setup of `capsid` and
  # `post-receive-hook` setup to touch the file system).
  efs_web_app_container_path = "/data"

  # This is the path needed for the `AuthorizedKeyCommand` script in `ssheep`.
  #
  # TODO:
  # By sharing the `/home/git` folder as a volume, as mounting is AFTER docker
  # container starts, all the following files disappears. Not sure if that's a
  # (functional) problem through.
  # > root@02a50eab24c4:/home/git# ls -la
  # > -rw-------  1 git  root  788 Aug 16 19:58 .bash_history
  # > -rw-r--r--  1 git  root 3771 Apr  4  2018 .bashrc
  # > drwx------  4 git  root 4096 Jun 25 13:03 .cache
  # > -rw-r--r--  1 git  root  807 Apr  4  2018 .profile
  # > ...
  #
  # Possible solutions:
  # Put git folder to a different path, e.g. `/data` or `/home/git/data`. Need
  # to change the shellscript for `AuthorizedKeyCommand`. Also the git UI interface
  # becomes weird.
  #
  # Note:
  # There's a behavior difference that in docker-compose it will cause `/data` in web
  # container to have those hidden files, while in ECS/EFS it will cause `/home/git`
  # in git container to not have those files.
  efs_git_container_path = "/home/git"

  # Needs to be downsized then than EC2 `instance_type`. Otherwise a EC2 instance
  # cannot hold one single container.
  #
  # Notice that `t2.micro`-256/512 doesn't work. Probably that's because the memory
  # difference between EC2 and container are not large enough for ECS utilities.
  # `t2-small`-512/1024 doesn't work for deploying a whole website the SECOND time.
  #
  # Also, notice that every EC2 instance may hold multiple containers (so after
  # scaling up we may have bigger EC2 instances). However, for web application we
  # may have a lot of tiny traffic so no need for each container to be big.
  # By doing so, we don't need to have multiple executors (e.g. boosted by supervisor)
  # setup in our own code.
  task_cpu = 256 # 1 vCPU = 1024 CPU units
  task_memory = 512 # in MiB
}

# The task definition. This is a simple metadata description of what
# container to run, and what resource requirements it has.
resource "aws_ecs_task_definition" "web_app" {
  family                   = "${local.web_app_ecs_service_name}"
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
    "name": "${local.web_app_ecs_service_name}",
    "cpu": ${local.task_cpu},
    "memory": ${local.task_memory},
    "image": "tomcat:latest",
    "essential": true,
    "portMappings": [
      {
        "containerPort": ${local.web_app_export_port}
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
        "sourceVolume": "${local.efs_docker_volumn_name}",
        "containerPath": "${local.efs_web_app_container_path}",
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
  # > docker run -v ${local.efs_mount_point}:/data/efs -it tomcat /bin/bash
  # we can see/edit the content of EFS by `cd /data/efs`.
  #
  # After we set up as this, if we `docker ps` and then
  # > docker exec -it <container-id> /bin/bash
  # `cd` to `var.efs_*_container_path` and add something, that will be saved in EFS.
  volume {
    # After this setup, if we `docker volume ls` we'll see the volume name (currently
    # driver is `local`).
    name      = "${local.efs_docker_volumn_name}"

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
    host_path = "${local.efs_mount_point}"

    # docker_volume_configuration {
    #   scope = "shared"
    #   autoprovision = true
    # }
  }

  # Seems no need to make it depends on `aws_launch_configuration.main`.
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
resource "aws_ecs_service" "web_app" {
  name            = "${local.web_app_ecs_service_name}"

  # No need to specify `iam_role` as we are using `awsvpc` network mode.
  # A service-linked role `AWSServiceRoleForECS` will be created automatically.
  # https://docs.aws.amazon.com/AmazonECS/latest/developerguide/using-service-linked-roles.html
  cluster         = "${aws_ecs_cluster.main.id}"
  task_definition = "${aws_ecs_task_definition.web_app.arn}"
  desired_count   = "${var.web_app_count}"
  launch_type     = "EC2"

  deployment_maximum_percent = 200
  deployment_minimum_healthy_percent = 75

  network_configuration {
    # `aws_security_group` is not how ECS decide which instance to place the task.
    # It is defined by `ordered_placement_strategy` and `placement_constraints`.
    #
    # TODO:
    # After setting up private subnets and NAT gateway, here should be replaced
    # by private subnet.
    # That may break SSH access defined in `aws_security_group.web_app` but
    # needs to double check.
    security_groups = ["${aws_security_group.web_app.id}"]
    subnets         = ["${aws_subnet.public.*.id}"]

    # `assign_public_ip = true` is not supported for this launch type
  }

  load_balancer {
    # TODO:
    # Container is currently defined inline inside of `aws_ecs_task_definition`
    # and it is using the service name as its name. We may choose a different
    # name (or at least names the local variables better) for
    # service/task_definition/container names.
    container_name   = "${local.web_app_ecs_service_name}"
    container_port   = "${local.web_app_export_port}"
    target_group_arn = "${aws_lb_target_group.web_app.id}"
  }

  # `binpack` is the most cost-effective, but it turns to have tasks cover less
  # available zones. Right now we are not doing "auto"-scaling so `spread` is fine.
  # After we do ASG of tasks this need to be revised.
  # https://docs.aws.amazon.com/AmazonECS/latest/developerguide/task-placement-strategies.html
  # https://aws.amazon.com/blogs/compute/amazon-ecs-task-placement/
  ordered_placement_strategy {
    type = "spread"
    field = "instanceId"
  }

  # May also setup `placement_constraints` to tell which kind of EC2 instances
  # (e.g. `instance-type`) this task can be placed.

  # TODO:
  # Current deployment is through rolling update (`ECS`). Consider to change
  # to blue/green (`CODE_DEPLOY`) deployment.
  # https://docs.aws.amazon.com/AmazonECS/latest/APIReference/API_DeploymentController.html
  deployment_controller {
    type = "ECS"
  }

  depends_on = [
    "aws_ecr_repository.web_app",
    "aws_lb_listener_rule.web_app"
  ]
}

resource "aws_ecs_task_definition" "web_static" {
  family                   = "${local.web_static_ecs_service_name}"
  network_mode             = "awsvpc"
  requires_compatibilities = ["EC2"]
  cpu                      = "${local.task_cpu}"
  memory                   = "${local.task_memory}"

  execution_role_arn       = "${data.aws_iam_role.ecs_task_execution.arn}"

  container_definitions = <<DEFINITION
[
  {
    "name": "${local.web_static_ecs_service_name}",
    "cpu": ${local.task_cpu},
    "memory": ${local.task_memory},
    "image": "nginx:latest",
    "essential": true,
    "portMappings": [
      {
        "containerPort": ${local.web_static_export_port}
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
    ]
  }
]
DEFINITION

}

resource "aws_ecs_service" "web_static" {
  name            = "${local.web_static_ecs_service_name}"

  cluster         = "${aws_ecs_cluster.main.id}"
  task_definition = "${aws_ecs_task_definition.web_static.arn}"
  desired_count   = "${var.web_static_count}"
  launch_type     = "EC2"

  deployment_maximum_percent = 200
  deployment_minimum_healthy_percent = 75

  network_configuration {
    security_groups = ["${aws_security_group.web_static.id}"]
    subnets         = ["${aws_subnet.public.*.id}"]
  }

  load_balancer {
    container_name   = "${local.web_static_ecs_service_name}"
    container_port   = "${local.web_static_export_port}"
    target_group_arn = "${aws_lb_target_group.web_static.id}"
  }

  ordered_placement_strategy {
    type = "spread"
    field = "instanceId"
  }

  deployment_controller {
    type = "ECS"
  }

  depends_on = [
    "aws_ecr_repository.web_static",
    "aws_lb_listener_rule.web_static"
  ]
}

resource "aws_ecs_task_definition" "git" {
  family                   = "${local.git_ecs_service_name}"
  network_mode             = "awsvpc"
  requires_compatibilities = ["EC2"]
  cpu                      = "${local.task_cpu}"
  memory                   = "${local.task_memory}"

  execution_role_arn       = "${data.aws_iam_role.ecs_task_execution.arn}"

  # The below image is basically ubuntu with sshd installed.
  # https://hub.docker.com/r/rastasheep/ubuntu-sshd
  #
  # In here we need an image with port 22 open. Otherwise image will fail
  # the NLB health check in `aws_lb_target_group.git`.
  container_definitions = <<DEFINITION
[
  {
    "name": "${local.git_ecs_service_name}",
    "cpu": ${local.task_cpu},
    "memory": ${local.task_memory},
    "image": "rastasheep/ubuntu-sshd:18.04",
    "essential": true,
    "portMappings": [
      {
        "containerPort": 22
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
        "sourceVolume": "${local.efs_docker_volumn_name}",
        "containerPath": "${local.efs_git_container_path}",
        "readOnly": false
      }
    ]
  }
]
DEFINITION

  volume {
    name      = "${local.efs_docker_volumn_name}"
    host_path = "${local.efs_mount_point}"
  }
}

# TODO:
# Deploying git image is much slower than web image (11min vs 5min for deployment
# process). May relate to the detail between ALB and NLB but I don't fully understand
# the reason.
resource "aws_ecs_service" "git" {
  name            = "${local.git_ecs_service_name}"

  cluster         = "${aws_ecs_cluster.main.id}"
  task_definition = "${aws_ecs_task_definition.git.arn}"
  desired_count   = "${var.git_count}"
  launch_type     = "EC2"

  deployment_maximum_percent = 200
  deployment_minimum_healthy_percent = 75

  network_configuration {
    security_groups = ["${aws_security_group.git.id}"]
    subnets         = ["${aws_subnet.public.*.id}"]
  }

  load_balancer {
    container_name   = "${local.git_ecs_service_name}"
    container_port   = 22
    target_group_arn = "${aws_lb_target_group.git.id}"
  }

  ordered_placement_strategy {
    type = "spread"
    field = "instanceId"
  }

  deployment_controller {
    type = "ECS"
  }

  depends_on = [
    "aws_ecr_repository.git",
    "aws_lb_listener.git_front_end"
  ]
}

resource "aws_ecs_cluster" "main" {
  name = "${local.ecs_cluster_name}"
}
