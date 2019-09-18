import boto3
from Crypto.PublicKey import RSA
from os.path import expanduser, join

from pathlib import Path, PosixPath


class Profile(object):

    web_domain = None
    ecs_cluster_name = None
    ecs_service_name = None

    def __init__(self):
        if self.web_domain:
            self.__web_domain = self.web_domain
            return

        # Boto3 will automatically check `AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY` env variables
        # which match with the env variables setup in CircleCI.
        # https://boto3.amazonaws.com/v1/documentation/api/latest/guide/configuration.html#environment-variables
        if self.ecs_cluster_name and self.ecs_service_name:
            target_group_arn = boto3.client(
                'ecs').describe_services(
                cluster=self.ecs_cluster_name,
                services=[
                    self.ecs_service_name,
                ]
            )['services'][0]['loadBalancers'][0]['targetGroupArn']

            elbv2_client = boto3.client('elbv2')
            elb_arn = elbv2_client.describe_target_groups(
                TargetGroupArns=[
                    target_group_arn,
                ]
            )['TargetGroups'][0]['LoadBalancerArns'][0]
            elb_dns_name = elbv2_client.describe_load_balancers(
                LoadBalancerArns=[
                    elb_arn,
                ]
            )['LoadBalancers'][0]['DNSName']

            self.__web_domain = "http://{}".format(elb_dns_name)
            return

    def get_web_domain(self):
        return self.__web_domain

    def get_remote_git_url(self, org_name, repo_name):
        if type(self.git_server_remote_location) == PosixPath:
            remote_git_path = self.git_server_remote_location / org_name / "{}.git".format(repo_name)
            return "file://{}".format(str(remote_git_path))
        elif type(self.git_server_remote_location) == str:
            # May use the SSH protocol one if we need a customized port.
            return "git@{}:/home/git/{}/{}.git".format(
                self.git_server_remote_location, org_name, repo_name)
            # return "ssh://git@{}:22/{}/{}.git".format(
            #     self.git_server_remote_location, org_name, repo_name)
            #
            # TODO:
            # For some reason right now `git@{}:{}/{}.git` doesn't work if we go through SSH
            # forced command (therefore, `check_if_can_edit_repository.sh`). Error message:
            # > fatal: 'org/repo.git' does not appear to be a git repository
            # > fatal: Could not read from remote repository.
            # >
            # > Please make sure you have the correct access rights

    def _generate_dummy_ssh_key(self):
        key = RSA.generate(2048)
        return key.publickey().exportKey('OpenSSH').decode("utf-8")

    def _get_ssh_key_from_local_file(self, filepath):
        with open(filepath) as f:
            return f.read().strip()

    def get_ssh_key(self):
        raise NotImplementedError


class LocalProfile(Profile):
    web_domain = "http://localhost:8080/"
    git_server_remote_location = Path.home() / "Workspace" / "gitenter-test" / "local-git-server"
    local_git_sandbox_path = Path.home() / "Workspace" / "gitenter-test" / "sandbox"

    # Only need a dummy solution for this, as in local test we bypasses
    # SSH authorization.
    def get_ssh_key(self):
        return self._generate_dummy_ssh_key()


class DockerProfile(Profile):
    web_domain = "http://web:8080/"
    git_server_remote_location = "git"
    local_git_sandbox_path = Path.home() / "Workspace" / "gitenter-test" / "sandbox"

    def get_ssh_key(self):
        return self._get_ssh_key_from_local_file(join(expanduser("~"), ".ssh/id_rsa.pub"))


class StagingProfile(Profile):
    git_server_remote_location = Path.home() / "Workspace" / "gitenter-test" / "local-git-server"
    local_git_sandbox_path = Path.home() / "Workspace" / "gitenter-test" / "sandbox"

    ecs_cluster_name = "staging-cluster"
    ecs_service_name = "staging-web-app-service"

    # TODO:
    # This is kind of a hack, depending on the CircleCI image implementation detail
    # and the operation(s) we defined in `.circleci/config.yml`. We should fine a
    # less fragile implementation.
    def get_ssh_key(self):
        return self._get_ssh_key_from_local_file("/home/circleci/.ssh/id_rsa.pub")


profile = LocalProfile()
