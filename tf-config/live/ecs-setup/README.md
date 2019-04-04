## Fargate

- https://github.com/Oxalide/terraform-fargate-example/blob/master/main.tf

## Docker volume

- https://github.com/terraform-providers/terraform-provider-aws/issues/5523
- https://aws.amazon.com/about-aws/whats-new/2018/08/amazon-ecs-now-supports-docker-volume-and-volume-plugins/
- https://docs.aws.amazon.com/AmazonECS/latest/developerguide/using_data_volumes.html

(1) "Docker volumes" is supported by EC2 tasks:

- ECS will create the docker volume in `/var/lib/docker/volumes`.
- Use [Docker volume drivers](https://docs.docker.com/engine/extend/plugins_volume/) to link this volume to EBS/EFS. Default is local volume driver.

This is only available for 2018/08 so Docker volume for Fargate may be available soon.

(2) Bind mounts (mount host machine folder to container) is supported for both EC2 and Fargate tasks. For Fargate it is 4GB with [setup](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/fargate-task-storage.html). Can be shared by containers *(Not different services!?)*. Works when there are two `container_definitions` in `aws_ecs_task_definition`, one for tomcat and one for git. Non-persistent.

(3) Fargate may have 10G Docker layer storage. Non-persistent and cannot be shared by containers.

Sounds like:

1. We can use Fargate + bind mounts for testing. But everytime after deployment the storage is gone!? And that's almost the same as `aws_ecs_service.desired_count = 1` so for sure we can have an associated local file system.
2. We need to use EC2 tasks and/or use more manual way to attach EBS/EFS.
