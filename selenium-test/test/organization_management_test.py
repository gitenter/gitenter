import unittest
from urllib.parse import urlparse, urljoin

from test.testcase import GitEnterTest
from forms.authorization_form import (
    fill_signup_form,
    fill_login_form
)
from forms.organization_management_form import (
    fill_create_organization_form
)


class TestOrganizationManagement(GitEnterTest):

    def setUp(self):
        super(TestOrganizationManagement, self).setUp()

        username = "username"
        password = "password"
        display_name = "User Name"
        email = "username@email.com"

        another_username = "another_username"
        another_password = "another_password"
        another_display_name = "Another User Name"
        another_email = "another_username@email.com"

        self.driver.get(urljoin(self.root_url, "register"))
        fill_signup_form(self.driver, username, password, display_name, email)

        self.driver.get(urljoin(self.root_url, "register"))
        fill_signup_form(self.driver, another_username, another_password, another_display_name, another_email)

        self.driver.get(urljoin(self.root_url, "login"))
        fill_login_form(self.driver, username, password)

        self.username = username
        self.display_name = display_name

        self.another_username = another_username
        self.another_password = another_password
        self.another_display_name = another_display_name

    def tearDown(self):
        super(TestOrganizationManagement, self).tearDown()

    def test_create_organization_and_display_managed_organizations(self):
        org_name = "another_org"
        org_display_name = "Another Organization"

        self.driver.get(urljoin(self.root_url, "/"))
        assert "Managed organizations" in self.driver.page_source
        assert org_display_name not in self.driver.page_source

        self.assertTrue(self.driver.find_elements_by_xpath("//form[@action='/organizations/create']"))
        self.driver.get(urljoin(self.root_url, "/organizations/create"))
        fill_create_organization_form(self.driver, org_name, org_display_name)

        self.assertEqual(urlparse(self.driver.current_url).path, "/")
        assert org_display_name in self.driver.page_source

        self.driver.find_element_by_link_text(org_display_name).click()
        assert "Repositories" in self.driver.page_source
        assert "Managers" in self.driver.page_source
        self.assertIn(self.display_name, map(lambda x: x.text, self.driver.find_elements_by_class_name("user")))

    def test_create_organization_invalid_input(self):
        org_name = "o"
        org_display_name = "O"

        self.driver.get(urljoin(self.root_url, "/organizations/create"))
        fill_create_organization_form(self.driver, org_name, org_display_name)

        self.assertEqual(urlparse(self.driver.current_url).path, "/organizations/create")
        assert "size" in self.driver.find_element_by_id("name.errors").text
        assert "size" in self.driver.find_element_by_id("displayName.errors").text

    def test_create_organization_name_already_exists(self):
        pass

    def test_organization_not_listed_for_non_member(self):
        org_name = "another_org"
        org_display_name = "Another Organization"

        self.driver.get(urljoin(self.root_url, "/organizations/create"))
        fill_create_organization_form(self.driver, org_name, org_display_name)

        self.driver.get(urljoin(self.root_url, "logout"))

        self.driver.get(urljoin(self.root_url, "login"))
        fill_login_form(self.driver, self.another_username, self.another_password)
        self.driver.get(urljoin(self.root_url, "/"))
        assert org_display_name not in self.driver.page_source


if __name__ == '__main__':
    unittest.main()
