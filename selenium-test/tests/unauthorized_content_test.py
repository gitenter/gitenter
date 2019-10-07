import unittest
from urllib.parse import urlparse, urljoin

from testsuites.base_testsuite import BaseTestSuite


class TestUnauthorizedContent(BaseTestSuite):

    def setUp(self):
        super(TestUnauthorizedContent, self).setUp()

    def tearDown(self):
        super(TestUnauthorizedContent, self).tearDown()

    def test_redirect_to_login_for_authorized_page_and_login_page_content(self):
        self.driver.get(urljoin(self.root_url, "/"))
        self.assertEqual(urlparse(self.driver.current_url).path, "/login")
        assert "Log in" in self.driver.page_source
        assert "Sign up" in self.driver.page_source

    def test_switching_between_static_and_dynamic_content(self):
        self.driver.get(urljoin(self.root_url, "/"))

        self.driver.find_element_by_link_text("About").click()
        self.assertEqual(urlparse(self.driver.current_url).path, "/about/")
        assert "About" in self.driver.page_source

        self.driver.find_element_by_link_text("Contact").click()
        self.assertEqual(urlparse(self.driver.current_url).path, "/contact/")
        assert "Contact" in self.driver.page_source

        self.assertTrue(self.driver.find_elements_by_xpath("//form[@action='/help/']"))
        self.driver.get(urljoin(self.root_url, "/help/"))
        assert "Help" in self.driver.page_source

        self.assertTrue(self.driver.find_elements_by_xpath("//form[@action='/pricing/']"))
        self.driver.get(urljoin(self.root_url, "/pricing/"))
        assert "Pricing" in self.driver.page_source


if __name__ == '__main__':
    unittest.main()
