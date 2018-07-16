from urllib.parse import urljoin, urlparse

from testsuite.registered_testsuite import RegisteredTestSuite
from forms.organization_management_form import (
    fill_create_organization_form
)


class OrganizationCreatedTestSuite(RegisteredTestSuite):

    def setUp(self):
        super(OrganizationCreatedTestSuite, self).setUp()

        org_name = "another_org"
        org_display_name = "Another Organization"

        self.driver.get(urljoin(self.root_url, "/organizations/create"))
        fill_create_organization_form(self.driver, org_name, org_display_name)
        org_link = self.driver.find_element_by_link_text(org_display_name).get_attribute("href")

        self.org_name = org_name
        self.org_display_name = org_display_name
        self.org_id = urlparse(org_link).path.split("/")[-1]

    def tearDown(self):
        super(OrganizationCreatedTestSuite, self).tearDown()
