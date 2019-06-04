resource "aws_db_subnet_group" "main" {
  # TODO:
  # The logic/reason to have private subnet is to put the not-internet-accessable
  # resources (e.g. database) to the private subnet. Therefore, finally we should
  # replace public subnet to private subnet.
  #
  # After we setup the private subnet, we may want both public and private
  # subnets so we can still psql connect from the outside. Therefore we are
  # still keeping the possibility for database initialization and/or debugging.
  #
  # Finally, we need to remove public subnet. Consider read-only database e.g.
  # StrongDM: https://www.strongdm.com/
  subnet_ids = ["${aws_subnet.public.*.id}"]
}

resource "aws_db_instance" "postgres" {
  identifier               = "${local.aws_postgres}"
  port                     = 5432

  engine                   = "postgres"
  engine_version           = "11.1"
  # license_model            = "Postgresql License"
  name                     = "gitenter"
  username                 = "postgres"
  password                 = "postgres"
  # option_group_name        = "default:postgres-11"
  # parameter_group_name     = "default.postgres11"
  deletion_protection      = false # TODO: to be changed in production
  skip_final_snapshot      = true # TODO: to be changed in production

  # availability_zone        = "us-east-1a"
  db_subnet_group_name     = "${aws_db_subnet_group.main.name}"
  vpc_security_group_ids   = ["${aws_security_group.postgres.id}"]
  publicly_accessible      = true

  instance_class           = "db.t2.micro"
  storage_type             = "gp2" # general purpose SSD
  allocated_storage        = 20 # gigabytes
  # TODO:
  # May consider changing to storage_type = "io1"` for production.
  # And then `allocated_storage` needs to be at least 100 and default `iops = 1000`
  # However, `gp2` with 20GB is also supported.
  multi_az                 = false
  # TODO:
  # `multi_az = true` works, and we should use it for production setup
  # But needs to execute in good internet connection otherwise timeout
  # or other weird errors.
  backup_retention_period  = 7   # in days
  # backup_window            = ?
  copy_tags_to_snapshot    = true

  # TODO:
  # May create `aws_kms_key` resource, and fill `kms_key_id` in here.
  # And if enabled, `storage_encrypted` is not needed.

  auto_minor_version_upgrade = true
  storage_encrypted        = false
}

# TODO:
#   # Alwaus get the handshake error, no matter whether it is in `aws_db_instance` or `null_resource`.
#   # > * aws_db_instance.postgres: timeout - last error: ssh: handshake failed: ssh: unable to
#   # > authenticate, attempted methods [none publickey], no supported methods remain
#
# In EC2 instance, the following attributes are needed. But there are not associated ones in db instance.
# > associate_public_ip_address = true
# > ey_name = "${aws_key_pair.terraform-seashore.key_name}"
#
# Probably it is just not the right way to do so, as it doesn't support more complicated things such as
# Liquibase anyway. Thinking about other ways to do deployment.
# Probably just let deployment script (e.g. Circle CI) to use `psql` command to call this db endpoint.
# > psql --host=... --port=5432 --username=postgres --password --dbname=gitenter -f to_be_executed.sql
#
# resource "null_resource" "db_setup" {
#   # runs after database and security group providing external access is created
#   depends_on = ["aws_db_instance.postgres", "aws_security_group.postgres"]
#
#   provisioner "file" {
#     source      = "../../../database/"
#     destination = "/tmp/database"
#   }
#
#   provisioner "local-exec" {
#     command = <<EOT
#                 psql -U postgres -h localhost -p 5432 -w -f create_users.sql
#                 psql -U postgres -h localhost -p 5432 -d gitenter -w -f initiate_database.sql
#                 psql -U postgres -h localhost -p 5432 -d gitenter -w -f privilege_control.sql
#                 psql -U postgres -h localhost -p 5432 -d gitenter -w -f alter_sequence.sql
#                 psql -U postgres -h localhost -p 5432 -c 'ALTER DATABASE gitenter OWNER TO gitenter;'
# EOT
#     working_dir = "/tmp/database"
#   }
# }
