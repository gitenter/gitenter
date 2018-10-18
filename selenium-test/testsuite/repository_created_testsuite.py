from urllib.parse import urljoin, urlparse

from testsuite.organization_created_testsuite import OrganizationCreatedTestSuite
from forms.authorization_form import fill_login_form
from forms.repository_management_form import fill_create_repository_form


class RepositoryCreatedTestSuite(OrganizationCreatedTestSuite):

    def setUp(self):
        super(RepositoryCreatedTestSuite, self).setUp()

        self.repo_name = "repo"
        self.repo_display_name = "A Repository"
        self.repo_description = "A Repository Description"

        self.driver.get(urljoin(self.root_url, "/login"))
        fill_login_form(self.driver, self.org_member_username, self.org_member_password)

        self.driver.get(urljoin(self.root_url, "/organizations/{}/repositories/create".format(self.org_id)))
        fill_create_repository_form(self.driver, self.repo_name, self.repo_display_name, self.repo_description, is_public=False)
        repo_link = self.driver.find_element_by_link_text(self.repo_display_name).get_attribute("href")

        self.repo_id = urlparse(repo_link).path.split("/")[-1]

        self.driver.get(urljoin(self.root_url, "/logout"))

    def tearDown(self):
        super(RepositoryCreatedTestSuite, self).tearDown()
