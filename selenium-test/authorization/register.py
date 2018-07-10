import unittest
import random
import time
from urllib.parse import urlparse, urljoin

from selenium import webdriver
from selenium.webdriver.support.ui import WebDriverWait

root_url = "http://localhost:8887"


class TestSignUp(unittest.TestCase):

    def setUp(self):
        self.driver = webdriver.Chrome()

    def tearDown(self):
        self.driver.close()

    def test_redirect_to_login_for_authorized_pate(self):
        self.driver.get(urljoin(root_url, "/"))
        self.assertEqual(urlparse(self.driver.current_url).path, "/login")

    def test_register_and_successfully_login_fill_form(self):
        distingisher = str(random.randint(0, 10000))

        username = "username"+distingisher
        password = "password"
        display_name = "User Name "+distingisher
        email = "username"+distingisher+"@email.com"

        self.driver.get(urljoin(root_url, "register"))
        assert "GitEnter" in self.driver.title
        assert "Register" in self.driver.page_source

        self._signup_fill_form(self.driver, username, password, display_name, email)

        # Redirect to login after register
        self.assertEqual(urlparse(self.driver.current_url).path, "/login")

        # Login with just registered username and password
        self.driver.get(urljoin(root_url, "login"))
        assert "Log in" in self.driver.page_source

        self._login_fill_form(self.driver, username, password)
        self.assertEqual(len(self.driver.get_cookies()), 1)

        # TODO:
        # Check the user can successfully login.

        self.driver.get(urljoin(root_url, "logout"))

        # Logout and login again with remember_me checked
        self.driver.get(urljoin(root_url, "login"))

        self._login_fill_form(self.driver, username, password, remember_me=True)

        self.assertEqual(urlparse(self.driver.current_url).path, "/")
        self.assertEqual(len(self.driver.get_cookies()), 2)

        find_cookie = False
        for cookie in self.driver.get_cookies():
            if cookie["name"] == "remember-me":
                self.assertEqual(cookie["domain"], urlparse(self.driver.current_url).hostname)
                self.assertAlmostEqual(cookie["expiry"] - float(time.time()), 2419200, delta=1)
                find_cookie = True
        self.assertTrue(find_cookie)

        self.driver.get(urljoin(root_url, "logout"))

    def test_login_fill_form_with_nonexistent_user(self):
        username = "nonexistent_username"
        password = "password"

        self.driver.get(urljoin(root_url, "login"))

        self._login_fill_form(self.driver, username, password)
        assert "Invalid username and password!" in self.driver.page_source

    def test_register_with_invalid_input(self):
        username = "u"
        password = "p"
        display_name = "U"
        email = "not_a_email_address"

        self.driver.get(urljoin(root_url, "register"))
        self._signup_fill_form(self.driver, username, password, display_name, email)

        self.assertEqual(urlparse(self.driver.current_url).path, "/register")
        assert "size" in self.driver.find_element_by_id("username.errors").text
        assert "size" in self.driver.find_element_by_id("password.errors").text
        assert "size" in self.driver.find_element_by_id("displayName.errors").text
        assert "not a well-formed email addres" in self.driver.find_element_by_id("email.errors").text

    @staticmethod
    def _signup_fill_form(driver, username, password, display_name, email):
        form_start = driver.find_element_by_id("username")
        form_start.send_keys(username)
        driver.find_element_by_id("password").send_keys(password)
        driver.find_element_by_id("displayName").send_keys(display_name)
        driver.find_element_by_id("email").send_keys(email)

        form_start.submit()

    @staticmethod
    def _login_fill_form(driver, username, password, remember_me=False):
        form_start = driver.find_element_by_id("username")
        form_start.send_keys(username)
        driver.find_element_by_id("password").send_keys(password)
        if remember_me:
            driver.find_element_by_id("remember_me").click()

        form_start.submit()


if __name__ == '__main__':
    unittest.main()
