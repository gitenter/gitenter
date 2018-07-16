import unittest
from urllib.parse import urlparse, urljoin

from testsuite.organization_created_testsuite import OrganizationCreatedTestSuite
from forms.authorization_form import fill_login_form


class TestOrganizationManagement(OrganizationCreatedTestSuite):

    def setUp(self):
        super(TestOrganizationManagement, self).setUp()

    def tearDown(self):
        super(TestOrganizationManagement, self).tearDown()

    def test_organization_not_listed_for_non_member(self):
        self.driver.get(urljoin(self.root_url, "logout"))

        self.driver.get(urljoin(self.root_url, "login"))
        fill_login_form(self.driver, self.another_username, self.another_password)
        self.driver.get(urljoin(self.root_url, "/"))
        assert self.org_display_name not in self.driver.page_source

    def test_organization_add_member(self):
        self.driver.get(urljoin(self.root_url, "organizations/{}/settings/members".format(self.org_id)))
        assert self.another_display_name not in self.driver.page_source

        form_start = self.driver.find_element_by_id("username")
        form_start.send_keys(self.another_username)
        form_start.submit()

        self.assertEqual(urlparse(self.driver.current_url).path, "/organizations/{}/settings/members".format(self.org_id))
        assert self.another_display_name in self.driver.page_source


if __name__ == '__main__':
    unittest.main()
