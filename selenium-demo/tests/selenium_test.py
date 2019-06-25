from selenium import webdriver
from unittest import TestCase


class SeleniumTestCase(TestCase):

    @classmethod
    def setUpClass(cls):
        cls.driver = webdriver.Chrome(service_args=["--verbose", "--log-path=test-reports/chrome.log"])
        # cls.driver = webdriver.Chrome()

    @classmethod
    def tearDownClass(cls):
        cls.driver.close()

    def test_senaty(self):
        self.driver.get("http://www.python.org")
        assert "Python" in self.driver.title
