def fill_create_organization_form(driver, name, display_name):
    form_start = driver.find_element_by_id("name")
    form_start.send_keys(name)
    driver.find_element_by_id("displayName").send_keys(display_name)

    form_start.submit()
