from urllib.parse import urljoin

from testsuites.registered_testsuite import RegisteredTestSuite
from forms.authorization_form import fill_signup_form


class OrganizationToBeCreatedTestSuite(RegisteredTestSuite):

    def setUp(self):
        super(OrganizationToBeCreatedTestSuite, self).setUp()

        self.org_manager_username = "manager"
        self.org_manager_password = "password"
        self.org_manager_display_name = "Organization Manager"
        self.org_manager_email = "manager@organization.com"

        self.driver.get(urljoin(self.root_url, "/register"))
        fill_signup_form(
            self.driver,
            self.org_manager_username,
            self.org_manager_password,
            self.org_manager_display_name,
            self.org_manager_email)

        self.org_member_username = "member"
        self.org_member_password = "password"
        self.org_member_display_name = "Organization Member"
        self.org_member_email = "member@organization.com"

        self.driver.get(urljoin(self.root_url, "/register"))
        fill_signup_form(
            self.driver,
            self.org_member_username,
            self.org_member_password,
            self.org_member_display_name,
            self.org_member_email)

    def tearDown(self):
        super(OrganizationToBeCreatedTestSuite, self).tearDown()
