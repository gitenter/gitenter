import unittest
from urllib.parse import urlparse, urljoin

from test.testcase import GitEnterTest
from forms.authorization_form import (
    fill_signup_form,
    fill_login_form
)
from forms.organization_management_form import (
    fill_create_organization_form
)
from forms.repository_management_form import (
    fill_create_repository_form
)


class TestOrganizationManagement(GitEnterTest):

    def setUp(self):
        super(TestOrganizationManagement, self).setUp()

        username = "username"
        password = "password"
        display_name = "User Name"
        email = "username@email.com"

        another_username = "another_username"
        another_password = "password"
        another_display_name = "Another User Name"
        another_email = "another_username@email.com"

        self.driver.get(urljoin(self.root_url, "register"))
        fill_signup_form(self.driver, username, password, display_name, email)

        self.driver.get(urljoin(self.root_url, "register"))
        fill_signup_form(self.driver, another_username, another_password, another_display_name, another_email)

        self.driver.get(urljoin(self.root_url, "login"))
        fill_login_form(self.driver, username, password)

        org_name = "another_org"
        org_display_name = "Another Organization"

        self.driver.get(urljoin(self.root_url, "/organizations/create"))
        fill_create_organization_form(self.driver, org_name, org_display_name)
        org_link = self.driver.find_element_by_link_text(org_display_name).get_attribute("href")
        self.org_id = urlparse(org_link).path.split("/")[-1]

        self.username = username
        self.display_name = display_name

        self.another_username = another_username
        self.another_password = another_password
        self.another_display_name = another_display_name

    def tearDown(self):
        super(TestOrganizationManagement, self).tearDown()

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
        #assert repo_display_name not in self.driver.page_source
