import unittest

from testsuites.registered_testsuite import RegisteredTestSuite


class RegisteredInitState(RegisteredTestSuite):

    def setUp(self):
        super(RegisteredInitState, self).setUp()

    def tearDown(self):
        super(RegisteredInitState, self).tearDown()

    def test_init(self):
        import pdb; pdb.set_trace()  # noqa: ignore=E702


if __name__ == '__main__':
    unittest.main()
