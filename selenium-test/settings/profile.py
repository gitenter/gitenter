import boto3

from pathlib import Path, PosixPath


class Profile(object):

    def __init__(self):
        if self.web_domain:
            self.__web_domain = self.web_domain
            return

        # Boto3 will automatically check `AWS_ACCESS_KEY_ID` and `AWS_SECRET_ACCESS_KEY` env variables
        # which match with the env variables setup in CircleCI.
        # https://boto3.amazonaws.com/v1/documentation/api/latest/guide/configuration.html#environment-variables
        if self.ecs_cluster_name and self.ecs_cluster_name:
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
            # Note that `pygit2.clone_repository` needs URL in a very special format.
            # pygit2:    git://github.com/libgit2/pygit2.git
            # git clone: git@github.com:libgit2/pygit2.git
            #            ssh://git@github.com:[port]/libgit2/pygit2.git
            # with a customized port, `git clone` only works for `ssh:` format,
            # while `pygit2.clone_repository` doesn't support that, with error
            # > _pygit2.GitError: invalid hex digit in length: 'SSH-'
            return "git://{}/home/git/{}/{}.git".format(
                self.git_server_remote_location, org_name, repo_name)


class LocalProfile(Profile):
    web_domain = "http://localhost:8080/"
    git_server_remote_location = Path.home() / "Workspace" / "gitenter-test" / "local-git-server"
    local_git_sandbox_path = Path.home() / "Workspace" / "gitenter-test" / "sandbox"


class DockerProfile(Profile):
    web_domain = "http://www.gitenter.local/"
    git_server_remote_location = "gitenter.local"
    local_git_sandbox_path = Path.home() / "Workspace" / "gitenter-test" / "sandbox"


class StagingProfile(Profile):
    git_server_remote_location = Path.home() / "Workspace" / "gitenter-test" / "local-git-server"
    local_git_sandbox_path = Path.home() / "Workspace" / "gitenter-test" / "sandbox"

    ecs_cluster_name = "staging-cluster"
    ecs_service_name = "staging-web-app-service"


profile = LocalProfile()
