References:

- https://github.com/terraform-providers/terraform-provider-aws/issues/5523
- https://aws.amazon.com/about-aws/whats-new/2018/08/amazon-ecs-now-supports-docker-volume-and-volume-plugins/
- https://docs.aws.amazon.com/AmazonECS/latest/developerguide/using_data_volumes.html

Fargate tasks only support nonpersistent storage volumes. Sounds like we need to use EC2 tasks and/or use more manual way to attach EBS/EFS.

"Docker volumes" is supported by EC2 tasks:

- ECS will create the docker volume in `/var/lib/docker/volumes`
- Use [Docker volume drivers](https://docs.docker.com/engine/extend/plugins_volume/) to link this volume to EBS/EFS. Default is local volume driver

May also consider Bind mounts (mount host machine folder to container). Supported for both EC2 and Fargate tasks. *(Wonder then why it is said Fargate only support nonpersistent storage? Because it can only bound to machine volume but not EBS/EFS?)*
