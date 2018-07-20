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
        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.username, self.password)

        self.driver.get(urljoin(self.root_url, "/"))
        assert self.org_display_name not in self.driver.page_source

    def test_organization_member_cannot_access_setting(self):
        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.org_member_username, self.org_member_password)

        self.driver.get(urljoin(self.root_url, "/organizations/{}".format(self.org_id)))
        self.assertFalse(self.driver.find_elements_by_xpath("//form[@action='/organizations/{}/settings']".format(self.org_id)))

    def test_non_member_cannot_access_setting(self):
        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.username, self.password)

        self.driver.get(urljoin(self.root_url, "/organizations/{}".format(self.org_id)))
        self.assertFalse(self.driver.find_elements_by_xpath("//form[@action='/organizations/{}/settings']".format(self.org_id)))

    def test_organization_manager_add_member(self):
        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.org_manager_username, self.org_manager_password)

        self.driver.get(urljoin(self.root_url, "/organizations/{}".format(self.org_id)))
        self.assertTrue(self.driver.find_elements_by_xpath("//form[@action='/organizations/{}/settings']".format(self.org_id)))

        self.driver.get(urljoin(self.root_url, "organizations/{}/settings/members".format(self.org_id)))
        assert self.display_name not in self.driver.page_source

        form_start = self.driver.find_element_by_id("username")
        form_start.send_keys(self.username)
        form_start.submit()

        self.assertEqual(urlparse(self.driver.current_url).path, "/organizations/{}/settings/members".format(self.org_id))
        assert self.display_name in self.driver.page_source

    def test_organization_normal_member_cannot_add_member(self):
        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.org_member_username, self.org_member_password)

        # TODO:
        # Should we forbidden it from the page level (do not let the user to access this
        # page), or from the method level (do not allow user to call the real "add user
        # to database" method)?
        self.driver.get(urljoin(self.root_url, "organizations/{}/settings/members".format(self.org_id)))
        form_start = self.driver.find_element_by_id("username")
        form_start.send_keys(self.username)
        form_start.submit()

        assert "status=403" in self.driver.page_source

    def test_non_member_cannot_add_members(self):
        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.username, self.password)

        self.driver.get(urljoin(self.root_url, "organizations/{}/settings/members".format(self.org_id)))
        form_start = self.driver.find_element_by_id("username")
        form_start.send_keys(self.username)
        form_start.submit()

        assert "status=403" in self.driver.page_source

    def test_organization_manager_remove_member(self):
        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.org_manager_username, self.org_manager_password)

        self.driver.get(urljoin(self.root_url, "organizations/{}/settings/members".format(self.org_id)))
        form_start = self.driver.find_element_by_xpath("//form[@action='/organizations/{}/settings/members/remove']/input".format(self.org_id))
        self.assertEqual(form_start.get_attribute("value"), self.org_member_username)
        form_start.submit()

        self.assertEqual(urlparse(self.driver.current_url).path, "/organizations/{}/settings/members".format(self.org_id))
        self.assertFalse(self.driver.find_elements_by_xpath("//form[@action='/organizations/{}/settings/members/remove']/input".format(self.org_id)))

    def test_organization_manager_add_manager(self):
        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.org_manager_username, self.org_manager_password)

        self.driver.get(urljoin(self.root_url, "organizations/{}/settings/managers".format(self.org_id)))
        self.assertFalse(self.driver.find_elements_by_xpath("//form[@action='/organizations/{}/settings/managers/remove']/input".format(self.org_id)))
        form_start = self.driver.find_element_by_xpath("//form[@action='/organizations/{}/settings/managers/add']/input".format(self.org_id))
        self.assertEqual(form_start.get_attribute("value"), self.org_member_username)
        form_start.submit()

        self.assertEqual(urlparse(self.driver.current_url).path, "/organizations/{}/settings/managers".format(self.org_id))
        self.assertFalse(self.driver.find_elements_by_xpath("//form[@action='/organizations/{}/settings/managers/add']/input".format(self.org_id)))
        form_start = self.driver.find_element_by_xpath("//form[@action='/organizations/{}/settings/managers/remove']/input".format(self.org_id))
        self.assertEqual(form_start.get_attribute("value"), self.org_member_username)
        form_start.submit()

        self.assertEqual(urlparse(self.driver.current_url).path, "/organizations/{}/settings/managers".format(self.org_id))
        self.assertFalse(self.driver.find_elements_by_xpath("//form[@action='/organizations/{}/settings/managers/remove']/input".format(self.org_id)))
        self.assertTrue(self.driver.find_elements_by_xpath("//form[@action='/organizations/{}/settings/managers/add']/input".format(self.org_id)))


if __name__ == '__main__':
    unittest.main()
