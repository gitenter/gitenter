import unittest
import time
from urllib.parse import urlparse, urljoin

from setup.testcase import GitEnterTest
from fill_forms import authorization


class TestAuthorization(GitEnterTest):

    def setUp(self):
        super(TestAuthorization, self).setUp()

    def tearDown(self):
        super(TestAuthorization, self).tearDown()

    def test_redirect_to_login_for_authorized_pate(self):
        self.driver.get(urljoin(self.root_url, "/"))
        self.assertEqual(urlparse(self.driver.current_url).path, "/login")

    def test_register_and_successfully_login(self):
        username = "username"
        password = "password"
        display_name = "User Name "
        email = "username@email.com"

        self.driver.get(urljoin(self.root_url, "register"))
        assert "GitEnter" in self.driver.title
        assert "Register" in self.driver.page_source

        authorization.signup_fill_form(self.driver, username, password, display_name, email)

        # Redirect to login after register
        self.assertEqual(urlparse(self.driver.current_url).path, "/login")

        # Login with just registered username and password
        self.driver.get(urljoin(self.root_url, "login"))
        assert "Log in" in self.driver.page_source

        authorization.login_fill_form(self.driver, username, password)
        self.assertEqual(len(self.driver.get_cookies()), 1)

        # TODO:
        # Check the user can successfully login.

        self.driver.get(urljoin(self.root_url, "logout"))

        # Logout and login again with remember_me checked
        self.driver.get(urljoin(self.root_url, "login"))

        authorization.login_fill_form(self.driver, username, password, remember_me=True)

        self.assertEqual(urlparse(self.driver.current_url).path, "/")
        self.assertEqual(len(self.driver.get_cookies()), 2)

        find_cookie = False
        for cookie in self.driver.get_cookies():
            if cookie["name"] == "remember-me":
                self.assertEqual(cookie["domain"], urlparse(self.driver.current_url).hostname)
                self.assertAlmostEqual(cookie["expiry"] - float(time.time()), 2419200, delta=1)
                find_cookie = True
        self.assertTrue(find_cookie)

        self.driver.get(urljoin(self.root_url, "logout"))

    def test_login_with_nonexistent_user(self):
        username = "nonexistent_username"
        password = "password"

        self.driver.get(urljoin(self.root_url, "login"))

        authorization.login_fill_form(self.driver, username, password)
        assert "Invalid username and password!" in self.driver.page_source

    def test_register_with_invalid_input(self):
        username = "u"
        password = "p"
        display_name = "U"
        email = "not_a_email_address"

        self.driver.get(urljoin(self.root_url, "register"))
        authorization.signup_fill_form(self.driver, username, password, display_name, email)

        self.assertEqual(urlparse(self.driver.current_url).path, "/register")
        assert "size" in self.driver.find_element_by_id("username.errors").text
        assert "size" in self.driver.find_element_by_id("password.errors").text
        assert "size" in self.driver.find_element_by_id("displayName.errors").text
        assert "not a well-formed email addres" in self.driver.find_element_by_id("email.errors").text


if __name__ == '__main__':
    unittest.main()
