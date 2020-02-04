from selenium.common.exceptions import TimeoutException
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from urllib.parse import urljoin

from testsuites.registered_testsuite import RegisteredTestSuite
from forms.authorization_form import (
    fill_signup_form,
    fill_login_form,
    fill_delete_user_form
)


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
        try:
            WebDriverWait(self.driver, 3).until(EC.url_to_be(urljoin(self.root_url, "/login")))
        except TimeoutException:
            self.assertFalse(True, 'Sign up fails')

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
        try:
            WebDriverWait(self.driver, 3).until(EC.url_to_be(urljoin(self.root_url, "/login")))
        except TimeoutException:
            self.assertFalse(True, 'Sign up fails')

    def tearDown(self):
        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.org_manager_username, self.org_manager_password)
        try:
            WebDriverWait(self.driver, 3).until(EC.url_changes(urljoin(self.root_url, "/login")))
        except TimeoutException:
            self.assertFalse(True, 'Login fails')
        self.driver.get(urljoin(self.root_url, "/settings/account/delete"))
        fill_delete_user_form(self.driver, self.org_manager_password)
        try:
            WebDriverWait(self.driver, 3).until(EC.url_changes(urljoin(self.root_url, "/settings/account/delete")))
        except TimeoutException:
            self.assertFalse(True, 'Cannot delete user')

        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.org_member_username, self.org_member_password)
        try:
            WebDriverWait(self.driver, 3).until(EC.url_changes(urljoin(self.root_url, "/login")))
        except TimeoutException:
            self.assertFalse(True, 'Login fails')
        self.driver.get(urljoin(self.root_url, "/settings/account/delete"))
        fill_delete_user_form(self.driver, self.org_member_password)
        try:
            WebDriverWait(self.driver, 3).until(EC.url_changes(urljoin(self.root_url, "/settings/account/delete")))
        except TimeoutException:
            self.assertFalse(True, 'Cannot delete user')

        super(OrganizationToBeCreatedTestSuite, self).tearDown()
