from pathlib import Path, PosixPath


class Profile(object):
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


class QaProfile(Profile):
    # TODO:
    # A way to query these URLs from AWS (we know the cluster/load balancer name)
    # rather than hardcode them in here.
    web_domain = "http://qa-web-alb-1949265169.us-east-1.elb.amazonaws.com/"
    git_server_remote_location = Path.home() / "Workspace" / "gitenter-test" / "local-git-server"
    local_git_sandbox_path = Path.home() / "Workspace" / "gitenter-test" / "sandbox"


profile = DockerProfile()
