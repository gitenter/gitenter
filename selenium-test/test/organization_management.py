import unittest
from urllib.parse import urljoin

from setup.testcase import GitEnterTest
from fill_forms import authorization


class TestOrganizationManagement(GitEnterTest):

    def setUp(self):
        super(TestOrganizationManagement, self).setUp()

        username = "username"
        password = "password"
        display_name = "User Name "
        email = "username@email.com"

        self.driver.get(urljoin(self.root_url, "register"))
        authorization.signup_fill_form(self.driver, username, password, display_name, email)

        self.driver.get(urljoin(self.root_url, "login"))
        authorization.login_fill_form(self.driver, username, password)

    def tearDown(self):
        super(TestOrganizationManagement, self).tearDown()

    def test_organization_management(self):
        pass


if __name__ == '__main__':
    unittest.main()
