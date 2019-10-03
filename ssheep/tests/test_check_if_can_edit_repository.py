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
from check_if_can_edit_repository import parse_repo_path


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
        Repository(
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
        Repository(
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
        Repository(
            organization=org,
            name="repo",
            display_name="A Repository",
            is_public=True
        )

        self.session.add(member)
        self.session.commit()

        self.assertFalse(
            RepositoryMemberMapManager.is_editable(self.session, "member", "org", "repo"))

    def test_nonmember_cannot_editor_private_repo(self):
        member = Member(
            username="member",
            password="password",
            display_name="Member",
            email="member@member.com")
        org = Organization(
            name="org",
            display_name="Organization")
        Repository(
            organization=org,
            name="repo",
            display_name="A Repository",
            is_public=False
        )

        self.session.add(member)
        self.session.commit()

        self.assertFalse(
            RepositoryMemberMapManager.is_editable(self.session, "member", "org", "repo"))


class TestParseRepoPath(TestCase):

    def test_parse_repo_path_with_absolute_path(self):
        repo_path = "/home/git/org/repo.git"
        self.assertTrue(parse_repo_path(repo_path), ("org", "repo"))

    def test_parse_repo_path_with_relative_path(self):
        repo_path = "org/repo.git"
        self.assertTrue(parse_repo_path(repo_path), ("org", "repo"))

    def test_parse_repo_path_with_invalid_absolute_path(self):
        repo_path = "/invalid/repo/path.git"
        self.assertTrue(parse_repo_path(repo_path), None)

    def test_parse_repo_path_with_invalid_relative_path(self):
        repo_path = "invalid/repo/path.git"
        self.assertTrue(parse_repo_path(repo_path), None)
