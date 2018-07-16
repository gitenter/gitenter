from urllib.parse import urljoin

from testsuite.base_testsuite import BaseTestSuite
from forms.authorization_form import (
    fill_signup_form,
    fill_login_form
)


class RegisteredTestSuite(BaseTestSuite):

    def setUp(self):
        super(RegisteredTestSuite, self).setUp()

        username = "username"
        password = "password"
        display_name = "User Name"
        email = "username@email.com"

        another_username = "another_username"
        another_password = "another_password"
        another_display_name = "Another User Name"
        another_email = "another_username@email.com"

        self.driver.get(urljoin(self.root_url, "register"))
        fill_signup_form(self.driver, username, password, display_name, email)

        self.driver.get(urljoin(self.root_url, "register"))
        fill_signup_form(self.driver, another_username, another_password, another_display_name, another_email)

        self.driver.get(urljoin(self.root_url, "login"))
        fill_login_form(self.driver, username, password)

        self.username = username
        self.display_name = display_name

        self.another_username = another_username
        self.another_password = another_password
        self.another_display_name = another_display_name

    def tearDown(self):
        super(RegisteredTestSuite, self).tearDown()
