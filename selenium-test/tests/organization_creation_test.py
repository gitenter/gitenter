import unittest
from urllib.parse import urlparse, urljoin
from selenium.common.exceptions import TimeoutException
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

from testsuites.organization_created_testsuite import OrganizationToBeCreatedTestSuite
from forms.authorization_form import login_as
from forms.organization_management_form import (
    fill_create_organization_form,
    fill_delete_organization_form
)


class TestOrganizationCreation(OrganizationToBeCreatedTestSuite):

    def setUp(self):
        super(TestOrganizationCreation, self).setUp()

    def tearDown(self):
        super(TestOrganizationCreation, self).tearDown()

    def test_create_and_display_and_delete_organization(self):
        org_name = "org"
        org_display_name = "A Organization"

        with login_as(self.driver, self.root_url, self.org_manager_username, self.org_manager_password):
            self.driver.get(urljoin(self.root_url, "/"))
            assert "Managed organizations" in self.driver.page_source
            assert org_display_name not in self.driver.page_source

            self.assertTrue(self.driver.find_elements_by_xpath("//form[@action='/organizations/create']"))
            self.driver.get(urljoin(self.root_url, "/organizations/create"))
            fill_create_organization_form(self.driver, org_name, org_display_name)
            try:
                element = WebDriverWait(self.driver, 3).until(EC.presence_of_element_located((By.CLASS_NAME, "success")))
            except TimeoutException:
                self.assertFalse(True, 'Expected successful message not raised')
            self.assertEqual(urlparse(self.driver.current_url).path, "/organizations/create")
            assert "Organization created!" in element.text

            self.driver.get(urljoin(self.root_url, "/"))
            WebDriverWait(self.driver, 3).until(EC.visibility_of_all_elements_located((By.ID, "managed-organizations")))
            assert org_display_name in self.driver.page_source

            org_link = self.driver.find_element_by_link_text(org_display_name).get_attribute("href")
            org_id = urlparse(org_link).path.split("/")[-1]

            self.driver.find_element_by_link_text(org_display_name).click()
            WebDriverWait(self.driver, 3).until(EC.url_changes(urljoin(self.root_url, "/")))
            self.assertEqual(urlparse(self.driver.current_url).path, "/organizations/{}".format(org_id))
            WebDriverWait(self.driver, 3).until(EC.visibility_of_all_elements_located((By.ID, "organization-members")))
            assert "Repositories" in self.driver.page_source
            assert "Members" in self.driver.page_source
            self.assertIn(self.org_manager_display_name, map(lambda x: x.text, self.driver.find_elements_by_class_name("user")))

            self.driver.get(urljoin(self.root_url, "/organizations/{}/settings/managers").format(org_id))
            WebDriverWait(self.driver, 3).until(EC.visibility_of_all_elements_located((By.CLASS_NAME, "user")))
            self.assertIn(self.org_manager_display_name, map(lambda x: x.text, self.driver.find_elements_by_class_name("user")))

            # TODO:
            # assert that the user cannot remove herself as a manager.

            self.driver.get(urljoin(self.root_url, "/organizations/{}/settings/members").format(org_id))
            WebDriverWait(self.driver, 3).until(EC.visibility_of_all_elements_located((By.CLASS_NAME, "user")))
            self.assertIn(self.org_manager_display_name, map(lambda x: x.text, self.driver.find_elements_by_class_name("user")))

            # Create another organization with the same name
            # self.driver.get(urljoin(self.root_url, "/organizations/create"))
            # name_crashing_org_display_name = "Another Organization with the Same Name"
            # fill_create_organization_form(self.driver, org_name, name_crashing_org_display_name)
            # self.assertEqual(urlparse(self.driver.current_url).path, "/organizations/create")
            # assert org_name in self.driver.page_source
            # assert name_crashing_org_display_name in self.driver.page_source
            # assert "name already exist!" in self.driver.page_source

            # Delete organization
            self.driver.get(urljoin(self.root_url, "/organizations/{}/settings/delete").format(org_id))
            fill_delete_organization_form(self.driver, "wrong_org_name")
            self.assertEqual(urlparse(self.driver.current_url).path, "/organizations/{}/settings/delete".format(org_id))
            try:
                element = WebDriverWait(self.driver, 3).until(EC.presence_of_element_located((By.CLASS_NAME, "error")))
            except TimeoutException:
                self.assertFalse(True, 'Expected error message not raised')
            assert "Organization name doesn't match!" in self.driver.page_source

            self.driver.get(urljoin(self.root_url, "/organizations/{}/settings/delete").format(org_id))
            fill_delete_organization_form(self.driver, org_name)
            try:
                WebDriverWait(self.driver, 3).until(EC.url_changes(urljoin(self.root_url, "/organizations/{}/settings/delete").format(org_id)))
            except TimeoutException:
                self.assertFalse(True, 'Cannot delete organization')
            self.assertEqual(urlparse(self.driver.current_url).path, "/")
            assert org_display_name not in self.driver.page_source

            # TODO:
            # May test e.g. organization member cannot delete organization, etc...
            # Right now although they don't get a link of "settings" they can still get the settings
            # page by hard code the URL. They'll be 403ed when they try to submit the POST request
            # (as it is forbidden in functional level). Thinking about a better way of testing it.


if __name__ == '__main__':
    unittest.main()
