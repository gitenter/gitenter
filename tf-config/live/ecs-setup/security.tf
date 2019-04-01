resource "aws_security_group" "terraform-ecs" {
  name = "ecs-instances-default-cluster"

  # Inbound and outbound rules matches what is suggested by the development guide
  # and followed by the AWS console UI.
  # https://docs.aws.amazon.com/AmazonECS/latest/developerguide/get-set-up-for-amazon-ecs.html

  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # HTTP
  ingress {
    from_port = 80
    to_port = 80
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"] # May not be needed as the associated VPC is without an IPv6 CIDR block
  }

  # HTTPS
  ingress {
    from_port = 443
    to_port = 443
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }

  # SSH
  ingress {
    from_port = 22
    to_port = 22
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # TODO: `Custom IP` rather than `Anywhere`
    ipv6_cidr_blocks = ["::/0"]
  }

  vpc_id = "${aws_vpc.ecs.id}"
}
