# AWS

AWS account ID: 662490392829

AWS console sign in page: https://gitenter.signin.aws.amazon.com/console

Manually created a user `Administrator` with permissions `AdministratorAccess`. The `access_key` and `secret_key` of this user is used to setup users in `/iam`.

`iam-terraform-config` user is used for most terraform setup.

`iam-terraform-deploy` user is used for CircleCI deployment of `aws ecs update-service`.

# Terraform

Executed each single folder under `live`

```
terraform init
terraform get -update
terraform plan
terraform apply
terraform destroy
```

`terraform get -update` will (sometimes?) get the following error.

```
Error loading modules: error downloading 'file:///.../gitenter/tf-config/modules/iam-group-terraform-config': destination exists and is not a symlink
```

The alternative solution is to remove `.terraform/modules` so it gets force updated.

If we are later on using Terragrunt [we can `terragrunt apply --terragrunt-source path/to/the/module`](https://github.com/gruntwork-io/terragrunt#working-locally).

# Debugging Tips

Access website: using `alb_hostname`.

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

Connect to Postgres:

```
psql --host=ecs-circleci-qa-postgres.cqx7dy9nh94t.us-east-1.rds.amazonaws.com --port=5432 --username=postgres --password --dbname=gitenter
```
