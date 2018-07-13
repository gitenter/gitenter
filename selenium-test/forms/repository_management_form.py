def fill_create_repository_form(driver, name, display_name, description, is_public=True, include_setup_files=False):
    form_start = driver.find_element_by_id("name")
    form_start.send_keys(name)
    driver.find_element_by_id("displayName").send_keys(display_name)
    driver.find_element_by_id("description").send_keys(description)

    if not is_public:
        driver.find_element_by_id("isPublic2").click()

    if include_setup_files:
        driver.find_element_by_id("include_setup_files_no").click()

    form_start.submit()
