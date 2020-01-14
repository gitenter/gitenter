from Crypto.PublicKey import RSA
from os.path import expanduser, join

from pathlib import Path, PosixPath


class Profile(object):

    def get_web_domain(self):
        return self.web_domain

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
    web_domain = "http://localhost:3000/"
    git_server_remote_location = Path.home() / "Workspace" / "gitenter-test" / "local-git-server"
    local_git_sandbox_path = Path.home() / "Workspace" / "gitenter-test" / "sandbox"

    # Only need a dummy solution for this, as in local test we bypasses
    # SSH authorization.
    def get_ssh_key(self):
        return self._generate_dummy_ssh_key()


class DockerProfile(Profile):
    web_domain = "http://nginx/"
    git_server_remote_location = "git"
    local_git_sandbox_path = Path.home() / "Workspace" / "gitenter-test" / "sandbox"

    def get_ssh_key(self):
        return self._get_ssh_key_from_local_file(join(expanduser("~"), ".ssh/id_rsa.pub"))


class StagingProfile(Profile):
    local_git_sandbox_path = Path.home() / "Workspace" / "gitenter-test" / "sandbox"
    web_domain = "http://staging.gitenter.com/"
    git_server_remote_location = "git.staging.gitenter.com"

    # TODO:
    # This is kind of a hack, depending on the CircleCI image implementation detail
    # and the operation(s) we defined in `.circleci/config.yml`. We should fine a
    # less fragile implementation.
    def get_ssh_key(self):
        return self._get_ssh_key_from_local_file("/home/circleci/.ssh/id_rsa.pub")


profile = LocalProfile()
