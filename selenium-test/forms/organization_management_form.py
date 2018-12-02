def fill_create_organization_form(driver, name, display_name):
    form_start = driver.find_element_by_id("name")
    form_start.send_keys(name)
    driver.find_element_by_id("displayName").send_keys(display_name)

    form_start.submit()


def fill_add_member_form(driver, username):
    form_start = driver.find_element_by_name("to_be_add_username")
    form_start.send_keys(username)
    form_start.submit()
