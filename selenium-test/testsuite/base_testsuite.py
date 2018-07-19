import unittest
import psycopg2
from psycopg2.extensions import ISOLATION_LEVEL_AUTOCOMMIT
from selenium import webdriver
from urllib.parse import urlparse

from setup.config import (
    STSConfig
)


class BaseTestSuite(unittest.TestCase):

    def setUp(self):
        self.driver = webdriver.Chrome()

        self.config = STSConfig()

        self.root_url = self.config.web_root_url
        self._reset_database("enterovirus", "enterovirus_empty")

    def tearDown(self):
        self.driver.close()

    def _reset_database(self, dbname, template_dbname):

        # TODO:
        # Makes database connection a singleton.
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
