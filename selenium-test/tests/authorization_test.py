import unittest
import time
from selenium.common.exceptions import TimeoutException
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from urllib.parse import urlparse, urljoin

from testsuites.base_testsuite import BaseTestSuite
from forms.authorization_form import (
    fill_signup_form,
    fill_login_form,
    fill_delete_user_form,
    login_as
)


class TestAuthorization(BaseTestSuite):

    def setUp(self):
        super(TestAuthorization, self).setUp()

    def tearDown(self):
        super(TestAuthorization, self).tearDown()

    def test_register_and_login_and_delete_user(self):
        username = "username"
        password = "password"
        display_name = "User Name"
        email = "username@email.com"

        self.driver.get(urljoin(self.root_url, "/register"))
        assert "GitEnter" in self.driver.title
        assert "/register" in self.driver.page_source

        fill_signup_form(self.driver, username, password, display_name, email)
        try:
            WebDriverWait(self.driver, 3).until(EC.url_to_be(urljoin(self.root_url, "/login")))
        except TimeoutException:
            self.assertFalse(True, 'Redirect to login after register fails')

        # Cannot login with incorrect password
        incorrect_password = "incorrect_password"

        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, username, incorrect_password)
        try:
            element = WebDriverWait(self.driver, 3).until(EC.presence_of_element_located((By.CLASS_NAME, "error")))
        except TimeoutException:
            self.assertFalse(True, 'Expected error not raised')
        self.assertEqual(urlparse(self.driver.current_url).path, "/login")
        assert "Invalid username and password!" in element.text

#        baseline_cookie_count = len(self.driver.get_cookies())

        # Login with just registered username and password
        with login_as(self.driver, self.root_url, username, password):
            self.assertEqual(urlparse(self.driver.current_url).path, "/")  # if from "/login" page will be redirect to "/":
            assert "Logged in as {}".format(username) in self.driver.page_source
#            self.assertEqual(len(self.driver.get_cookies()), baseline_cookie_count)

#        # Login again with remember_me checked
#        with login_as(self.driver, self.root_url, username, password, remember_me=True):
#            self.assertEqual(urlparse(self.driver.current_url).path, "/")
#            self.assertEqual(len(self.driver.get_cookies()), baseline_cookie_count + 1)
#
#            find_cookie = False
#            for cookie in self.driver.get_cookies():
#                if cookie["name"] == "remember-me":
#                    self.assertEqual(cookie["domain"], urlparse(self.driver.current_url).hostname)
#                    self.assertAlmostEqual(cookie["expiry"] - float(time.time()), 2419200, delta=1)
#                    find_cookie = True
#            self.assertTrue(find_cookie)

        # Create another user with the same username
#        self.driver.get(urljoin(self.root_url, "/register"))
#        fill_signup_form(self.driver, username, "dummy", "dummy", "dummy@dummy.com")
#        self.assertEqual(urlparse(self.driver.current_url).path, "/register")
#        assert "username already exist!" in self.driver.page_source

        # Create another user with the same email address
#        self.driver.get(urljoin(self.root_url, "/register"))
#        fill_signup_form(self.driver, "dummy", "dummy", "dummy", email)
#        self.assertEqual(urlparse(self.driver.current_url).path, "/register")
#        assert "email already exist!" in self.driver.page_source

        # Login again and delete user herself
        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, username, password)
        try:
            WebDriverWait(self.driver, 3).until(EC.url_changes(urljoin(self.root_url, "/login")))
        except TimeoutException:
            self.assertFalse(True, 'Login fails')

        self.driver.get(urljoin(self.root_url, "/settings/account/delete"))
        fill_delete_user_form(self.driver, "wrong_password")
        try:
            element = WebDriverWait(self.driver, 3).until(EC.presence_of_element_located((By.CLASS_NAME, "error")))
        except TimeoutException:
            self.assertFalse(True, 'Expected error not raised')
        self.assertEqual(urlparse(self.driver.current_url).path, "/settings/account/delete")
        assert "Password doesn't match!" in element.text

        self.driver.get(urljoin(self.root_url, "/settings/account/delete"))
        fill_delete_user_form(self.driver, password)
        try:
            WebDriverWait(self.driver, 3).until(EC.url_changes(urljoin(self.root_url, "/settings/account/delete")))
        except TimeoutException:
            self.assertFalse(True, 'Cannot delete user')
        self.assertEqual(urlparse(self.driver.current_url).path, "/login")

        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, username, password)
        try:
            element = WebDriverWait(self.driver, 3).until(EC.presence_of_element_located((By.CLASS_NAME, "error")))
        except TimeoutException:
            self.assertFalse(True, 'Expected error not raised')
        self.assertEqual(urlparse(self.driver.current_url).path, "/login")
        assert "Invalid username and password!" in element.text

    def test_redirect_after_login(self):
        pass

    def test_login_with_nonexistent_user(self):
        username = "nonexistent_username"
        password = "password"

        self.driver.get(urljoin(self.root_url, "/login"))

        fill_login_form(self.driver, username, password)
        try:
            element = WebDriverWait(self.driver, 3).until(EC.presence_of_element_located((By.CLASS_NAME, "error")))
        except TimeoutException:
            self.assertFalse(True, 'Expected error not raised')
        self.assertEqual(urlparse(self.driver.current_url).path, "/login")
        assert "Invalid username and password!" in element.text


if __name__ == '__main__':
    unittest.main()
