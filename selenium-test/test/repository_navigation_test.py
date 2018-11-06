from urllib.parse import urlparse, urljoin
import subprocess
import pygit2

from testsuite.repository_created_testsuite import RepositoryCreatedTestSuite
from forms.authorization_form import fill_login_form


class AddToGitFile:

    def __init__(self, relative_filepath, content):
        self.relative_filepath = relative_filepath
        self.content = content


class GitCommitDatapack:

    def __init__(self, commit_message, username, email):
        self.add_to_git_files = []
        self.commit_message = commit_message
        self.username = username
        self.email = email

    def add_file(self, file):
        self.add_to_git_files.append(file)


class TestRepositoryNavigation(RepositoryCreatedTestSuite):

    def setUp(self):
        super(TestRepositoryNavigation, self).setUp()

    def tearDown(self):
        super(TestRepositoryNavigation, self).tearDown()

    # TODO:
    # Consider move these methods and helper classes to `RepositoryCreatedTestSuite`.
    def _clone_repo_and_return_local_path(self):

        remote_git_url = self.config.git_server_remote_path / self.org_name / "{}.git".format(self.repo_name)
        local_path = self.config.sandbox_path / self.repo_name
        local_path.mkdir(mode=0o777, parents=False, exist_ok=False)

        pygit2.clone_repository("file://{}".format(str(remote_git_url)), str(local_path))

        return local_path

    def _commit_to_repo(self, git_commit_datapack, local_path):

        for add_to_git_file in git_commit_datapack.add_to_git_files:
            with open(str(local_path / add_to_git_file.relative_filepath), 'w') as f:
                f.write(add_to_git_file.content)

        repo = pygit2.Repository(str(local_path))
        # reference = "refs/heads/master"
        index = repo.index
        index.add_all()
        index.write()
        author = pygit2.Signature(git_commit_datapack.username, git_commit_datapack.email)
        commiter = pygit2.Signature(git_commit_datapack.username, git_commit_datapack.email)
        message = git_commit_datapack.commit_message
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
        push_process = subprocess.Popen(
            ["git", "push", "origin", "master"],
            cwd=str(local_path),
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE)
        output, errors = push_process.communicate()
        print("git push output:\n{}".format(output.decode('UTF-8')))
        print("git push errors:\n{}".format(errors.decode('UTF-8')))

    def test_repo_with_no_commit(self):
        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.org_member_username, self.org_member_password)

        self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/{}".format(self.org_id, self.repo_id)))
        assert "Setup a new repository" in self.driver.page_source

        # In this case, there is no commit yet. So there is no branch available
        # (branch "master" is not available yet.)
        #
        # TODO:
        # Should later on aviod return code 500, but to catch the error and redirect
        # to 404 error properly.
        self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/{}/branches/master".format(self.org_id, self.repo_id)))
        assert "status=404" in self.driver.page_source
        self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/{}/branches/master/commits".format(self.org_id, self.repo_id)))
        assert "status=500" in self.driver.page_source
        assert "Branch master is not existing yet!" in self.driver.page_source

    def test_ignored_commit(self):
        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.org_member_username, self.org_member_password)

        git_commit_datapack = GitCommitDatapack("add commit without setup file", self.org_member_username, self.org_member_email)
        git_commit_datapack.add_file(AddToGitFile("a_irrelevant_file.txt", "A irrelevant file"))

        local_path = self._clone_repo_and_return_local_path()
        self._commit_to_repo(git_commit_datapack, local_path)

        self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/{}".format(self.org_id, self.repo_id)))
        self.assertEqual(urlparse(self.driver.current_url).path, "/organizations/{}/repositories/{}/branches/master".format(self.org_id, self.repo_id))
        assert "This system is turned off for this commit." in self.driver.page_source

        self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/{}/branches/master/commits".format(self.org_id, self.repo_id)))
        assert self.repo_display_name in self.driver.page_source
        assert git_commit_datapack.commit_message in self.driver.page_source
        assert git_commit_datapack.username in self.driver.page_source

        self.driver.get(urljoin(self.root_url, "/logout"))
