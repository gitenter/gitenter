def add_ssh_key(driver, ssh_key):
    form_start = driver.find_element_by_id("sshKeyValue")
    form_start.send_keys(ssh_key)
    form_start.submit()
