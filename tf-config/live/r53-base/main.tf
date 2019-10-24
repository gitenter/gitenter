terraform {
  required_version = "> 0.11.12"
}

provider "aws" {
  access_key = "${var.access_key}"
  secret_key = "${var.secret_key}"
  region = "us-east-1"
}

# Add nameservers (NS) to GoDaddy. Then GoDaddy will complain
# > We can't display your DNS information because your nameservers aren't managed by us.
# and all customer setups disappear.
#
# No need to create route53 zones for different environments, as we can route
# them to different subdomains through "resource record sets".
#
# Also, please note the NS and SOA are changing if one recreate the route53 zone,
# and that needs to be updated in the GoDaddy side.
resource "aws_route53_zone" "main" {
  name = "gitenter.com"
}
