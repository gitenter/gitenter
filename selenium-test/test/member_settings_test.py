import unittest
from urllib.parse import urlparse, urljoin
from selenium.webdriver.common.keys import Keys

from testsuite.registered_testsuite import RegisteredTestSuite
from forms.authorization_form import fill_login_form

class TestMemberSetting(RegisteredTestSuite):

    def setUp(self):
        super(TestMemberSetting, self).setUp()

    def tearDown(self):
        super(TestMemberSetting, self).tearDown()

    def test_non_user_cannot_access_setting_page(self):
        self.driver.get(urljoin(self.root_url, "/settings"))
        self.assertEqual(urlparse(self.driver.current_url).path, "/login")

    def test_change_user_profile(self):
        display_name_append = "Jr."
        email_append = ".com"

        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.username, self.password)

        self.driver.get(urljoin(self.root_url, "/settings/profile"))
        display_form_fill = self.driver.find_element_by_id("displayName")
        email_form_fill = self.driver.find_element_by_id("email")

        self.assertEqual(self.driver.find_element_by_id("username").get_attribute("value"), self.username)
        self.assertEqual(display_form_fill.get_attribute("value"), self.display_name)
        self.assertEqual(email_form_fill.get_attribute("value"), self.email)

        # To move the cursor to the very right. By unknow reasons
        # the cursor will starts to the right for "display_form_fill"
        # but will starts to the left for "email_form_fill".
        for i in range(50):
            display_form_fill.send_keys(Keys.ARROW_RIGHT)
            email_form_fill.send_keys(Keys.ARROW_RIGHT)
        display_form_fill.send_keys(display_name_append)
        email_form_fill.send_keys(email_append)
        display_form_fill.submit()

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
        display_form_fill = self.driver.find_element_by_id("displayName")
        email_form_fill = self.driver.find_element_by_id("email")

        # Cannot use Keys.CTRL+"a", as Linux and Mac behaviors differently.
        for i in range(50):
            display_form_fill.send_keys(Keys.ARROW_RIGHT)
            email_form_fill.send_keys(Keys.ARROW_RIGHT)
        for i in range(50):
            display_form_fill.send_keys(Keys.BACKSPACE)
            email_form_fill.send_keys(Keys.BACKSPACE)
        display_form_fill.send_keys(new_display_name)
        email_form_fill.send_keys(new_email)
        display_form_fill.submit()

        self.assertEqual(urlparse(self.driver.current_url).path, "/settings/profile")
        assert "Changes has been saved successfully!" not in self.driver.page_source
        self.assertEqual(self.driver.find_element_by_id("displayName").get_attribute("value"), new_display_name)
        self.assertEqual(self.driver.find_element_by_id("email").get_attribute("value"), new_email)
        assert "size" in self.driver.find_element_by_id("displayName.errors").text
        assert "not a well-formed email addres" in self.driver.find_element_by_id("email.errors").text


if __name__ == '__main__':
    unittest.main()
