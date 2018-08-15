import unittest
from urllib.parse import urlparse, urljoin

from testsuite.registered_testsuite import RegisteredTestSuite
from forms.authorization_form import fill_login_form
from forms.organization_management_form import (
    fill_create_organization_form
)


class TestOrganizationCreation(RegisteredTestSuite):

    def setUp(self):
        super(TestOrganizationCreation, self).setUp()

    def tearDown(self):
        super(TestOrganizationCreation, self).tearDown()

    def test_create_organization_and_display_managed_organizations(self):
        org_name = "org"
        org_display_name = "A Organization"

        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.org_manager_username, self.org_manager_password)

        self.driver.get(urljoin(self.root_url, "/"))
        assert "Managed organizations" in self.driver.page_source
        assert org_display_name not in self.driver.page_source

        self.assertTrue(self.driver.find_elements_by_xpath("//form[@action='/organizations/create']"))
        self.driver.get(urljoin(self.root_url, "/organizations/create"))
        fill_create_organization_form(self.driver, org_name, org_display_name)

        self.assertEqual(urlparse(self.driver.current_url).path, "/")
        assert org_display_name in self.driver.page_source

        org_link = self.driver.find_element_by_link_text(org_display_name).get_attribute("href")
        org_id = urlparse(org_link).path.split("/")[-1]

        self.driver.find_element_by_link_text(org_display_name).click()
        self.assertEqual(urlparse(self.driver.current_url).path, "/organizations/{}".format(org_id))
        assert "Repositories" in self.driver.page_source
        assert "Members" in self.driver.page_source
        self.assertIn(self.org_manager_display_name, map(lambda x: x.text, self.driver.find_elements_by_class_name("user")))

        self.driver.get(urljoin(self.root_url, "/organizations/{}/settings/managers").format(org_id))
        self.assertIn(self.org_manager_display_name, map(lambda x: x.text, self.driver.find_elements_by_class_name("user")))

        # TODO:
        # assert that the user cannot remove herself as a manager.

        self.driver.get(urljoin(self.root_url, "/organizations/{}/settings/members").format(org_id))
        self.assertIn(self.org_manager_display_name, map(lambda x: x.text, self.driver.find_elements_by_class_name("user")))

    def test_create_organization_invalid_input(self):
        org_name = "o"
        org_display_name = "O"

        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.org_manager_username, self.org_manager_password)

        self.driver.get(urljoin(self.root_url, "/organizations/create"))
        fill_create_organization_form(self.driver, org_name, org_display_name)

        self.assertEqual(urlparse(self.driver.current_url).path, "/organizations/create")
        assert "size" in self.driver.find_element_by_id("name.errors").text
        assert "size" in self.driver.find_element_by_id("displayName.errors").text

    def test_create_organization_name_already_exists(self):
        pass


if __name__ == '__main__':
    unittest.main()
