from pathlib import Path


class Config():
    pass


class STSConfig(Config):
    web_root_url = "http://localhost:8887/"
    postgres_root_url = "http://localhost:5432/"
    git_server_root_path = Path.home() / "Workspace" / "gitenter-test" / "local-git-server"
    git_server_remote_path = Path.home() / "Workspace" / "gitenter-test" / "local-git-server"
    sandbox_path = Path.home() / "Workspace" / "gitenter-test" / "sandbox"
    ssh_server_patch = Path.home() / "Workspace" / "gitenter-test" / "local-git-server" / ".ssh"
