import os
import unittest
from selenium import webdriver
import shutil
from urllib.parse import urlparse

from settings.profile import profile


class BaseTestSuite(unittest.TestCase):

    @classmethod
    def setUpClass(cls):
        options = webdriver.ChromeOptions()
        options.add_argument("headless")
        cls.driver = webdriver.Chrome(options=options)

        cls.profile = profile
        cls.root_url = cls.profile.web_root_url

    @classmethod
    def tearDownClass(cls):
        cls.driver.close()

    def setUp(self):
        self._cleanup_local_git_sandbox()

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
        self._reset_folder_content(self.profile.local_git_sandbox_path)
