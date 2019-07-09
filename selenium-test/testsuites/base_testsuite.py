import os
import unittest
from selenium import webdriver
import shutil
from urllib.parse import urlparse

from setup.config import (
    STSConfig
)


class BaseTestSuite(unittest.TestCase):

    def setUp(self):
        options = webdriver.ChromeOptions()
        options.add_argument("headless")
        self.driver = webdriver.Chrome(options=options)

        self.config = STSConfig()

        self.root_url = self.config.web_root_url
        self._cleanup_local_git_sandbox()

    def tearDown(self):
        self.driver.close()

    def _reset_folder_content(self, folderpath):
        os.makedirs(str(folderpath), exist_ok=True)

        for the_file in os.listdir(str(folderpath)):
            subpathstring = str(folderpath / the_file)
            if os.path.isfile(subpathstring):
                os.remove(subpathstring)
            else:
                shutil.rmtree(subpathstring)

        # if os.path.exists(str(folderpath)) and os.path.isdir(str(folderpath)):
        #     shutil.rmtree(str(folderpath))
        # folderpath.mkdir(mode=0o777, parents=False, exist_ok=False)

    def _cleanup_local_git_sandbox(self):
        self._reset_folder_content(self.config.local_git_sandbox_path)
