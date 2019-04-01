variable "az_count" {
  default = "2"
}

resource "aws_vpc" "ecs" {
  cidr_block       = "10.0.0.0/16"
  assign_generated_ipv6_cidr_block = false
  # instance_tenancy = "dedicated"
  enable_dns_support = true
  enable_dns_hostnames = true

  tags = {
    Name = "ecs-vpc"
  }
}
