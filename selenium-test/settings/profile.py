from pathlib import Path, PosixPath


class Profile(object):
    def get_remote_git_url(self, org_name, repo_name):
        if type(self.git_server_remote_location) == PosixPath:
            remote_git_path = self.git_server_remote_location / org_name / "{}.git".format(repo_name)
            return "file://{}".format(str(remote_git_path))
        elif type(self.git_server_remote_location) == str:
            return "ssh://git@{}/home/git/{}/{}.git".format(
                self.git_server_remote_location, org_name, repo_name)


class LocalProfile(Profile):
    web_domain = "http://localhost:8080/"
    git_server_remote_location = Path.home() / "Workspace" / "gitenter-test" / "local-git-server"
    local_git_sandbox_path = Path.home() / "Workspace" / "gitenter-test" / "sandbox"


class DockerProfile(Profile):
    web_domain = "http://localhost:8886/"
    git_server_remote_location = "0.0.0.0:8822"
    local_git_sandbox_path = Path.home() / "Workspace" / "gitenter-test" / "sandbox"


class QaProfile(Profile):
    # TODO:
    # A way to query these URLs from AWS (we know the cluster/load balancer name)
    # rather than hardcode them in here.
    web_domain = "http://qa-web-alb-1949265169.us-east-1.elb.amazonaws.com/"
    git_server_remote_location = Path.home() / "Workspace" / "gitenter-test" / "local-git-server"
    local_git_sandbox_path = Path.home() / "Workspace" / "gitenter-test" / "sandbox"


profile = LocalProfile()
