# Can be connected by
# psql --host=${aws_db_instance.postgres.address} --port=5432 --username=postgres --password --dbname=gitenter

terraform {
  required_version = "> 0.11.12"
}

provider "aws" {
  access_key = "${var.access_key}"
  secret_key = "${var.secret_key}"
  region = "us-east-1"
}

# vvvvvvvvv Same as /live/ecs-setup/network.tf vvvvvvvvvv

variable "az_count" {
  default = "2"
}

# Fetch AZs in the current region
data "aws_availability_zones" "available" {}

resource "aws_vpc" "main" {
  cidr_block       = "172.17.0.0/16"
  assign_generated_ipv6_cidr_block = false
  # instance_tenancy = "dedicated"
  enable_dns_support = true
  enable_dns_hostnames = true

  tags = {
    Name = "terraform-rds"
  }

  # TODO:
  # User-defined VPC needs to satisfy multiple conditions
  # in order to hold RDS instance:
  # at least two subnets, each in a separate availability zone
  # https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_SettingUp.html#CHAP_SettingUp.Requirements
  # https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/USER_VPC.html
}

# Create var.az_count private subnets, each in a different AZ
resource "aws_subnet" "private" {
  count             = "${var.az_count}"
  cidr_block        = "${cidrsubnet(aws_vpc.main.cidr_block, 8, count.index)}"
  availability_zone = "${data.aws_availability_zones.available.names[count.index]}"
  vpc_id            = "${aws_vpc.main.id}"
}

# Create var.az_count public subnets, each in a different AZ
resource "aws_subnet" "public" {
  count                   = "${var.az_count}"
  cidr_block              = "${cidrsubnet(aws_vpc.main.cidr_block, 8, var.az_count + count.index)}"
  availability_zone       = "${data.aws_availability_zones.available.names[count.index]}"
  vpc_id                  = "${aws_vpc.main.id}"
  map_public_ip_on_launch = true
}

# IGW for the public subnet
resource "aws_internet_gateway" "gw" {
  vpc_id = "${aws_vpc.main.id}"
}

# Route the public subnet trafic through the IGW
resource "aws_route" "internet_access" {
  route_table_id         = "${aws_vpc.main.main_route_table_id}"
  destination_cidr_block = "0.0.0.0/0"
  gateway_id             = "${aws_internet_gateway.gw.id}"
}

# Create a NAT gateway with an EIP for each private subnet to get internet connectivity
resource "aws_eip" "gw" {
  count      = "${var.az_count}"
  vpc        = true
  depends_on = ["aws_internet_gateway.gw"]
}

resource "aws_nat_gateway" "gw" {
  count         = "${var.az_count}"
  subnet_id     = "${element(aws_subnet.public.*.id, count.index)}"
  allocation_id = "${element(aws_eip.gw.*.id, count.index)}"
}

# Create a new route table for the private subnets, make it route non-local traffic through the NAT gateway to the internet
resource "aws_route_table" "private" {
  count  = "${var.az_count}"
  vpc_id = "${aws_vpc.main.id}"

  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = "${element(aws_nat_gateway.gw.*.id, count.index)}"
  }
}

# Explicitly associate the newly created route tables to the private subnets (so they don't default to the main route table)
resource "aws_route_table_association" "private" {
  count          = "${var.az_count}"
  subnet_id      = "${element(aws_subnet.private.*.id, count.index)}"
  route_table_id = "${element(aws_route_table.private.*.id, count.index)}"
}

# ^^^^^^^^^^ Same as /live/ecs-setup/network.tf ^^^^^^^^^^

resource "aws_key_pair" "terraform-seashore" {
  key_name   = "terraform-key_pair-seashore"
  public_key = "${file("~/.ssh/id_rsa.pub")}"
}

# ^^^^^^^^^^ Same as /live/ecs-setup/ssh.tf ^^^^^^^^^^

resource "aws_db_subnet_group" "main" {
  name       = "terraform-main"
  # TODO:
  # The logic/reason to have private subnet is to put the
  # not-internet-accessable resources (e.g. database) to
  # the private subnet. Therefore, we should finally remove
  # the public subnet IDs in here.
  #
  # Although by doing so, we need an alternative way to deploy
  # Postgres initialization into the database and/or debugging
  # database status.
  # Consider e.g. StrongDM: https://www.strongdm.com/
  subnet_ids = ["${aws_subnet.private.*.id}", "${aws_subnet.public.*.id}"]

  tags = {
    Name = "Terraform main subnet group"
  }
}

resource "aws_security_group" "postgres" {
  name = "terraform-postgres"
  vpc_id = "${aws_vpc.main.id}"

  # Inbound and outbound rules matches what is suggested by the development guide
  # and followed by the AWS console UI.
  # https://docs.aws.amazon.com/AmazonECS/latest/developerguide/get-set-up-for-amazon-ecs.html

  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Postgres
  ingress {
    from_port = 5432
    to_port = 5432
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }

  # # SSH
  # ingress {
  #   from_port = 22
  #   to_port = 22
  #   protocol = "tcp"
  #   cidr_blocks = ["0.0.0.0/0"] # TODO: `Custom IP` rather than `Anywhere`
  #   ipv6_cidr_blocks = ["::/0"]
  # }
}

resource "aws_db_instance" "postgres" {
  identifier               = "terraform-gitenter"
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

output "postgres_endpoint" {
  value = "${aws_db_instance.postgres.endpoint}"
}
