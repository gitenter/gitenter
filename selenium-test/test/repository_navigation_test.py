from urllib.parse import urlparse, urljoin
import subprocess
import os
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
            file_path = str(local_path / add_to_git_file.relative_filepath)
            os.makedirs(os.path.dirname(file_path), exist_ok=True)
            with open(file_path, 'w') as f:
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

        # TODO:
        # Should later on aviod return code 500, but to catch the error and redirect
        # to 404 error properly.
        self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/{}/branches/master".format(self.org_id, self.repo_id)))
        assert "status=500" in self.driver.page_source
        assert "Branch master is not existing yet!" in self.driver.page_source
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
        assert "Turn off for Traceability Analysis" in self.driver.page_source

        self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/{}/branches/master/commits".format(self.org_id, self.repo_id)))
        assert self.repo_display_name in self.driver.page_source
        assert git_commit_datapack.commit_message in self.driver.page_source
        assert git_commit_datapack.username in self.driver.page_source

        commit_web_elements = self.driver.find_elements_by_class_name("commit-in-list")
        self.assertEqual(len(commit_web_elements), 1)
        commit_link = commit_web_elements[0].get_attribute("action")
        self.driver.get(commit_link)
        assert "Turn off for Traceability Analysis" in self.driver.page_source
        assert "Browse Historical Commits" not in self.driver.page_source

    def test_invalid_commit(self):
        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.org_member_username, self.org_member_password)

        git_commit_datapack = GitCommitDatapack("add commit setup file", self.org_member_username, self.org_member_email)
        git_commit_datapack.add_file(AddToGitFile("gitenter.properties", "enable_systemwide = on"))
        git_commit_datapack.add_file(AddToGitFile("file.md", "- [tag]{refer-not-exist} a traceable item."))

        local_path = self._clone_repo_and_return_local_path()
        self._commit_to_repo(git_commit_datapack, local_path)

        self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/{}".format(self.org_id, self.repo_id)))
        self.assertEqual(urlparse(self.driver.current_url).path, "/organizations/{}/repositories/{}/branches/master".format(self.org_id, self.repo_id))
        assert "Trace Analyzer Error" in self.driver.page_source
        assert "Browse Historical Commits" in self.driver.page_source

        self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/{}/branches/master/commits".format(self.org_id, self.repo_id)))
        assert self.repo_display_name in self.driver.page_source
        assert git_commit_datapack.commit_message in self.driver.page_source
        assert git_commit_datapack.username in self.driver.page_source

        commit_web_elements = self.driver.find_elements_by_class_name("commit-in-list")
        self.assertEqual(len(commit_web_elements), 1)
        commit_link = commit_web_elements[0].get_attribute("action")
        self.driver.get(commit_link)
        assert "Trace Analyzer Error" in self.driver.page_source
        assert "Browse Historical Commits" not in self.driver.page_source

    def test_valid_commit_one_file(self):
        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.org_member_username, self.org_member_password)

        git_commit_datapack = GitCommitDatapack("add commit setup file", self.org_member_username, self.org_member_email)
        git_commit_datapack.add_file(AddToGitFile("gitenter.properties", "enable_systemwide = on"))
        git_commit_datapack.add_file(AddToGitFile("file.md", "- [tag1] a traceable item.\n- [tag2]{tag1} another traceable item."))

        local_path = self._clone_repo_and_return_local_path()
        self._commit_to_repo(git_commit_datapack, local_path)

        self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/{}".format(self.org_id, self.repo_id)))
        self.assertEqual(urlparse(self.driver.current_url).path, "/organizations/{}/repositories/{}/branches/master".format(self.org_id, self.repo_id))
        assert "Browse files and folders" in self.driver.page_source
        self.assertEqual(self.driver.find_element_by_class_name("nav-current").text, "Branch: master")
        self.assertEqual(self.driver.find_element_by_class_name("document-file").get_attribute("value"), "file.md")
        self.assertEqual(self.driver.find_element_by_class_name("non-document-file").text, "gitenter.properties")

        document_link = self.driver.find_element_by_xpath("//input[@value='file.md']/parent::form").get_attribute("action")
        self.driver.get(document_link)
        self.assertEqual(self.driver.find_element_by_class_name("nav-current").text, "file.md")
        assert "a traceable item" in self.driver.page_source

        # TODO:
        # Navigate to there rather than hardcode the link.
        self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/{}/branches/master/commits".format(self.org_id, self.repo_id)))
        assert self.repo_display_name in self.driver.page_source
        assert git_commit_datapack.commit_message in self.driver.page_source
        assert git_commit_datapack.username in self.driver.page_source

        commit_link = self.driver.find_element_by_class_name("commit-in-list").get_attribute("action")
        self.driver.get(commit_link)
        assert "Browse files and folders" in self.driver.page_source
        self.assertTrue("Commit:" in self.driver.find_element_by_class_name("nav-current").text)
        self.assertEqual(self.driver.find_element_by_class_name("document-file").get_attribute("value"), "file.md")
        self.assertEqual(self.driver.find_element_by_class_name("non-document-file").text, "gitenter.properties")

        document_link = self.driver.find_element_by_xpath("//input[@value='file.md']/parent::form").get_attribute("action")
        self.driver.get(document_link)
        self.assertEqual(
            self.driver.find_element_by_xpath("//input[@value='tag1' and @class='original']/parent::form").get_attribute("action"),
            "{}#tag1".format(document_link))
        self.assertEqual(
            self.driver.find_element_by_xpath("//input[@value='tag2' and @class='downstream']/parent::form").get_attribute("action"),
            "{}#tag2".format(document_link))
        self.assertEqual(
            self.driver.find_element_by_xpath("//input[@value='tag2' and @class='original']/parent::form").get_attribute("action"),
            "{}#tag2".format(document_link))
        self.assertEqual(
            self.driver.find_element_by_xpath("//input[@value='tag1' and @class='upstream']/parent::form").get_attribute("action"),
            "{}#tag1".format(document_link))

    def test_valid_commit_two_files_in_root(self):
        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.org_member_username, self.org_member_password)

        git_commit_datapack = GitCommitDatapack("add commit setup file", self.org_member_username, self.org_member_email)
        git_commit_datapack.add_file(AddToGitFile("gitenter.properties", "enable_systemwide = on"))
        git_commit_datapack.add_file(AddToGitFile("file1.md", "- [tag1] a traceable item."))
        git_commit_datapack.add_file(AddToGitFile("file2.md", "- [tag2]{tag1} another traceable item."))

        local_path = self._clone_repo_and_return_local_path()
        self._commit_to_repo(git_commit_datapack, local_path)

        repo_link = urljoin(self.root_url, "/organizations/{}/repositories/{}".format(self.org_id, self.repo_id))

        self.driver.get(repo_link)
        document_link = self.driver.find_element_by_xpath("//input[@value='file1.md']/parent::form").get_attribute("action")
        self.driver.get(document_link)
        self.assertEqual(self.driver.find_element_by_class_name("nav-current").text, "file1.md")
        self.assertEqual(
            self.driver.find_element_by_xpath("//input[@value='tag1' and @class='original']/parent::form").get_attribute("action"),
            "{}#tag1".format(document_link))
        self.assertEqual(
            self.driver.find_element_by_xpath("//input[@value='tag2' and @class='downstream']/parent::form").get_attribute("action"),
            "{}/branches/master/documents/directories/file2.md#tag2".format(repo_link))

        self.driver.get(repo_link)
        document_link = self.driver.find_element_by_xpath("//input[@value='file2.md']/parent::form").get_attribute("action")
        self.driver.get(document_link)
        self.assertEqual(self.driver.find_element_by_class_name("nav-current").text, "file2.md")
        self.assertEqual(
            self.driver.find_element_by_xpath("//input[@value='tag2' and @class='original']/parent::form").get_attribute("action"),
            "{}#tag2".format(document_link))
        self.assertEqual(
            self.driver.find_element_by_xpath("//input[@value='tag1' and @class='upstream']/parent::form").get_attribute("action"),
            "{}/branches/master/documents/directories/file1.md#tag1".format(repo_link))

    def test_valid_commit_two_files_in_nested_folder(self):
        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.org_member_username, self.org_member_password)

        git_commit_datapack = GitCommitDatapack("add commit setup file", self.org_member_username, self.org_member_email)
        git_commit_datapack.add_file(AddToGitFile("gitenter.properties", "enable_systemwide = on"))
        git_commit_datapack.add_file(AddToGitFile("root-file.md", "- [tag1] a traceable item."))
        git_commit_datapack.add_file(AddToGitFile("nested-folder/nested-file.md", "- [tag2]{tag1} another traceable item."))

        local_path = self._clone_repo_and_return_local_path()
        self._commit_to_repo(git_commit_datapack, local_path)

        repo_link = urljoin(self.root_url, "/organizations/{}/repositories/{}".format(self.org_id, self.repo_id))

        self.driver.get(repo_link)
        document_link = self.driver.find_element_by_xpath("//input[@value='root-file.md']/parent::form").get_attribute("action")
        self.driver.get(document_link)
        self.assertEqual(self.driver.find_element_by_class_name("nav-current").text, "root-file.md")
        self.assertEqual(
            self.driver.find_element_by_xpath("//input[@value='tag1' and @class='original']/parent::form").get_attribute("action"),
            "{}#tag1".format(document_link))
        self.assertEqual(
            self.driver.find_element_by_xpath("//input[@value='tag2' and @class='downstream']/parent::form").get_attribute("action"),
            "{}/branches/master/documents/directories/nested-folder/nested-file.md#tag2".format(repo_link))

        self.driver.get(repo_link)
        document_link = self.driver.find_element_by_xpath("//input[@value='nested-file.md']/parent::form").get_attribute("action")
        self.driver.get(document_link)
        self.assertEqual(self.driver.find_element_by_class_name("nav-current").text, "nested-folder/nested-file.md")
        self.assertEqual(
            self.driver.find_element_by_xpath("//input[@value='tag2' and @class='original']/parent::form").get_attribute("action"),
            "{}#tag2".format(document_link))
        self.assertEqual(
            self.driver.find_element_by_xpath("//input[@value='tag1' and @class='upstream']/parent::form").get_attribute("action"),
            "{}/branches/master/documents/directories/root-file.md#tag1".format(repo_link))
