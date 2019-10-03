# AWS

AWS account ID: 662490392829

AWS console sign in page: https://gitenter.signin.aws.amazon.com/console

Manually created a user `Administrator` with permissions `AdministratorAccess`. The `access_key` and `secret_key` of this user is used to setup users in `/iam`.

Put in `tf-config/live/*/secret.auto.tfvars`.

`iam-terraform-config` user is used for most terraform setup.

`iam-terraform-deploy` user is used for CircleCI deployment of `aws ecs update-service`.

# Environments

+ `test`: In orchestration framework, but uses non-persistent storage (as docker containers) and mocked APIs.
+ `staging`: Everything is the same as prod. Uses different set of persistent storage/3rd party APIs. Deployed through `tf-config/live/ecs-circleci-staging` module.
+ `production`

# Initialization/Destroy

### Initialization

Needs to initialize the remote AWS state manually before `staging-readiness` CircleCI step.

```bash
cd ~/Workspace/gitenter/tf-config/live/ecs-circleci-staging
terraform apply
```

```bash
POSTGRES_ENDPOINT="staging-postgres.cqx7dy9nh94t.us-east-1.rds.amazonaws.com"

cd ~/Workspace/gitenter/
PGPASSWORD=postgres psql -U postgres -h $POSTGRES_ENDPOINT -p 5432 -w -f database/create_users.sql

# No need to create database, as it is already created in Terraform
# psql -U postgres -h $POSTGRES_ENDPOINT -p 5432 -w -f database/create_database.sql -v dbname=gitenter

PGPASSWORD=postgres psql -U postgres -h $POSTGRES_ENDPOINT -p 5432 -d gitenter -w -f database/initiate_database.sql
PGPASSWORD=postgres psql -U postgres -h $POSTGRES_ENDPOINT -p 5432 -d gitenter -w -f database/privilege_control.sql
PGPASSWORD=postgres psql -U postgres -h $POSTGRES_ENDPOINT -p 5432 -d gitenter -w -f database/alter_sequence.sql
PGPASSWORD=postgres psql -U postgres -h $POSTGRES_ENDPOINT -p 5432 -c 'ALTER DATABASE gitenter OWNER TO gitenter;'
```

Notes:

- `psql` commands are not idempotent. It can be only executed once each `terraform destroy/apply` cycle.
- Looks like no easy way to execute this part inside of Terraform definition -- https://stackoverflow.com/questions/14384849/is-there-a-way-to-run-initial-sql-when-creating-an-rds-database-instance-using-c
- TODO: May consider just create db user `gitenter` in Terraform, so no need for this step.
- TODO: Probably for QA the database should be in docker container rather than RDS? Therefore it can be reset every time. But that will cause difference between QA/prod to make QA less informative.

### Destroy

```bash
cd ~/Workspace/gitenter/tf-config/live/ecs-circleci-staging
terraform destroy
```

# Debugging Tips

## Access website

Using `web_lb_hostname`.

## Debug inside of the EC2 instance

Log into EC2 machines (Amazon Linux 2 docker containers for EC2 launch type): `ssh ec2-user@<ip-address>`.

Log into a docker container:

```
$ docker ps
$ docker exec -it <container-id> /bin/bash
root@ip-10-0-0-50:/usr/local/tomcat# java --version
openjdk 11.0.3 2019-04-16
OpenJDK Runtime Environment (build 11.0.3+1-Debian-1bpo91)
OpenJDK 64-Bit Server VM (build 11.0.3+1-Debian-1bpo91, mixed mode, sharing)
```

Then we can check tomcat log inside of the container.

```
cd logs
cat catalina.2019-05-22.log
```

Inside the machine we may add packages, so we can check the connections to outside of this container. E.g.

```
amazon-linux-extras list
sudo amazon-linux-extras install -y postgresql10
```

Connect to Postgres (both local and inside of the container):

```
psql --host=staging-postgres.cqx7dy9nh94t.us-east-1.rds.amazonaws.com --port=5432 --username=postgres --password --dbname=gitenter
```

Install `redis-cli`:

