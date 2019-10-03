import unittest

from testsuites.organization_created_testsuite import OrganizationCreatedTestSuite


class OrganizationCreatedInitState(OrganizationCreatedTestSuite):

    def setUp(self):
        super(OrganizationCreatedInitState, self).setUp()

    def tearDown(self):
        super(OrganizationCreatedInitState, self).tearDown()

    def test_init(self):
        import pdb; pdb.set_trace()  # noqa: ignore=E702


if __name__ == '__main__':
    unittest.main()
