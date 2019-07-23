from unittest import TestCase
from sqlalchemy.orm import sessionmaker

from settings.postgres import postgres_engine
from managers import SshKeyManager
from models import Member, SshKey


class TestSshKeyManager(TestCase):

    @classmethod
    def setUpClass(cls):
        cls.connection = postgres_engine().connect()

    @classmethod
    def tearDownClass(cls):
        cls.connection.close()

    def setUp(self):
        self.transaction = self.connection.begin()
        Session = sessionmaker(bind=self.connection)
        self.session = Session()

        member = Member(
            username="member",
            password="password",
            display_name="Member",
            email="member@member.com")
        SshKey(
            member=member,
            key_type="ssh-rsa",
            key_data="AAAAB3NzaC1yc",
            comment="key_1")
        SshKey(
            member=member,
            key_type="ssh-rsa",
            key_data="CFGrGDnSs+j7F",
            comment="key_2")

        self.session.add(member)
        self.session.commit()

    def tearDown(self):
        self.session.close()
        self.transaction.rollback()

    def test_plain_get_authorized_keys_content(self):
        desired_output_items = set([
            "ssh-rsa AAAAB3NzaC1yc key_1",
            "ssh-rsa CFGrGDnSs+j7F key_2"
        ])
        self.assertTrue(
            desired_output_items <=
            set(SshKeyManager.get_plain_authorized_keys_file_content(self.session).splitlines())
        )

    def test_force_command_get_authorized_keys_content(self):
        desired_output_items = set([
            """command="bash /ssheep/check_if_can_edit_repository.sh member",""" +
            "no-port-forwarding,no-x11-forwarding," +
            "no-agent-forwarding,no-pty ssh-rsa AAAAB3NzaC1yc key_1",
            """command="bash /ssheep/check_if_can_edit_repository.sh member",""" +
            "no-port-forwarding,no-x11-forwarding," +
            "no-agent-forwarding,no-pty ssh-rsa CFGrGDnSs+j7F key_2"
        ])
        self.assertTrue(
            desired_output_items <=
            set(SshKeyManager.get_force_command_authorized_keys_file_content(self.session).splitlines())
        )
