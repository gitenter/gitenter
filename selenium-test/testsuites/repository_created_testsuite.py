from random import randint
from urllib.parse import urljoin, urlparse

from testsuites.repository_to_be_created_testsuite import RepositoryToBeCreatedTestSuite
from forms.authorization_form import (
    fill_login_form,
    click_logout
)
from forms.repository_management_form import (
    fill_create_repository_form,
    fill_delete_repository_form
)


class RepositoryCreatedTestSuite(RepositoryToBeCreatedTestSuite):

    def setUp(self):
        super(RepositoryCreatedTestSuite, self).setUp()

        self.repo_name = "repo-{}".format(randint(1, 10**10))
        self.repo_display_name = "A Repository"
        self.repo_description = "A Repository Description"

        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.repo_organizer_username, self.repo_organizer_password)

        self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/create".format(self.org_id)))
        fill_create_repository_form(self.driver, self.repo_name, self.repo_display_name, self.repo_description, is_public=False)
        repo_link = self.driver.find_element_by_link_text(self.repo_display_name).get_attribute("href")

        self.repo_id = urlparse(repo_link).path.split("/")[-1]

        click_logout(self.driver)

    def tearDown(self):
        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.repo_organizer_username, self.repo_organizer_password)

        self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/{}/settings/delete".format(self.org_id, self.repo_id)))
        fill_delete_repository_form(self.driver, self.repo_name)

        click_logout(self.driver)

        super(RepositoryCreatedTestSuite, self).tearDown()
