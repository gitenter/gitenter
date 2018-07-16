from urllib.parse import urlparse, urljoin

from testsuite.organization_created_testsuite import OrganizationCreatedTestSuite
from forms.authorization_form import fill_login_form
from forms.repository_management_form import (
    fill_create_repository_form
)


class TestRepositoryManagement(OrganizationCreatedTestSuite):

    def setUp(self):
        super(TestRepositoryManagement, self).setUp()

    def tearDown(self):
        super(TestRepositoryManagement, self).tearDown()

    def test_create_public_repository_and_view_by_organization_member_and_non_member(self):
        repo_name = "another_repo"
        repo_display_name = "Another Repository"
        repo_description = "Another Repository Description"

        self.driver.get(urljoin(self.root_url, "/organizations/{}".format(self.org_id)))
        assert repo_display_name not in self.driver.page_source

        # TODO:
        # Here proved that the organization manager can create repository.
        # Should do the more general case that any organizer member can
        # create repository.

        self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/create".format(self.org_id)))
        fill_create_repository_form(self.driver, repo_name, repo_display_name, repo_description)

        self.driver.get(urljoin(self.root_url, "/organizations/{}".format(self.org_id)))
        assert repo_display_name in self.driver.page_source
        assert "Public" in self.driver.page_source

        # TODO:
        # Check the repository page infomation.

        # TODO:
        # check the current user is a project organizer.

        self.driver.get(urljoin(self.root_url, "logout"))

        self.driver.get(urljoin(self.root_url, "login"))
        fill_login_form(self.driver, self.another_username, self.another_password)

        self.driver.get(urljoin(self.root_url, "/organizations/{}".format(self.org_id)))
        assert repo_display_name in self.driver.page_source
        assert "Public" in self.driver.page_source

    def test_create_private_repository_and_view_by_organization_member_and_non_member(self):
        repo_name = "another_repo"
        repo_display_name = "Another Repository"
        repo_description = "Another Repository Description"

        self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/create".format(self.org_id)))
        fill_create_repository_form(self.driver, repo_name, repo_display_name, repo_description, is_public=False)

        self.driver.get(urljoin(self.root_url, "/organizations/{}".format(self.org_id)))
        assert "Private" in self.driver.page_source

        self.driver.get(urljoin(self.root_url, "logout"))

        self.driver.get(urljoin(self.root_url, "login"))
        fill_login_form(self.driver, self.another_username, self.another_password)

        self.driver.get(urljoin(self.root_url, "/organizations/{}".format(self.org_id)))
        assert repo_display_name not in self.driver.page_source
