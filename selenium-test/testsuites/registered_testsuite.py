from urllib.parse import urljoin

from testsuites.base_testsuite import BaseTestSuite
from forms.authorization_form import fill_signup_form


class RegisteredTestSuite(BaseTestSuite):

    def setUp(self):
        super(RegisteredTestSuite, self).setUp()

        self.username = "username"
        self.password = "password"
        self.display_name = "Display Name"
        self.email = "username@email.com"

        self.driver.get(urljoin(self.root_url, "/register"))
        fill_signup_form(self.driver, self.username, self.password, self.display_name, self.email)

    def tearDown(self):
        super(RegisteredTestSuite, self).tearDown()
