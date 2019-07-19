from unittest import TestCase
from sqlalchemy.orm import sessionmaker

from settings.postgres import postgres_engine
from managers import RepositoryMemberMapManager
from models import (
    Member,
    Organization,
    OrganizationMemberMap,
    Repository,
    RepositoryMemberMap
)


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

    def tearDown(self):
        self.session.close()
        self.transaction.rollback()

    def test_repo_editor_can_editor_public_repo(self):
        member = Member(
            username="member",
            password="password",
            display_name="Member",
            email="member@member.com")
        org = Organization(
            name="org",
            display_name="Organization")
        OrganizationMemberMap(
            organization=org,
            member=member,
            role_shortname='M')
        repo = Repository(
            organization=org,
            name="repo",
            display_name="A Repository",
            is_public=True
        )
        RepositoryMemberMap(
            repository=repo,
            member=member,
            role_shortname='E'
        )

        self.session.add(member)
        self.session.commit()

        self.assertTrue(
            RepositoryMemberMapManager.is_editable(self.session, "member", "org", "repo"))

    def test_repo_editor_can_editor_private_repo(self):
        member = Member(
            username="member",
            password="password",
            display_name="Member",
            email="member@member.com")
        org = Organization(
            name="org",
            display_name="Organization")
        OrganizationMemberMap(
            organization=org,
            member=member,
            role_shortname='M')
        repo = Repository(
            organization=org,
            name="repo",
            display_name="A Repository",
            is_public=False
        )
        RepositoryMemberMap(
            repository=repo,
            member=member,
            role_shortname='E'
        )

        self.session.add(member)
        self.session.commit()

        self.assertTrue(
            RepositoryMemberMapManager.is_editable(self.session, "member", "org", "repo"))

    def test_org_member_cannot_editor_public_repo(self):
        member = Member(
            username="member",
            password="password",
            display_name="Member",
            email="member@member.com")
        org = Organization(
            name="org",
            display_name="Organization")
        OrganizationMemberMap(
            organization=org,
            member=member,
            role_shortname='M')
        repo = Repository(
            organization=org,
            name="repo",
            display_name="A Repository",
            is_public=True
        )

        self.session.add(member)
        self.session.commit()

        self.assertFalse(
            RepositoryMemberMapManager.is_editable(self.session, "member", "org", "repo"))

    def test_org_member_cannot_editor_private_repo(self):
        member = Member(
            username="member",
            password="password",
            display_name="Member",
            email="member@member.com")
        org = Organization(
            name="org",
            display_name="Organization")
        OrganizationMemberMap(
            organization=org,
            member=member,
            role_shortname='M')
        repo = Repository(
            organization=org,
            name="repo",
            display_name="A Repository",
            is_public=False
        )

        self.session.add(member)
        self.session.commit()

        self.assertFalse(
            RepositoryMemberMapManager.is_editable(self.session, "member", "org", "repo"))

    def test_nonmember_cannot_editor_public_repo(self):
        member = Member(
            username="member",
            password="password",
            display_name="Member",
            email="member@member.com")
        org = Organization(
            name="org",
            display_name="Organization")
        repo = Repository(
            organization=org,
            name="repo",
            display_name="A Repository",
            is_public=True
        )

        self.session.add(member)
        self.session.commit()

        self.assertFalse(
            RepositoryMemberMapManager.is_editable(self.session, "member", "org", "repo"))

    def test_org_member_cannot_editor_private_repo(self):
        member = Member(
            username="member",
            password="password",
            display_name="Member",
            email="member@member.com")
        org = Organization(
            name="org",
            display_name="Organization")
        repo = Repository(
            organization=org,
            name="repo",
            display_name="A Repository",
            is_public=False
        )

        self.session.add(member)
        self.session.commit()

        self.assertFalse(
            RepositoryMemberMapManager.is_editable(self.session, "member", "org", "repo"))
