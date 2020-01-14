from contextlib import contextmanager
from selenium.common.exceptions import TimeoutException
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from urllib.parse import urljoin


def fill_signup_form(driver, username, password, display_name, email):
    form_start = driver.find_element_by_id("username")
    form_start.send_keys(username)
    driver.find_element_by_id("password").send_keys(password)
    driver.find_element_by_id("displayName").send_keys(display_name)
    driver.find_element_by_id("email").send_keys(email)

    form_start.submit()


def fill_login_form(driver, username, password, remember_me=False):
    form_start = driver.find_element_by_id("username")
    form_start.send_keys(username)
    driver.find_element_by_id("password").send_keys(password)

    # By default, "remember_me" is clicked. So here is to
    # unclick it.
    if not remember_me:
        driver.find_element_by_id("remember_me").click()

    form_start.submit()


def fill_delete_user_form(driver, password):
    form_start = driver.find_element_by_id("password")
    form_start.send_keys(password)
    form_start.submit()


# Can't just call the URL because then we don't have the associated CSRF key.
def click_logout(driver):
    form_start = driver.find_element_by_id("logout")
    form_start.submit()


@contextmanager
def login_as(driver, root_url, username, password, remember_me=False):
    driver.get(urljoin(root_url, "/login"))
    fill_login_form(driver, username, password, remember_me=remember_me)
    try:
        WebDriverWait(driver, 3).until(EC.url_changes(urljoin(root_url, "/login")))
    except TimeoutException:
        self.assertFalse(True, 'Login fails')

    yield

    driver.get(urljoin(root_url, "/"))
    click_logout(driver)
