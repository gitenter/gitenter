from unittest import TestCase
from sqlalchemy.orm import sessionmaker

from settings import postgres_engine
from managers import SshKeyManager
from models import Member, SshKey


class TestAuthorizedKeys(TestCase):

    def setUp(self):
        Session = sessionmaker(bind=postgres_engine())
        self.session = Session()

    def test_authorized_keys(self):
        member = Member(username="member", password="password", display_name="Member", email="member@member.com")
        ssh_key_1 = SshKey(
            member=member,
            key_type="ssh-rsa",
            key_data=b"AAAAB3NzaC1yc",
            comment="key_1")
        ssh_key_1 = SshKey(
            member=member,
            key_type="ssh-rsa",
            key_data=b"CFGrGDnSs+j7F",
            comment="key_2")

        self.session.add(member)
        # Should not commit, otherwise (as we are without a rollback machanism)
        # this test will fail the 2nd run.
        # session.commit()

        desired_output = "ssh-rsa AAAAB3NzaC1yc key_1\nssh-rsa CFGrGDnSs+j7F key_2\n"
        self.assertEqual(
            SshKeyManager.get_authorized_keys_file_content(self.session),
            desired_output
        )
