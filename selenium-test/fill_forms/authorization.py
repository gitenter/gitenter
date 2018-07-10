def signup_fill_form(driver, username, password, display_name, email):
    form_start = driver.find_element_by_id("username")
    form_start.send_keys(username)
    driver.find_element_by_id("password").send_keys(password)
    driver.find_element_by_id("displayName").send_keys(display_name)
    driver.find_element_by_id("email").send_keys(email)

    form_start.submit()


def login_fill_form(driver, username, password, remember_me=False):
    form_start = driver.find_element_by_id("username")
    form_start.send_keys(username)
    driver.find_element_by_id("password").send_keys(password)
    if remember_me:
        driver.find_element_by_id("remember_me").click()

    form_start.submit()
