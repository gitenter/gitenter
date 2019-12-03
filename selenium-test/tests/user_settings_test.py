import unittest
from urllib.parse import urlparse, urljoin
from selenium.webdriver.common.keys import Keys

from testsuites.registered_testsuite import RegisteredTestSuite
from forms.authorization_form import (
    fill_login_form,
    login_as
)
from forms.user_settings_form import add_ssh_key


class TestUserSettings(RegisteredTestSuite):

    def setUp(self):
        super(TestUserSettings, self).setUp()

    def tearDown(self):
        super(TestUserSettings, self).tearDown()

    def test_non_user_cannot_access_setting_page(self):
        self.driver.get(urljoin(self.root_url, "/settings"))
        self.assertEqual(urlparse(self.driver.current_url).path, "/login")

        self.driver.get(urljoin(self.root_url, "/settings/profile"))
        self.assertEqual(urlparse(self.driver.current_url).path, "/login")

        self.driver.get(urljoin(self.root_url, "/settings/ssh"))
        self.assertEqual(urlparse(self.driver.current_url).path, "/login")

        self.driver.get(urljoin(self.root_url, "/settings/account/password"))
        self.assertEqual(urlparse(self.driver.current_url).path, "/login")

        self.driver.get(urljoin(self.root_url, "/settings/account/delete"))
        self.assertEqual(urlparse(self.driver.current_url).path, "/login")


class TestChangeUserProfile(RegisteredTestSuite):

    def setUp(self):
        super(TestChangeUserProfile, self).setUp()

    def tearDown(self):
        super(TestChangeUserProfile, self).tearDown()

    def test_change_user_profile(self):
        display_name_append = "Jr."
        email_append = ".com"

        with login_as(self.driver, self.root_url, self.username, self.password):
            self.driver.get(urljoin(self.root_url, "/settings/profile"))
            display_name_form_fill = self.driver.find_element_by_id("displayName")
            email_form_fill = self.driver.find_element_by_id("email")

            self.assertEqual(self.driver.find_element_by_id("username").get_attribute("value"), self.username)
            self.assertEqual(display_name_form_fill.get_attribute("value"), self.display_name)
            self.assertEqual(email_form_fill.get_attribute("value"), self.email)

            # To move the cursor to the very right. By unknow reasons
            # the cursor will starts to the right for "display_name_form_fill"
            # but will starts to the left for "email_form_fill".
            for i in range(50):
                display_name_form_fill.send_keys(Keys.ARROW_RIGHT)
                email_form_fill.send_keys(Keys.ARROW_RIGHT)
            display_name_form_fill.send_keys(display_name_append)
            email_form_fill.send_keys(email_append)
            display_name_form_fill.submit()

            self.assertEqual(urlparse(self.driver.current_url).path, "/settings/profile")
            assert "Changes has been saved successfully!" in self.driver.page_source
            self.assertEqual(
                self.driver.find_element_by_id("displayName").get_attribute("value"),
                self.display_name+display_name_append)
            self.assertEqual(self.driver.find_element_by_id("email").get_attribute("value"), self.email+email_append)


class TestChangeUserPassword(RegisteredTestSuite):

    def setUp(self):
        super(TestChangeUserPassword, self).setUp()

    def tearDown(self):
        super(TestChangeUserPassword, self).tearDown()

    def _change_password_form(self, driver, current_password, new_password):
        form_start = driver.find_element_by_id("old_password")
        form_start.send_keys(current_password)
        driver.find_element_by_id("password").send_keys(new_password)
        form_start.submit()

    def test_change_password_valid(self):
        new_password = "new_password"

        with login_as(self.driver, self.root_url, self.username, self.password):
            self.driver.get(urljoin(self.root_url, "/settings/account/password"))
            self.assertEqual(self.driver.find_element_by_id("username").get_attribute("value"), self.username)

            self._change_password_form(self.driver, self.password, new_password)

            self.assertEqual(urlparse(self.driver.current_url).path, "/settings/account/password")
            assert "Changes has been saved successfully!" in self.driver.page_source

        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.username, self.password)
        assert "Invalid username and password!" in self.driver.page_source

        with login_as(self.driver, self.root_url, self.username, new_password):
            assert "Logged in as {}".format(self.username) in self.driver.page_source

            # Reset state, so this test is idempotent and `tearDown` can successfully delete
            # the user account.
            self.driver.get(urljoin(self.root_url, "/settings/account/password"))
            self._change_password_form(self.driver, new_password, self.password)

    def test_wrong_old_password_deny_change_password(self):
        with login_as(self.driver, self.root_url, self.username, self.password):
            self.driver.get(urljoin(self.root_url, "/settings/account/password"))
            form_start = self.driver.find_element_by_id("old_password")
            form_start.send_keys("wrong_password")
            self.driver.find_element_by_id("password").send_keys("whatever")
            form_start.submit()

            self.assertEqual(urlparse(self.driver.current_url).path, "/settings/account/password")
            self.assertEqual(self.driver.find_element_by_id("username").get_attribute("value"), self.username)
            assert "Old password doesn't match!" in self.driver.page_source


class TestAddSshKey(RegisteredTestSuite):

    def setUp(self):
        super(TestAddSshKey, self).setUp()

    def tearDown(self):
        super(TestAddSshKey, self).tearDown()

    def test_add_valid_ssh_key(self):
        ssh_key = self.profile.get_ssh_key()

        with login_as(self.driver, self.root_url, self.username, self.password):
            self.driver.get(urljoin(self.root_url, "/settings/ssh"))
            add_ssh_key(self.driver, ssh_key)

            self.assertEqual(urlparse(self.driver.current_url).path, "/settings/ssh")
            assert ssh_key[:10] in self.driver.page_source

    def test_add_ssh_key_invalid_format(self):
        ssh_key = "invalid_ssh_key"

        with login_as(self.driver, self.root_url, self.username, self.password):
            self.driver.get(urljoin(self.root_url, "/settings/ssh"))
            add_ssh_key(self.driver, ssh_key)

            self.assertEqual(urlparse(self.driver.current_url).path, "/settings/ssh")
            assert "The SSH key does not have a valid format!" in self.driver.find_element_by_id("value.errors").text

    def test_add_ssh_key_base64_encoded_key_error(self):
        ssh_key = "ssh-rsa AAAAB3 user@email.com"

        with login_as(self.driver, self.root_url, self.username, self.password):
            self.driver.get(urljoin(self.root_url, "/settings/ssh"))
            add_ssh_key(self.driver, ssh_key)

            self.assertEqual(urlparse(self.driver.current_url).path, "/settings/ssh")
            assert "The SSH key does not have a valid format!" in self.driver.find_element_by_class_name("error").text


if __name__ == '__main__':
    unittest.main()
