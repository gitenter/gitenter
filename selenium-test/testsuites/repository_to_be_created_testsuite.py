from urllib.parse import urljoin

from testsuites.organization_created_testsuite import OrganizationCreatedTestSuite
from forms.authorization_form import (
    fill_login_form,
    fill_signup_form,
    fill_delete_user_form,
    click_logout
)
from forms.organization_management_form import fill_add_member_form


class RepositoryToBeCreatedTestSuite(OrganizationCreatedTestSuite):

    def setUp(self):
        super(RepositoryToBeCreatedTestSuite, self).setUp()

        self.repo_organizer_username = "repo_organizer"
        self.repo_organizer_password = "password"
        self.repo_organizer_display_name = "Repository Organizer"
        self.repo_organizer_email = "repo_organizer@organization.com"

        self.driver.get(urljoin(self.root_url, "/register"))
        fill_signup_form(
            self.driver,
            self.repo_organizer_username,
            self.repo_organizer_password,
            self.repo_organizer_display_name,
            self.repo_organizer_email)

        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.org_manager_username, self.org_manager_password)

        self.driver.get(urljoin(self.root_url, "organizations/{}/settings/members".format(self.org_id)))
        fill_add_member_form(self.driver, self.repo_organizer_username)

        click_logout(self.driver)

    def tearDown(self):
        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.repo_organizer_username, self.repo_organizer_password)
        self.driver.get(urljoin(self.root_url, "/settings/account/delete"))
        fill_delete_user_form(self.driver, self.repo_organizer_password)

        super(RepositoryToBeCreatedTestSuite, self).tearDown()
