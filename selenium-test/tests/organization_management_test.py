import unittest
from urllib.parse import urlparse, urljoin
from selenium.webdriver.common.keys import Keys

from testsuites.organization_created_testsuite import OrganizationCreatedTestSuite
from forms.authorization_form import login_as
from forms.organization_management_form import fill_add_member_form


class TestOrganizationManagement(OrganizationCreatedTestSuite):

    def setUp(self):
        super(TestOrganizationManagement, self).setUp()

    def tearDown(self):
        super(TestOrganizationManagement, self).tearDown()

    def test_organization_not_listed_for_non_member(self):
        with login_as(self.driver, self.root_url, self.username, self.password):
            self.driver.get(urljoin(self.root_url, "/"))
            assert self.org_display_name not in self.driver.page_source

    def test_organization_member_cannot_access_setting(self):
        with login_as(self.driver, self.root_url, self.org_member_username, self.org_member_password):
            self.driver.get(urljoin(self.root_url, "/organizations/{}".format(self.org_id)))
            self.assertFalse(self.driver.find_elements_by_xpath("//form[@action='/organizations/{}/settings']".format(self.org_id)))

    def test_non_member_cannot_access_setting(self):
        with login_as(self.driver, self.root_url, self.username, self.password):
            self.driver.get(urljoin(self.root_url, "/organizations/{}".format(self.org_id)))
            self.assertFalse(self.driver.find_elements_by_xpath("//form[@action='/organizations/{}/settings']".format(self.org_id)))


class TestModifyOrganizationProfile(OrganizationCreatedTestSuite):

    def setUp(self):
        super(TestModifyOrganizationProfile, self).setUp()

    def tearDown(self):
        super(TestModifyOrganizationProfile, self).tearDown()

    def test_organization_manager_modify_organization_profile(self):
        display_name_append = " v2"

        with login_as(self.driver, self.root_url, self.org_manager_username, self.org_manager_password):
            self.driver.get(urljoin(self.root_url, "/organizations/{}/settings/profile".format(self.org_id)))
            display_name_form_fill = self.driver.find_element_by_id("displayName")

            self.assertEqual(self.driver.find_element_by_id("name").get_attribute("value"), self.org_name)
            self.assertEqual(display_name_form_fill.get_attribute("value"), self.org_display_name)

            for i in range(50):
                display_name_form_fill.send_keys(Keys.ARROW_RIGHT)
            display_name_form_fill.send_keys(display_name_append)
            display_name_form_fill.submit()

            self.assertEqual(urlparse(self.driver.current_url).path, "/organizations/{}/settings/profile".format(self.org_id))
            assert "Changes has been saved successfully!" in self.driver.page_source
            self.assertEqual(self.driver.find_element_by_id("displayName").get_attribute("value"), self.org_display_name+display_name_append)

    def test_organization_member_cannot_modify_organization_profile(self):
        display_name_append = " v2"

        with login_as(self.driver, self.root_url, self.org_member_username, self.org_member_password):
            self.driver.get(urljoin(self.root_url, "/organizations/{}/settings/profile".format(self.org_id)))
            display_name_form_fill = self.driver.find_element_by_id("displayName")

            display_name_form_fill.send_keys(display_name_append)
            display_name_form_fill.submit()
            assert "status=403" in self.driver.page_source


class TestModifyOrganizationMembers(OrganizationCreatedTestSuite):

    def setUp(self):
        super(TestModifyOrganizationMembers, self).setUp()

    def tearDown(self):
        super(TestModifyOrganizationMembers, self).tearDown()

    def test_organization_manager_add_member(self):
        with login_as(self.driver, self.root_url, self.org_manager_username, self.org_manager_password):
            self.driver.get(urljoin(self.root_url, "/organizations/{}".format(self.org_id)))
            self.assertTrue(self.driver.find_elements_by_xpath("//form[@action='/organizations/{}/settings']".format(self.org_id)))

            self.driver.get(urljoin(self.root_url, "organizations/{}/settings/members".format(self.org_id)))
            assert self.display_name not in self.driver.page_source

            fill_add_member_form(self.driver, self.username)

            self.assertEqual(urlparse(self.driver.current_url).path, "/organizations/{}/settings/members".format(self.org_id))
            assert self.display_name in self.driver.page_source

    def test_organization_normal_member_cannot_add_member(self):
        with login_as(self.driver, self.root_url, self.org_member_username, self.org_member_password):
            # TODO:
            # Should we forbidden it from the page level (do not let the user to access this
            # page), or from the method level (do not allow user to call the real "add user
            # to database" method)?
            self.driver.get(urljoin(self.root_url, "organizations/{}/settings/members".format(self.org_id)))
            fill_add_member_form(self.driver, self.username)

            assert "status=403" in self.driver.page_source

    def test_non_member_cannot_add_members(self):
        with login_as(self.driver, self.root_url, self.username, self.password):
            self.driver.get(urljoin(self.root_url, "organizations/{}/settings/members".format(self.org_id)))
            fill_add_member_form(self.driver, self.username)

            assert "status=403" in self.driver.page_source

    def test_organization_manager_remove_member(self):
        with login_as(self.driver, self.root_url, self.org_manager_username, self.org_manager_password):
            self.driver.get(urljoin(self.root_url, "organizations/{}/settings/members".format(self.org_id)))

            assert self.org_manager_display_name in self.driver.find_element_by_class_name("user").text
            assert self.org_member_display_name in self.driver.find_element_by_class_name("user-deletable").text

            form_start = self.driver.find_element_by_xpath(
                "//form[@action='/organizations/{}/settings/members/remove']/input[@name='{}']".format(
                    self.org_id, "to_be_remove_username"))
            self.assertEqual(form_start.get_attribute("value"), self.org_member_username)
            form_start.submit()

            self.assertEqual(urlparse(self.driver.current_url).path, "/organizations/{}/settings/members".format(self.org_id))
            assert self.org_manager_display_name in self.driver.find_element_by_class_name("user").text
            self.assertEqual(len(self.driver.find_elements_by_class_name("user-deletable")), 0)
            self.assertFalse(self.driver.find_elements_by_xpath("//form[@action='/organizations/{}/settings/members/remove']/input".format(self.org_id)))

    def test_organization_manager_add_manager(self):
        with login_as(self.driver, self.root_url, self.org_manager_username, self.org_manager_password):
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
