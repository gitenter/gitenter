import os
import unittest
import psycopg2
from psycopg2.extensions import ISOLATION_LEVEL_AUTOCOMMIT
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
        self._reset_database("gitenter", "gitenter_empty")
        self._cleanup_local_git_server()

    def tearDown(self):
        self.driver.close()

    def _reset_database(self, dbname, template_dbname):

        # TODO:
        # Makes database connection a singleton.
        #
        # TODO:
        # Use anybody other than "postgres" to do it. Currently everybody except
        # "postgres" will have error:
        # > psycopg2.OperationalError: terminating connection due to administrator command
        # > server closed the connection unexpectedly...
        conn_string = "host='{}' port='{}' user='postgres' password='postgres'".format(
            urlparse(self.config.postgres_root_url).hostname,
            urlparse(self.config.postgres_root_url).port
        )
        conn = psycopg2.connect(conn_string)
        conn.set_isolation_level(ISOLATION_LEVEL_AUTOCOMMIT)
        cursor = conn.cursor()

        cursor.execute(
            "SELECT pg_terminate_backend (pg_stat_activity.pid)"
            + " FROM pg_stat_activity"
            + " WHERE pg_stat_activity.datname = '{}';".format(dbname))
        cursor.execute("DROP DATABASE IF EXISTS {};".format(dbname))
        cursor.execute("CREATE DATABASE {} TEMPLATE {};".format(dbname, template_dbname))

        conn.close()

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
