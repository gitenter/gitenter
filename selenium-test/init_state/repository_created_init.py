import unittest

from testsuites.repository_created_testsuite import RepositoryCreatedTestSuite


class RepositoryCreatedInitState(RepositoryCreatedTestSuite):

    def setUp(self):
        super(RepositoryCreatedInitState, self).setUp()

    def tearDown(self):
        super(RepositoryCreatedInitState, self).tearDown()

    def test_init(self):
        import pdb; pdb.set_trace()  # noqa: ignore=E702


if __name__ == '__main__':
    unittest.main()
