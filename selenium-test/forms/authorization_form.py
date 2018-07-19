# class SignupForm():
#
#     def __init__(self):
#         pass
#
#     def fill(self, username, password, display_name, email):
#         self.username = username
#         self.password = password
#         self.display_name = display_name
#         self.email = email
#
#     def submit(self, driver):
#         form_start = driver.find_element_by_id("username")
#         form_start.send_keys(self.username)
#         driver.find_element_by_id("password").send_keys(self.password)
#         driver.find_element_by_id("displayName").send_keys(self.display_name)
#         driver.find_element_by_id("email").send_keys(self.email)
#
#         form_start.submit()


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
