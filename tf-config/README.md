# AWS

AWS account ID: 662490392829

AWS console sign in page: https://gitenter.signin.aws.amazon.com/console

Manually created a user `Administrator` with permissions `AdministratorAccess`. The `access_key` and `secret_key` of this user is used to setup users in `/iam`.

`iam-terraform-config` user is used for most terraform setup.

`iam-terraform-deploy` user is used for CircleCI deployment of `aws ecs update-service`.

# Terraform Notes

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

# Initialization/Destroy

### Initialization

Needs to initialize the remote AWS state manually before `qa-readiness` CircleCI step.

```bash
cd ~/Workspace/gitenter/tf-config/live/ecs-circleci-qa
terraform apply
```

```bash
POSTGRES_ENDPOINT="ecs-circleci-qa-postgres.cqx7dy9nh94t.us-east-1.rds.amazonaws.com"

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

### Destroy

```bash
cd ~/Workspace/gitenter/tf-config/live/ecs-circleci-qa
terraform destroy
```

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
