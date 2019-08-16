from urllib.parse import urljoin, urlparse

from testsuites.organization_to_be_created_testsuite import OrganizationToBeCreatedTestSuite
from forms.authorization_form import (
    login_as,
    click_logout
)
from forms.organization_management_form import (
    fill_create_organization_form,
    fill_add_member_form,
    fill_delete_organization_form
)


class OrganizationCreatedTestSuite(OrganizationToBeCreatedTestSuite):

    def setUp(self):
        super(OrganizationCreatedTestSuite, self).setUp()

        self.org_name = "org"
        self.org_display_name = "A Organization"

        with login_as(self.driver, self.root_url, self.org_manager_username, self.org_manager_password):
            self.driver.get(urljoin(self.root_url, "/organizations/create"))
            fill_create_organization_form(self.driver, self.org_name, self.org_display_name)
            org_link = self.driver.find_element_by_link_text(self.org_display_name).get_attribute("href")

            self.org_id = urlparse(org_link).path.split("/")[-1]

            self.driver.get(urljoin(self.root_url, "organizations/{}/settings/members".format(self.org_id)))
            fill_add_member_form(self.driver, self.org_member_username)

    def tearDown(self):
        with login_as(self.driver, self.root_url, self.org_manager_username, self.org_manager_password):
            self.driver.get(urljoin(self.root_url, "/organizations/{}/settings/delete").format(self.org_id))
            fill_delete_organization_form(self.driver, self.org_name)

        super(OrganizationCreatedTestSuite, self).tearDown()
