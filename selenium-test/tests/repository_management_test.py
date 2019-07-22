import unittest
from urllib.parse import urlparse, urljoin
from selenium.webdriver.common.keys import Keys

from testsuites.repository_created_testsuite import RepositoryCreatedTestSuite
from forms.authorization_form import login_as
from forms.repository_management_form import fill_add_collaborator_form


class TestRepositoryManagement(RepositoryCreatedTestSuite):

    def setUp(self):
        super(TestRepositoryManagement, self).setUp()

    def tearDown(self):
        super(TestRepositoryManagement, self).tearDown()

    def test_repo_organizer_can_access_repo_setting(self):
        with login_as(self.driver, self.root_url, self.repo_organizer_username, self.repo_organizer_password):
            # TODO:
            # So this is actually in "Setup a new repository" page.
            # It is not proved that for a general index page (repo
            # with commit) this `Setting` is also displayed properly.
            self.driver.get(
                urljoin(self.root_url, "/organizations/{}/repositories/{}".format(self.org_id, self.repo_id)))
            self.assertTrue(
                self.driver.find_elements_by_xpath(
                    "//form[@action='/organizations/{}/repositories/{}/settings']".format(self.org_id, self.repo_id)))

    def test_non_repo_organizer_cannot_access_repo_setting(self):
        with login_as(self.driver, self.root_url, self.org_member_username, self.org_member_password):
            self.driver.get(
                urljoin(self.root_url, "/organizations/{}/repositories/{}".format(self.org_id, self.repo_id)))
            self.assertFalse(
                self.driver.find_elements_by_xpath(
                    "//form[@action='/organizations/{}/repositories/{}/settings']".format(self.org_id, self.repo_id)))


class TestModifyRepositoryProfile(RepositoryCreatedTestSuite):

    def setUp(self):
        super(TestModifyRepositoryProfile, self).setUp()

    def tearDown(self):
        super(TestModifyRepositoryProfile, self).tearDown()

    def test_repo_organizer_modify_repository_profile(self):
        display_name_append = " v2"
        description_append = " (version 2)"

        with login_as(self.driver, self.root_url, self.repo_organizer_username, self.repo_organizer_password):
            self.driver.get(urljoin(
                self.root_url,
                "/organizations/{}/repositories/{}/settings/profile".format(self.org_id, self.repo_id)))
            display_name_form_fill = self.driver.find_element_by_id("displayName")
            description_form_fill = self.driver.find_element_by_id("description")
            # TODO:
            # test for display and modify `isPublic`

            self.assertEqual(self.driver.find_element_by_id("name").get_attribute("value"), self.repo_name)
            self.assertEqual(display_name_form_fill.get_attribute("value"), self.repo_display_name)
            self.assertEqual(description_form_fill.get_attribute("value"), self.repo_description)

            for i in range(50):
                display_name_form_fill.send_keys(Keys.ARROW_RIGHT)
                description_form_fill.send_keys(Keys.ARROW_RIGHT)
            display_name_form_fill.send_keys(display_name_append)
            description_form_fill.send_keys(description_append)
            display_name_form_fill.submit()

            self.assertEqual(
                urlparse(self.driver.current_url).path,
                "/organizations/{}/repositories/{}/settings/profile".format(self.org_id, self.repo_id))
            assert "Changes has been saved successfully!" in self.driver.page_source
            self.assertEqual(
                self.driver.find_element_by_id("displayName").get_attribute("value"),
                self.repo_display_name+display_name_append)
            self.assertEqual(
                self.driver.find_element_by_id("description").get_attribute("value"),
                self.repo_description+description_append)

    def test_non_project_organizer_is_not_allowed_to_modify_repository_profile(self):
        with login_as(self.driver, self.root_url, self.org_member_username, self.org_member_password):
            self.driver.get(urljoin(
                self.root_url,
                "/organizations/{}/repositories/{}/settings/profile".format(self.org_id, self.repo_id)))
            display_name_form_fill = self.driver.find_element_by_id("displayName")
            display_name_form_fill.send_keys(" v2")
            display_name_form_fill.submit()
            assert "status=403" in self.driver.page_source


class TestModifyRepositoryCollaborator(RepositoryCreatedTestSuite):

    def setUp(self):
        super(TestModifyRepositoryCollaborator, self).setUp()

    def tearDown(self):
        super(TestModifyRepositoryCollaborator, self).tearDown()

    def test_repo_organizer_add_and_remove_collaborator(self):
        with login_as(self.driver, self.root_url, self.repo_organizer_username, self.repo_organizer_password):
            collaborator_url = urljoin(
                self.root_url,
                "/organizations/{}/repositories/{}/settings/collaborators".format(self.org_id, self.repo_id))
            self.driver.get(collaborator_url)

            assert self.repo_organizer_display_name in self.driver.find_element_by_class_name("user").text
            self.assertEqual(len(self.driver.find_elements_by_class_name("user-deletable")), 0)

            fill_add_collaborator_form(self.driver, self.org_member_username, "Document editor")

            self.assertEqual(self.driver.current_url, collaborator_url)
            assert self.repo_organizer_display_name in self.driver.find_element_by_class_name("user").text
            assert self.org_member_display_name in self.driver.find_element_by_class_name("user-deletable").text

            # `self.username` is not in that organization, therefore, shouldn't
            # be able to add as collaborator.
            self.driver.get(collaborator_url)
            fill_add_collaborator_form(self.driver, self.username, "Document editor")
            assert "status=500" in self.driver.page_source

            self.driver.get(collaborator_url)
            fill_add_collaborator_form(self.driver, "non_existing_username", "Document editor")
            assert "status=500" in self.driver.page_source

            self.driver.get(collaborator_url)
            form_start = self.driver.find_element_by_xpath(
                "//form[@action='/organizations/{}/repositories/{}/settings/collaborators/remove']/input[@name='{}']".format(
                    self.org_id, self.repo_id, "to_be_remove_username"))
            self.assertEqual(form_start.get_attribute("value"), self.org_member_username)
            form_start.submit()

            self.assertEqual(self.driver.current_url, collaborator_url)

            assert self.repo_organizer_display_name in self.driver.find_element_by_class_name("user").text
            self.assertEqual(len(self.driver.find_elements_by_class_name("user-deletable")), 0)
            self.assertFalse(self.driver.find_elements_by_xpath(
                "//form[@action='/organizations/{}/repositories/{}/settings/collaborators/remove']/input".format(
                    self.org_id, self.repo_id)))


if __name__ == '__main__':
    unittest.main()