```
sudo yum install gcc
sudo yum install wget
wget http://download.redis.io/redis-stable.tar.gz && tar xvzf redis-stable.tar.gz && cd redis-stable && make && sudo cp src/redis-cli /usr/local/bin/ && sudo chmod 755 /usr/local/bin/redis-cli
```

Connect to Redis (inside of the container):

```
redis-cli -h staging-redis-sess.vf1dmm.ng.0001.use1.cache.amazonaws.com
staging-redis-sess.vf1dmm.ng.0001.use1.cache.amazonaws.com:6379> KEYS *
(empty list or set)
staging-redis-sess.vf1dmm.ng.0001.use1.cache.amazonaws.com:6379> SET a 1
OK
staging-redis-sess.vf1dmm.ng.0001.use1.cache.amazonaws.com:6379> GET a
"1"
```

## Pulling the ECR image locally and debug

```
aws configure
aws ecr get-login --region us-east-1 --no-include-email
docker login -u AWS -p ... https://662490392829.dkr.ecr.us-east-1.amazonaws.com
docker run -p 8885:8080 662490392829.dkr.ecr.us-east-1.amazonaws.com/ecs-circleci-qa-repository:c1f58a2c852d24b22bc9b12f137fb1fbd2d16a5f
```

# Terraform Notes

Full cycle: executed each single folder under `live`

```
terraform init
terraform get -update
terraform plan
terraform apply
terraform destroy
```

To accelerate the debugging process, we can only apply to certain resource and dependencies.

```
terraform plan -target=aws_efs_mount_target.git
terraform apply -target=aws_efs_mount_target.git
terraform destroy
```

(When destroying, it may complain that the `output` doesn't exist yet. It is fine as far as all the relevant resources has been successfully destroyed.)

For local resource modules, they'll be automatically loaded again every time, so there's no need for `terraform get -update` (we do need to `terraform get` the first time).

`terraform get -update` will (sometimes?) get the following error.

```
Error loading modules: error downloading 'file:///.../gitenter/tf-config/modules/iam-terraform-config-group': destination exists and is not a symlink
```

The alternative solution is to remove `.terraform/modules` so it gets force updated.

If we are later on using Terragrunt [we can `terragrunt apply --terragrunt-source path/to/the/module`](https://github.com/gruntwork-io/terragrunt#working-locally).

# AWS Random Notes

## Docker volume

- https://github.com/terraform-providers/terraform-provider-aws/issues/5523
- https://aws.amazon.com/about-aws/whats-new/2018/08/amazon-ecs-now-supports-docker-volume-and-volume-plugins/
- https://docs.aws.amazon.com/AmazonECS/latest/developerguide/using_data_volumes.html

(1) "Docker volumes" is supported by EC2 tasks:

- ECS will create the docker volume in `/var/lib/docker/volumes`.
- Use [Docker volume drivers](https://docs.docker.com/engine/extend/plugins_volume/) to link this volume to EBS/EFS. Default is local volume driver.
  - Looks like we can only use EFS (which can be mounted to multiple EC2 instances), as EBS can only mount to one single EC2 instance. (differences between S3/EBS/EFS [1](https://dzone.com/articles/confused-by-aws-storage-options-s3-ebs-amp-efs-explained) [2](https://www.cloudberrylab.com/resources/blog/amazon-s3-vs-ebs-vs-efs/))

This is only available for 2018/08 so Docker volume for Fargate may be available soon.

(2) Bind mounts (mount host machine folder to container) is supported for both EC2 and Fargate tasks. For Fargate it is 4GB with [setup](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/fargate-task-storage.html). Can be shared by containers *(Not different services!?)*. Works when there are two `container_definitions` in `aws_ecs_task_definition`, one for tomcat and one for git. Non-persistent.

(3) Fargate may have 10G Docker layer storage. Non-persistent and cannot be shared by containers.

Sounds like:

1. We can use Fargate + bind mounts for testing. But everytime after deployment the storage is gone!? And that's almost the same as `aws_ecs_service.desired_count = 1` so for sure we can have an associated local file system.
2. We need to use EC2 tasks and/or use more manual way to attach EBS/EFS.
