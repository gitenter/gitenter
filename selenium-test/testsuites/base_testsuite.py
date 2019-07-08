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
        self.driver = webdriver.Chrome()

        self.config = STSConfig()

        self.root_url = self.config.web_root_url
        self._cleanup_local_git_server()
        self._setup_ssh()

    def tearDown(self):
        self.driver.close()

    # TODO:
    # There's currently a bug in here. Our gitar class `GitRepository`
    # build a list of existing repository and lazily reload. However, all
    # our tests are executed without the server being restarted, so
    # `GitRepository` will not know that in there the directories got delete,
    # but still believe the repo is in there. Therefore, it will NOT re-build
    # the repo, and if you go inside you will see an empty repo (with possibly
    # hooks).
    #
    # Possible ways:
    # (1) In test create repos with different names each time to avoid name crashing.
    # (2) Actually in `OrbanizationManagerServiceImpl.createRepository()` there's
    # always the case we want to re-create the repo (actually we should raise an
    # error if the repo is already in there). Make the application correct in that
    # sense.
    #
    # Should prefer to do (2).
    def _remove_folder_content(self, folderpath):
        for the_file in os.listdir(str(folderpath)):
            subpathstring = str(folderpath / the_file)
            if os.path.isfile(subpathstring):
                os.remove(subpathstring)
            else:
                shutil.rmtree(subpathstring)

        # if os.path.exists(str(folderpath)) and os.path.isdir(str(folderpath)):
        #     shutil.rmtree(str(folderpath))
        # folderpath.mkdir(mode=0o777, parents=False, exist_ok=False)

    def _cleanup_local_git_server(self):
        self._remove_folder_content(self.config.git_server_root_path)
        self._remove_folder_content(self.config.sandbox_path)

    def _setup_ssh(self):
        if os.path.exists(str(self.config.ssh_server_patch)):
            shutil.rmtree(self.config.ssh_server_patch)

        self.config.ssh_server_patch.mkdir(mode=0o777, parents=True, exist_ok=False)
