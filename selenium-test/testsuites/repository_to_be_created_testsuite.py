from urllib.parse import urljoin

from testsuites.organization_created_testsuite import OrganizationCreatedTestSuite
from forms.authorization_form import (
    fill_login_form,
    fill_signup_form,
    fill_delete_user_form,
    login_as
)
from forms.user_settings_form import add_ssh_key
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

        with login_as(self.driver, self.root_url, self.org_manager_username, self.org_manager_password):
            self.driver.get(urljoin(self.root_url, "organizations/{}/settings/members".format(self.org_id)))
            fill_add_member_form(self.driver, self.repo_organizer_username)

        with login_as(self.driver, self.root_url, self.repo_organizer_username, self.repo_organizer_password):
            # It is very tricky that the local SSH key need to be setup as the person
            # who is doing git command. As we only have one SSH key (in the selenium
            # machine) and the actual key is unique (and used to distinguish who the
            # user) is. If another user is having the same SSH key:
            # > fatal: Could not read from remote repository.
            # >
            # > Please make sure you have the correct access rights
            # > and the repository exists.
            # It is actually because `check_if_can_edit_repository.py` returns false.
            self.driver.get(urljoin(self.root_url, "/settings/ssh"))
            add_ssh_key(self.driver, self.profile.get_ssh_key())

    def tearDown(self):
        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.repo_organizer_username, self.repo_organizer_password)
        self.driver.get(urljoin(self.root_url, "/settings/account/delete"))
        fill_delete_user_form(self.driver, self.repo_organizer_password)

        super(RepositoryToBeCreatedTestSuite, self).tearDown()
