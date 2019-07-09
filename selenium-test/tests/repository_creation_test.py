from random import randint
from urllib.parse import urlparse, urljoin

from testsuites.repository_to_be_created_testsuite import RepositoryToBeCreatedTestSuite
from forms.authorization_form import login_as
from forms.repository_management_form import (
    fill_create_repository_form,
    fill_delete_repository_form
)


class TestRepositoryCreation(RepositoryToBeCreatedTestSuite):

    def setUp(self):
        super(TestRepositoryCreation, self).setUp()

        self.repo_name = "repo"
        self.repo_display_name = "A Repository"
        self.repo_description = "A Repository Description"

    def tearDown(self):
        super(TestRepositoryCreation, self).tearDown()

    def test_repository_organizer_create_public_repository_and_view_by_organization_member_and_non_member_and_delete(self):
        with login_as(self.driver, self.root_url, self.repo_organizer_username, self.repo_organizer_password):
            self.driver.get(urljoin(self.root_url, "/organizations/{}".format(self.org_id)))
            assert self.repo_display_name not in self.driver.page_source

            self.driver.get(urljoin(self.root_url, "/organizations/{}".format(self.org_id)))
            self.assertTrue(self.driver.find_elements_by_xpath("//form[@action='/organizations/{}/repositories/create']".format(self.org_id)))

            self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/create".format(self.org_id)))
            fill_create_repository_form(self.driver, self.repo_name, self.repo_display_name, self.repo_description)

            self.driver.get(urljoin(self.root_url, "/organizations/{}".format(self.org_id)))
            assert self.repo_display_name in self.driver.page_source
            assert "Public" in self.driver.page_source

            repo_link = self.driver.find_element_by_link_text(self.repo_display_name).get_attribute("href")
            repo_id = urlparse(repo_link).path.split("/")[-1]

            self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/{}".format(self.org_id, repo_id)))
            assert self.repo_display_name in self.driver.page_source
            assert "Setup a new repository" in self.driver.page_source

            # TODO:
            # check the current user is a project organizer.

            # Cannot create another repository with the same `repo_name`
            self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/create".format(self.org_id)))
            fill_create_repository_form(self.driver, self.repo_name, "Another Repo Display Name", "Another Repo Description")
            assert "status=500" in self.driver.page_source
            assert "Organizations can only have distinguishable repository names." in self.driver.page_source

        # Organization member can access
        with login_as(self.driver, self.root_url, self.org_member_username, self.org_member_password):
            self.driver.get(urljoin(self.root_url, "/organizations/{}".format(self.org_id)))
            assert self.repo_display_name in self.driver.page_source
            assert "Public" in self.driver.page_source

            self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/{}".format(self.org_id, repo_id)))
            assert self.repo_display_name in self.driver.page_source

            # TODO:
            # Should see an customized page saying that you can read, but this is a empty repository.
            # No need to talk about setup as the user cannot edit no matter what, and/or where to ask
            # to be added as an editor.

        # Non member can access
        with login_as(self.driver, self.root_url, self.username, self.password):
            self.driver.get(urljoin(self.root_url, "/organizations/{}".format(self.org_id)))
            assert self.repo_display_name in self.driver.page_source
            assert "Public" in self.driver.page_source

            self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/{}".format(self.org_id, repo_id)))
            assert self.repo_display_name in self.driver.page_source

            # TODO:
            # Should see an customized page saying that you can read, but this is a empty repository.
            # No need to talk about setup as the user cannot edit no matter what, and/or where to ask
            # to be added as an editor.

        # Delete repository
        with login_as(self.driver, self.root_url, self.repo_organizer_username, self.repo_organizer_password):
            self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/{}/settings/delete".format(self.org_id, repo_id)))
            fill_delete_repository_form(self.driver, "wrong_repo_name")
            self.assertEqual(urlparse(self.driver.current_url).path, "/organizations/{}/repositories/{}/settings/delete".format(self.org_id, repo_id))
            assert "Repository name doesn't match!" in self.driver.page_source

            self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/{}/settings/delete".format(self.org_id, repo_id)))
            fill_delete_repository_form(self.driver, self.repo_name)
            self.assertEqual(urlparse(self.driver.current_url).path, "/organizations/{}".format(self.org_id))
            assert self.repo_display_name not in self.driver.page_source

        # TODO:
        # May test e.g. repository collaborators cannot delete repository, etc...

    def test_repository_organizer_create_private_repository_and_view_by_organization_member_and_non_member(self):
        with login_as(self.driver, self.root_url, self.repo_organizer_username, self.repo_organizer_password):
            self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/create".format(self.org_id)))
            fill_create_repository_form(self.driver, self.repo_name, self.repo_display_name, self.repo_description, is_public=False)

            self.driver.get(urljoin(self.root_url, "/organizations/{}".format(self.org_id)))
            assert "Private" in self.driver.page_source

            repo_link = self.driver.find_element_by_link_text(self.repo_display_name).get_attribute("href")
            repo_id = urlparse(repo_link).path.split("/")[-1]

            self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/{}".format(self.org_id, repo_id)))
            assert self.repo_display_name in self.driver.page_source
            assert "Setup a new repository" in self.driver.page_source

        # Organization member can access
        with login_as(self.driver, self.root_url, self.org_member_username, self.org_member_password):
            self.driver.get(urljoin(self.root_url, "/organizations/{}".format(self.org_id)))
            assert self.repo_display_name in self.driver.page_source
            assert "Private" in self.driver.page_source

            self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/{}".format(self.org_id, repo_id)))
            assert self.repo_display_name in self.driver.page_source

            # TODO:
            # Should see an customized page saying that you can read, but this is a empty repository.
            # No need to talk about setup as the user cannot edit no matter what, and/or where to ask
            # to be added as an editor.

        # Non member cannot access
        with login_as(self.driver, self.root_url, self.username, self.password):
            self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/{}".format(self.org_id, repo_id)))
            assert self.repo_display_name not in self.driver.page_source
            assert "status=403" in self.driver.page_source

        # Delete repository
        with login_as(self.driver, self.root_url, self.repo_organizer_username, self.repo_organizer_password):
            self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/{}/settings/delete".format(self.org_id, repo_id)))
            fill_delete_repository_form(self.driver, self.repo_name)
            self.assertEqual(urlparse(self.driver.current_url).path, "/organizations/{}".format(self.org_id))
            assert self.repo_display_name not in self.driver.page_source

    def test_non_member_cannot_create_repository(self):
        with login_as(self.driver, self.root_url, self.username, self.password):
            self.driver.get(urljoin(self.root_url, "/organizations/{}".format(self.org_id)))
            self.assertFalse(self.driver.find_elements_by_xpath("//form[@action='/organizations/{}/repositories/create']".format(self.org_id)))

            self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/create".format(self.org_id)))
            fill_create_repository_form(self.driver, self.repo_name, self.repo_display_name, self.repo_description)
            assert "status=403" in self.driver.page_source

    def test_create_repository_with_invalid_input(self):
        repo_name = "a"
        repo_display_name = "A"
        repo_description = "A"

        with login_as(self.driver, self.root_url, self.repo_organizer_username, self.repo_organizer_password):
            self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/create".format(self.org_id)))
            fill_create_repository_form(self.driver, repo_name, repo_display_name, repo_description)

            self.assertEqual(urlparse(self.driver.current_url).path, "/organizations/{}/repositories/create".format(self.org_id))
            assert "size" in self.driver.find_element_by_id("name.errors").text
            assert "size" in self.driver.find_element_by_id("displayName.errors").text
