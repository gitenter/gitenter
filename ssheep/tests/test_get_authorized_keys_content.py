from unittest import TestCase
from sqlalchemy.orm import sessionmaker

from settings import postgres_engine
from managers import SshKeyManager
from models import Member, SshKey


class TestSshKeyManager(TestCase):

    def setUp(self):
        Session = sessionmaker(bind=postgres_engine())
        self.session = Session()

        member = Member(
            username="member",
            password="password",
            display_name="Member",
            email="member@member.com")
        SshKey(
            member=member,
            key_type="ssh-rsa",
            key_data=b"AAAAB3NzaC1yc",
            comment="key_1")
        SshKey(
            member=member,
            key_type="ssh-rsa",
            key_data=b"CFGrGDnSs+j7F",
            comment="key_2")

        self.session.add(member)
        # Should not commit, otherwise (as we are without a rollback machanism)
        # this test will fail the 2nd run.
        # session.commit()

    def tearDown(self):
        self.session.close()

    def test_plain_get_authorized_keys_content(self):
        desired_output = "ssh-rsa AAAAB3NzaC1yc key_1\nssh-rsa CFGrGDnSs+j7F key_2\n"
        self.assertEqual(
            SshKeyManager.get_plain_authorized_keys_file_content(self.session),
            desired_output
        )

    def test_force_command_get_authorized_keys_content(self):
        desired_output = (
            """command="./git-authorization.sh member",no-port-forwarding,no-x11-forwarding,""" +
            "no-agent-forwarding,no-pty ssh-rsa AAAAB3NzaC1yc key_1\n" +
            """command="./git-authorization.sh member",no-port-forwarding,no-x11-forwarding,""" +
            "no-agent-forwarding,no-pty ssh-rsa CFGrGDnSs+j7F key_2\n"
        )
        self.assertEqual(
            SshKeyManager.get_force_command_authorized_keys_file_content(self.session),
            desired_output
        )
