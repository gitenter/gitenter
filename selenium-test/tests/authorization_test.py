import unittest
import time
from urllib.parse import urlparse, urljoin

from testsuites.base_testsuite import BaseTestSuite
from forms.authorization_form import (
    fill_signup_form,
    fill_login_form,
    fill_delete_user_form,
    click_logout,
    login_as
)


class TestAuthorization(BaseTestSuite):

    def setUp(self):
        super(TestAuthorization, self).setUp()

    def tearDown(self):
        super(TestAuthorization, self).tearDown()

    def test_redirect_to_login_for_authorized_pate_and_login_page_content(self):
        self.driver.get(urljoin(self.root_url, "/"))
        self.assertEqual(urlparse(self.driver.current_url).path, "/login")
        assert "Log in" in self.driver.page_source
        assert "Sign up" in self.driver.page_source

    def test_register_and_login_and_delete_user(self):
        username = "username"
        password = "password"
        display_name = "User Name"
        email = "username@email.com"

        self.driver.get(urljoin(self.root_url, "/register"))
        assert "GitEnter" in self.driver.title
        assert "/register" in self.driver.page_source

        fill_signup_form(self.driver, username, password, display_name, email)

        # Redirect to login after register
        self.assertEqual(urlparse(self.driver.current_url).path, "/login")

        # Cannot login with incorrect password
        incorrect_password = "incorrect_password"

        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, username, incorrect_password)
        assert "Invalid username and password!" in self.driver.page_source

        # Login with just registered username and password
        with login_as(self.driver, self.root_url, username, password):
            self.assertEqual(urlparse(self.driver.current_url).path, "/") # if from "/login" page will be redirect to "/":
            assert "Logged in as {}".format(username) in self.driver.page_source
            self.assertEqual(len(self.driver.get_cookies()), 1)

        # Login again with remember_me checked
        with login_as(self.driver, self.root_url, username, password, remember_me=True):
            self.assertEqual(urlparse(self.driver.current_url).path, "/")
            self.assertEqual(len(self.driver.get_cookies()), 2)

            find_cookie = False
            for cookie in self.driver.get_cookies():
                if cookie["name"] == "remember-me":
                    self.assertEqual(cookie["domain"], urlparse(self.driver.current_url).hostname)
                    self.assertAlmostEqual(cookie["expiry"] - float(time.time()), 2419200, delta=1)
                    find_cookie = True
            self.assertTrue(find_cookie)

        # Login again and delete user herself
        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, username, password)

        self.driver.get(urljoin(self.root_url, "/settings/account/delete"))
        fill_delete_user_form(self.driver, "wrong_password")
        self.assertEqual(urlparse(self.driver.current_url).path, "/settings/account/delete")
        assert "Password doesn't match!" in self.driver.page_source

        self.driver.get(urljoin(self.root_url, "/settings/account/delete"))
        fill_delete_user_form(self.driver, password)
        self.assertEqual(urlparse(self.driver.current_url).path, "/login")

        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, username, password)
        assert "Invalid username and password!" in self.driver.page_source

    def test_redirect_after_login(self):
        pass

    def test_login_with_nonexistent_user(self):
        username = "nonexistent_username"
        password = "password"

        self.driver.get(urljoin(self.root_url, "/login"))

        fill_login_form(self.driver, username, password)
        assert "Invalid username and password!" in self.driver.page_source

    def test_register_username_already_exists(self):
        pass


if __name__ == '__main__':
    unittest.main()
