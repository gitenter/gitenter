from urllib.parse import urlparse, urljoin
from selenium.webdriver.common.keys import Keys

from testsuites.repository_created_testsuite import RepositoryCreatedTestSuite
from forms.authorization_form import fill_login_form


class TestRepositoryManagement(RepositoryCreatedTestSuite):

    def setUp(self):
        super(TestRepositoryManagement, self).setUp()

    def tearDown(self):
        super(TestRepositoryManagement, self).tearDown()

    def test_modify_repository_profile(self):
        display_name_append = "v2"
        description_append = " (version 2)"

        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.org_member_username, self.org_member_password)

        self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/{}/settings/profile".format(self.org_id, self.repo_id)))
        display_name_form_fill = self.driver.find_element_by_id("displayName")
        description_form_fill = self.driver.find_element_by_id("description")
        # TODO:
        # test for display and modify `isPublic`

        self.assertEqual(self.driver.find_element_by_id("name").get_attribute("value"), self.repo_name)
        self.assertEqual(display_name_form_fill.get_attribute("value"), self.repo_display_name)
        self.assertEqual(description_form_fill.get_attribute("value"), self.repo_description)

        # To move the cursor to the very right. By unknow reasons
        # the cursor will starts to the right for "display_name_form_fill"
        # but will starts to the left for "email_form_fill".
        for i in range(50):
            display_name_form_fill.send_keys(Keys.ARROW_RIGHT)
            description_form_fill.send_keys(Keys.ARROW_RIGHT)
        display_name_form_fill.send_keys(display_name_append)
        description_form_fill.send_keys(description_append)
        display_name_form_fill.submit()

        self.assertEqual(urlparse(self.driver.current_url).path, "/organizations/{}/repositories/{}/settings/profile".format(self.org_id, self.repo_id))
        assert "Changes has been saved successfully!" in self.driver.page_source
        self.assertEqual(self.driver.find_element_by_id("displayName").get_attribute("value"), self.repo_display_name+display_name_append)
        self.assertEqual(self.driver.find_element_by_id("description").get_attribute("value"), self.repo_description+description_append)


if __name__ == '__main__':
    unittest.main()
