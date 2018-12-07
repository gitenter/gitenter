import unittest
from urllib.parse import urlparse, urljoin
from selenium.webdriver.common.keys import Keys

from testsuites.registered_testsuite import RegisteredTestSuite
from forms.authorization_form import fill_login_form


class TestMemberSetting(RegisteredTestSuite):

    def setUp(self):
        super(TestMemberSetting, self).setUp()

    def tearDown(self):
        super(TestMemberSetting, self).tearDown()

    def test_non_user_cannot_access_setting_page(self):
        self.driver.get(urljoin(self.root_url, "/settings"))
        self.assertEqual(urlparse(self.driver.current_url).path, "/login")

        self.driver.get(urljoin(self.root_url, "/settings/profile"))
        self.assertEqual(urlparse(self.driver.current_url).path, "/login")

        self.driver.get(urljoin(self.root_url, "/settings/account"))
        self.assertEqual(urlparse(self.driver.current_url).path, "/login")


class TestChangeUserProfile(RegisteredTestSuite):

    def setUp(self):
        super(TestChangeUserProfile, self).setUp()

    def tearDown(self):
        super(TestChangeUserProfile, self).tearDown()

    def test_change_user_profile(self):
        display_name_append = "Jr."
        email_append = ".com"

        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.username, self.password)

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
        self.assertEqual(self.driver.find_element_by_id("displayName").get_attribute("value"), self.display_name+display_name_append)
        self.assertEqual(self.driver.find_element_by_id("email").get_attribute("value"), self.email+email_append)

    def test_change_user_profile_invalid_input(self):
        new_display_name = "D"
        new_email = "not_a_email_address"

        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.username, self.password)

        self.driver.get(urljoin(self.root_url, "/settings/profile"))
        display_name_form_fill = self.driver.find_element_by_id("displayName")
        email_form_fill = self.driver.find_element_by_id("email")

        # Cannot use Keys.CTRL+"a", as Linux and Mac behaviors differently.
        for i in range(50):
            display_name_form_fill.send_keys(Keys.ARROW_RIGHT)
            email_form_fill.send_keys(Keys.ARROW_RIGHT)
        for i in range(50):
            display_name_form_fill.send_keys(Keys.BACKSPACE)
            email_form_fill.send_keys(Keys.BACKSPACE)
        display_name_form_fill.send_keys(new_display_name)
        email_form_fill.send_keys(new_email)
        display_name_form_fill.submit()

        self.assertEqual(urlparse(self.driver.current_url).path, "/settings/profile")
        assert "Changes has been saved successfully!" not in self.driver.page_source
        self.assertEqual(self.driver.find_element_by_id("displayName").get_attribute("value"), new_display_name)
        self.assertEqual(self.driver.find_element_by_id("email").get_attribute("value"), new_email)
        assert "size" in self.driver.find_element_by_id("displayName.errors").text
        assert "not a well-formed email addres" in self.driver.find_element_by_id("email.errors").text


class TestChangeUserPassword(RegisteredTestSuite):

    def setUp(self):
        super(TestChangeUserPassword, self).setUp()

    def tearDown(self):
        super(TestChangeUserPassword, self).tearDown()

    def test_change_password_valid(self):
        new_password = "new_password"

        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.username, self.password)

        self.driver.get(urljoin(self.root_url, "/settings/account"))
        self.assertEqual(self.driver.find_element_by_id("username").get_attribute("value"), self.username)

        form_start = self.driver.find_element_by_id("old_password")
        form_start.send_keys(self.password)
        self.driver.find_element_by_id("password").send_keys(new_password)
        form_start.submit()

        self.assertEqual(urlparse(self.driver.current_url).path, "/settings/account")
        assert "Changes has been saved successfully!" in self.driver.page_source

        self.driver.get(urljoin(self.root_url, "/logout"))

        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.username, self.password)
        assert "Invalid username and password!" in self.driver.page_source

        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.username, new_password)
        assert "Logged in as {}".format(self.username) in self.driver.page_source

    def test_wrong_old_password_deny_change_password(self):
        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.username, self.password)

        self.driver.get(urljoin(self.root_url, "/settings/account"))
        form_start = self.driver.find_element_by_id("old_password")
        form_start.send_keys("wrong_password")
        self.driver.find_element_by_id("password").send_keys("whatever")
        form_start.submit()

        self.assertEqual(urlparse(self.driver.current_url).path, "/settings/account")
        self.assertEqual(self.driver.find_element_by_id("username").get_attribute("value"), self.username)
        assert "Old password doesn't match!" in self.driver.page_source

    def test_invalid_new_password(self):
        new_password = "p"

        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.username, self.password)

        self.driver.get(urljoin(self.root_url, "/settings/account"))
        form_start = self.driver.find_element_by_id("old_password")
        form_start.send_keys(self.password)
        self.driver.find_element_by_id("password").send_keys(new_password)
        form_start.submit()

        self.assertEqual(urlparse(self.driver.current_url).path, "/settings/account")
        self.assertEqual(self.driver.find_element_by_id("username").get_attribute("value"), self.username)
        assert "size" in self.driver.find_element_by_id("password.errors").text


class TestAddSshKey(RegisteredTestSuite):

    def setUp(self):
        super(TestAddSshKey, self).setUp()

    def tearDown(self):
        super(TestAddSshKey, self).tearDown()

    def _add_ssh_key(self, ssh_key):
        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.username, self.password)

        self.driver.get(urljoin(self.root_url, "/settings/ssh"))
        form_start = self.driver.find_element_by_id("value")
        form_start.send_keys(ssh_key)
        form_start.submit()

    def test_add_valid_ssh_key(self):
        ssh_key = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCvYWPKDryb70LRP1tePi9h1q2vebxFIQZn3MlPbp4XYKP+t+t325BlMbj6Tnvx55nDR5Q6CwPOBz5ijdv8yUEuQ9aaR3+CNvOqjrs7iE2mO4HPiE+w9tppNhOF37a/ElVuoKQtTrP4hFyQbdISVCpvhXx9MZZcaq+A8aLbcrL1ggydXiLpof6gyb9UgduXx90ntbahI5JZgNTZfZSzzCRu7of/zZYKr4dQLiCFGrGDnSs+j7Fq0GAGKywRz27UMh9ChE+PVy8AEOV5/Mycula2KWRhKU/DWZF5zaeVE4BliQjKtCJwhJGRz52OdFc55ic7JoDcF9ovEidnhw+VNnN9 user@email.com"
        self._add_ssh_key(ssh_key)

        self.assertEqual(urlparse(self.driver.current_url).path, "/settings/ssh")
        assert "AAAAB3NzaC1yc2EAAAADAQABAAABAQCv" in self.driver.page_source

    def test_add_ssh_key_invalid_format(self):
        ssh_key = "invalid_ssh_key"
        self._add_ssh_key(ssh_key)

        self.assertEqual(urlparse(self.driver.current_url).path, "/settings/ssh")
        assert "Wrong format" in self.driver.find_element_by_id("value.errors").text

    def test_add_ssh_key_base64_encoded_key_error(self):
        ssh_key = "ssh-rsa AAAAB3 user@email.com"
        self._add_ssh_key(ssh_key)

        self.assertEqual(urlparse(self.driver.current_url).path, "/settings/ssh")
        assert "The SSH key does not have a valid format." in self.driver.find_element_by_class_name("error").text


if __name__ == '__main__':
    unittest.main()
