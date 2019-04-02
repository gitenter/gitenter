resource "aws_ecr_repository" "terraform-ecs" {
  name = "terraform-ecs-repository"
}

# TODO:
# Push images into ECR repository is through docker command `docker tag` and `docker push`
# in an environment with that docker image. It is a non-idempotent operation through command
# line. Not a duty through Terraform.
# `aws_ecr_repository.terraform-ecs.repository_url` is needed for this option.
#
# Consider create the image through an Amazon Linux machine, guided by
# https://docs.aws.amazon.com/AmazonECS/latest/developerguide/docker-basics.html

# Used by `docker push`
output "ecr_repository_url" {
  value = "${aws_ecr_repository.terraform-ecs.repository_url}"
}
