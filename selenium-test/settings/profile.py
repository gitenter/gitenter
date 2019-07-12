from pathlib import Path


class Profile(object):
    pass


class LocalProfile(Profile):
    web_root_url = "http://localhost:8080/"
    git_server_remote_path = Path.home() / "Workspace" / "gitenter-test" / "local-git-server"
    local_git_sandbox_path = Path.home() / "Workspace" / "gitenter-test" / "sandbox"

class QaProfile(Profile):
    # TODO:
    # A way to query these URLs from AWS (we know the cluster/load balancer name)
    # rather than hardcode them in here.
    web_root_url = "http://qa-web-alb-1949265169.us-east-1.elb.amazonaws.com/"
    git_server_remote_path = Path.home() / "Workspace" / "gitenter-test" / "local-git-server"
    local_git_sandbox_path = Path.home() / "Workspace" / "gitenter-test" / "sandbox"


profile = LocalProfile()
