from urllib.parse import urlparse, urljoin
import os
import subprocess
import pygit2

from testsuite.repository_created_testsuite import RepositoryCreatedTestSuite
from forms.authorization_form import fill_login_form


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
        index.add_all()
        index.write()
        author = pygit2.Signature(self.org_member_username, self.org_member_email)
        commiter = pygit2.Signature(self.org_member_username, self.org_member_email)
        message = "add README"
        tree = repo.index.write_tree()
        if repo.head_is_unborn:
            parent = []
        else:
            parent = [repo.head.get_object().hex]
        repo.create_commit("refs/heads/master", author, commiter, message, tree, parent)

        # pygit2/libgit2 doesn't support hooks (which get bypassed silently).
        # https://github.com/libgit2/libgit2/issues/964
        # https://github.com/libgit2/libgit2/pull/3824
        # repo.remotes["origin"].push(["refs/heads/master"])
        #
        # TODO:
        # Consider to replace pygit2 with the following libs, to see if they have
        # better support.
        # https://github.com/gitpython-developers/GitPython
        # https://github.com/dulwich/dulwich
        push_process = subprocess.Popen(["git", "push", "origin", "master"], cwd=str(local_path), stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        output, errors = push_process.communicate()
        print("git push output:\n{}".format(output.decode('UTF-8')))
        print("git push errors:\n{}".format(errors.decode('UTF-8')))

        self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/{}/branches/master/commits".format(self.org_id, self.repo_id)))
        assert self.repo_display_name in self.driver.page_source
        assert message in self.driver.page_source
        assert author.name in self.driver.page_source

        self.driver.get(urljoin(self.root_url, "/logout"))
