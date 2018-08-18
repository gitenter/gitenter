from urllib.parse import urlparse, urljoin
import os
import subprocess
import pygit2

from testsuite.repository_created_testsuite import RepositoryCreatedTestSuite
from forms.authorization_form import fill_login_form


# !! Attention !!
# Need to restart the system every time to run RepositoryCreatedTestSuite.
# Otherwise Spring(?) try to be too smart to cache the file system, so it
# will not know that the folder has been reset.
class TestRepositoryNavigation(RepositoryCreatedTestSuite):

    def setUp(self):
        super(TestRepositoryNavigation, self).setUp()

    def tearDown(self):
        super(TestRepositoryNavigation, self).tearDown()

    def test_commit(self):
        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.org_member_username, self.org_member_password)

        self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/{}".format(self.org_id, self.repo_id)))
        assert "Setup a new repository" in self.driver.page_source

        # TODO:
        # Check commit list will display correctly when there's an empty git
        # repo with no commit at all.

        remote_git_url = self.config.git_server_remote_path / self.org_name / "{}.git".format(self.repo_name)
        local_path = self.config.sandbox_path / self.repo_name
        local_path.mkdir(mode=0o777, parents=False, exist_ok=False)

        pygit2.clone_repository("file://{}".format(str(remote_git_url)), str(local_path))

        with open(str(local_path / "README.md"), 'w') as f:
            f.write("readme")

        repo = pygit2.Repository(str(local_path))
        # reference = "refs/heads/master"
        index = repo.index
        index.add("README.md")
        index.write()
        author = pygit2.Signature(self.org_member_username, self.org_member_email)
        commiter = pygit2.Signature(self.org_member_username, self.org_member_email)
        message = "add README"
        tree = repo.TreeBuilder().write()
        if repo.head_is_unborn:
            parent = []
        else:
            parent = [repo.head.get_object().hex]
        repo.create_commit("HEAD", author, commiter, message, tree, parent)
        print(repo.remotes["origin"])
        repo.remotes["origin"].push(["refs/heads/master"])

        # TODO:
        # Currently the repo folder structure/commit list are not working yet.
        # because the server side post-receive hook is not included.

        self.driver.get(urljoin(self.root_url, "/logout"))
