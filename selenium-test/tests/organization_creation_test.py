import unittest
from urllib.parse import urlparse, urljoin

from testsuites.organization_created_testsuite import OrganizationToBeCreatedTestSuite
from forms.authorization_form import (
    fill_login_form,
    click_logout
)
from forms.organization_management_form import (
    fill_create_organization_form,
    fill_delete_organization_form
)


class TestOrganizationCreation(OrganizationToBeCreatedTestSuite):

    def setUp(self):
        super(TestOrganizationCreation, self).setUp()

        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.org_manager_username, self.org_manager_password)

    def tearDown(self):
        click_logout(self.driver)

        super(TestOrganizationCreation, self).tearDown()

    def test_create_and_display_and_delete_organization(self):
        org_name = "org"
        org_display_name = "A Organization"

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

        # Delete organization
        self.driver.get(urljoin(self.root_url, "/organizations/{}/settings/delete").format(org_id))
        fill_delete_organization_form(self.driver, "wrong_org_name")
        self.assertEqual(urlparse(self.driver.current_url).path, "/organizations/{}/settings/delete".format(org_id))
        assert "Organization name doesn't match!" in self.driver.page_source

        # TODO:
        # May test e.g. organization member cannot delete organization, etc...

        self.driver.get(urljoin(self.root_url, "/organizations/{}/settings/delete").format(org_id))
        fill_delete_organization_form(self.driver, org_name)
        self.assertEqual(urlparse(self.driver.current_url).path, "/")
        assert org_display_name not in self.driver.page_source

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


if __name__ == '__main__':
    unittest.main()
