from selenium.common.exceptions import TimeoutException
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from urllib.parse import urljoin

from testsuites.base_testsuite import BaseTestSuite
from forms.authorization_form import (
    fill_signup_form,
    fill_login_form,
    fill_delete_user_form
)


class RegisteredTestSuite(BaseTestSuite):

    def setUp(self):
        super(RegisteredTestSuite, self).setUp()

        self.username = "username"
        self.password = "password"
        self.display_name = "Display Name"
        self.email = "username@email.com"

        self.driver.get(urljoin(self.root_url, "/register"))
        fill_signup_form(self.driver, self.username, self.password, self.display_name, self.email)
        try:
            WebDriverWait(self.driver, 3).until(EC.url_to_be(urljoin(self.root_url, "/login")))
        except TimeoutException:
            self.assertFalse(True, 'Sign up fails')

    def tearDown(self):
        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.username, self.password)
        try:
            WebDriverWait(self.driver, 3).until(EC.url_changes(urljoin(self.root_url, "/login")))
        except TimeoutException:
            self.assertFalse(True, 'Login fails')

        self.driver.get(urljoin(self.root_url, "/settings/account/delete"))
        fill_delete_user_form(self.driver, self.password)
        try:
            WebDriverWait(self.driver, 3).until(EC.url_changes(urljoin(self.root_url, "/settings/account/delete")))
        except TimeoutException:
            self.assertFalse(True, 'Cannot delete user')

        super(RegisteredTestSuite, self).tearDown()
