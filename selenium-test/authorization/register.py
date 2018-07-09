import unittest
import random
# import urlparse

from selenium import webdriver

root_url = "http://localhost:8887"


class TestSignUp(unittest.TestCase):

    def test_register(self):

        distingisher = str(random.randint(0, 10000))

        username = "username"+distingisher
        password = "password"
        display_name = "User Name "+distingisher
        email = "username"+distingisher+"@email.com"

        driver = webdriver.Chrome()
        driver.get(root_url+"/register")
        # driver.get(urlparse.urljoin(root_url, "register"))

        assert "GitEnter" in driver.title

        username_form = driver.find_element_by_id("username")
        username_form.send_keys(username)
        password_form = driver.find_element_by_id("password")
        password_form.send_keys(password)
        display_name_form = driver.find_element_by_id("displayName")
        display_name_form.send_keys(display_name)
        email_form = driver.find_element_by_id("email")
        email_form.send_keys(email)

        username_form.submit()

        # assert "Sign Up" in driver.title
        # elem = driver.find_element_by_name("q")
        # elem.clear()
        # elem.send_keys("pycon")
        # elem.send_keys(Keys.RETURN)
        # assert "No results found." not in driver.page_source
        driver.close()


if __name__ == '__main__':
    unittest.main()
