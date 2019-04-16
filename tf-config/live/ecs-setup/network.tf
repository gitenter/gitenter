variable "az_count" {
  # For both a private and a public subnets, this number needs
  # to be <=128 (65536/(256*2)=128), otherwise the subnet
  # `cidr_block` will be out of range.
  default = "2"
}

# Fetch AZs in the current region
data "aws_availability_zones" "available" {}

# Several options are provided for a user-defined VPC:
# (1) VPC with a single public subnet
# (2) VPC with public and private subnets
# (3) More complicated cases
# https://docs.aws.amazon.com/vpc/latest/userguide/VPC_Scenarios.html
#
# For chosen (2) it will use private subnet we needs to use NAT gateway
# (`aws_nat_gateway` Terraform resource), with the charging rate
# $400/year plus data.
# However, to hold RDS instance in user-defined VPC we need
# at least two subnets, each in a separate availability zone.
# https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_SettingUp.html#CHAP_SettingUp.Requirements
# https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/USER_VPC.html

resource "aws_vpc" "main" {
  # Private IPv4 address ranges by RFC 1918.
  # Netmask can be `/16` or smaller. The largest chosen range includes:
  # - 10.0.0.0/8
  # - 172.16.0.0/12
  # - 192.168.0.0/16
  # https://docs.aws.amazon.com/vpc/latest/userguide/VPC_Subnets.html#VPC_Sizing
  # https://en.wikipedia.org/wiki/Private_network#Private_IPv4_address_spaces
  cidr_block       = "10.0.0.0/16" # 65536 available addresses

  assign_generated_ipv6_cidr_block = false
  enable_dns_support = true
  enable_dns_hostnames = true

  # Decide whether instances launched into your VPC are run on shared or
  # dedicated hardware. Choose "default" or "dedicated". Dedicated
  # tenancy incurs additional costs.
  # https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/dedicated-instance.html
  instance_tenancy = "default"

  tags = {
    Name = "terraform-ecs-vpc"
  }
}

resource "aws_subnet" "public" {
  vpc_id                  = "${aws_vpc.main.id}"
  map_public_ip_on_launch = true

  # If two AZs:
  # count.index=0: `10.0.0.0/24` - 256 addresses
  # count.index=1: `10.0.1.0/24`
  # https://www.terraform.io/docs/configuration/functions/cidrsubnet.html
  count             = "${var.az_count}"
  cidr_block        = "${cidrsubnet(aws_vpc.main.cidr_block, 8, count.index)}"
  # Therefore, subnets will be each in a separate availability zone.
  availability_zone = "${data.aws_availability_zones.available.names[count.index]}"
}

# TODO:
# May disable private subnets/NAT gateway to reduce cost for staging machines.
resource "aws_subnet" "private" {
  vpc_id            = "${aws_vpc.main.id}"

  # If two AZs:
  # count.index=0: `10.0.2.0/24`
  # count.index=1: `10.0.3.0/24`
  count             = "${var.az_count}"
  cidr_block        = "${cidrsubnet(aws_vpc.main.cidr_block, 8, var.az_count + count.index)}"
  availability_zone = "${data.aws_availability_zones.available.names[count.index]}"
}

# IGW for the public subnet
resource "aws_internet_gateway" "gw" {
  vpc_id = "${aws_vpc.main.id}"
}

# Route the public subnet trafic through the IGW
resource "aws_route" "internet_access" {
  route_table_id         = "${aws_vpc.main.main_route_table_id}"
  gateway_id             = "${aws_internet_gateway.gw.id}"

  destination_cidr_block = "0.0.0.0/0"
}

# Create a NAT gateway with an EIP for each private subnet to get internet connectivity
resource "aws_eip" "gw" {
  count      = "${var.az_count}"
  vpc        = true
  depends_on = ["aws_internet_gateway.gw"]
}

# TODO:
# `NatGateway-Hours` causes costs outside of free-tier.
# We probably want to investigate if anything is setting up in the
# not-the-most-popular way, and/or do some cost management.
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
