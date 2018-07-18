from urllib.parse import urlparse, urljoin

from testsuite.organization_created_testsuite import OrganizationCreatedTestSuite
from forms.authorization_form import fill_login_form
from forms.repository_management_form import (
    fill_create_repository_form
)


class TestRepositoryManagement(OrganizationCreatedTestSuite):

    def setUp(self):
        super(TestRepositoryManagement, self).setUp()

        self.repo_name = "another_repo"
        self.repo_display_name = "Another Repository"
        self.repo_description = "Another Repository Description"

    def tearDown(self):
        super(TestRepositoryManagement, self).tearDown()

    def test_organization_manager_create_public_repository_and_view_by_organization_member_and_non_member(self):
        self.driver.get(urljoin(self.root_url, "/organizations/{}".format(self.org_id)))
        assert self.repo_display_name not in self.driver.page_source

        # TODO:
        # Here proved that the organization manager can create repository.
        # Should do the more general case that any organizer member can
        # create repository.

        self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/create".format(self.org_id)))
        fill_create_repository_form(self.driver, self.repo_name, self.repo_display_name, self.repo_description)

        self.driver.get(urljoin(self.root_url, "/organizations/{}".format(self.org_id)))
        assert self.repo_display_name in self.driver.page_source
        assert "Public" in self.driver.page_source

        # TODO:
        # Check the repository page infomation.

        # TODO:
        # check the current user is a project organizer.

        self.driver.get(urljoin(self.root_url, "logout"))

        self.driver.get(urljoin(self.root_url, "login"))
        fill_login_form(self.driver, self.another_username, self.another_password)

        self.driver.get(urljoin(self.root_url, "/organizations/{}".format(self.org_id)))
        assert self.repo_display_name in self.driver.page_source
        assert "Public" in self.driver.page_source

    def test_organization_manager_create_private_repository_and_view_by_organization_member_and_non_member(self):
        self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/create".format(self.org_id)))
        fill_create_repository_form(self.driver, self.repo_name, self.repo_display_name, self.repo_description, is_public=False)

        self.driver.get(urljoin(self.root_url, "/organizations/{}".format(self.org_id)))
        assert "Private" in self.driver.page_source

        self.driver.get(urljoin(self.root_url, "logout"))

        self.driver.get(urljoin(self.root_url, "login"))
        fill_login_form(self.driver, self.another_username, self.another_password)

        self.driver.get(urljoin(self.root_url, "/organizations/{}".format(self.org_id)))
        assert self.repo_display_name not in self.driver.page_source

    def test_organization_member_create_repository(self):
        self.driver.get(urljoin(self.root_url, "organizations/{}/settings/members".format(self.org_id)))
        form_start = self.driver.find_element_by_id("username")
        form_start.send_keys(self.another_username)
        form_start.submit()

        self.driver.get(urljoin(self.root_url, "logout"))

        self.driver.get(urljoin(self.root_url, "login"))
        fill_login_form(self.driver, self.another_username, self.another_password)

        self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/create".format(self.org_id)))
        fill_create_repository_form(self.driver, self.repo_name, self.repo_display_name, self.repo_description)

        self.driver.get(urljoin(self.root_url, "/organizations/{}".format(self.org_id)))
        assert self.repo_display_name in self.driver.page_source

    def test_non_member_cannot_create_repository(self):
        self.driver.get(urljoin(self.root_url, "logout"))

        self.driver.get(urljoin(self.root_url, "login"))
        fill_login_form(self.driver, self.another_username, self.another_password)

        self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/create".format(self.org_id)))
        fill_create_repository_form(self.driver, self.repo_name, self.repo_display_name, self.repo_description)

        assert "status=403" in self.driver.page_source

    def test_create_repository_with_invalid_input(self):
        repo_name = "a"
        repo_display_name = "A"
        repo_description = "A"

        self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/create".format(self.org_id)))
        fill_create_repository_form(self.driver, repo_name, repo_display_name, repo_description)

        self.assertEqual(urlparse(self.driver.current_url).path, "/organizations/{}/repositories/create".format(self.org_id))
        assert "size" in self.driver.find_element_by_id("name.errors").text
        assert "size" in self.driver.find_element_by_id("displayName.errors").text
