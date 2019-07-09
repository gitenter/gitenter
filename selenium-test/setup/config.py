from pathlib import Path


class Config():
    pass


class STSConfig(Config):
    web_root_url = "http://localhost:8080/"
    git_server_remote_path = Path.home() / "Workspace" / "gitenter-test" / "local-git-server"
    local_git_sandbox_path = Path.home() / "Workspace" / "gitenter-test" / "sandbox"
