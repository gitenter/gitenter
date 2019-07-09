from pathlib import Path


class Profile(object):
    pass


class LocalProfile(Profile):
    web_root_url = "http://localhost:8080/"
    git_server_remote_path = Path.home() / "Workspace" / "gitenter-test" / "local-git-server"
    local_git_sandbox_path = Path.home() / "Workspace" / "gitenter-test" / "sandbox"


profile = LocalProfile()
