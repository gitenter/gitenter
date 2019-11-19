from unittest import TestCase
from sqlalchemy.orm import sessionmaker

from settings.postgres import postgres_engine
from managers import RepositoryUserMapManager
from models import (
    User,
    Organization,
    OrganizationUserMap,
    Repository,
    RepositoryUserMap
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
        user = User(
            username="username",
            password="password",
            display_name="Display Name",
            email="username@company.com")
        org = Organization(
            name="org",
            display_name="Organization")
        OrganizationUserMap(
            organization=org,
            user=user,
            role_shortname='M')
        repo = Repository(
            organization=org,
            name="repo",
            display_name="A Repository",
            is_public=True
        )
        RepositoryUserMap(
            repository=repo,
            user=user,
            role_shortname='E'
        )

        self.session.add(user)
        self.session.commit()

        self.assertTrue(
            RepositoryUserMapManager.is_editable(self.session, "username", "org", "repo"))

    def test_repo_editor_can_editor_private_repo(self):
        user = User(
            username="username",
            password="password",
            display_name="Display Name",
            email="username@company.com")
        org = Organization(
            name="org",
            display_name="Organization")
        OrganizationUserMap(
            organization=org,
            user=user,
            role_shortname='M')
        repo = Repository(
            organization=org,
            name="repo",
            display_name="A Repository",
            is_public=False
        )
        RepositoryUserMap(
            repository=repo,
            user=user,
            role_shortname='E'
        )

        self.session.add(user)
        self.session.commit()

        self.assertTrue(
            RepositoryUserMapManager.is_editable(self.session, "username", "org", "repo"))

    def test_org_member_cannot_editor_public_repo(self):
        user = User(
            username="username",
            password="password",
            display_name="Display Name",
            email="username@company.com")
        org = Organization(
            name="org",
            display_name="Organization")
        OrganizationUserMap(
            organization=org,
            user=user,
            role_shortname='M')
        Repository(
            organization=org,
            name="repo",
            display_name="A Repository",
            is_public=True
        )

        self.session.add(user)
        self.session.commit()

        self.assertFalse(
            RepositoryUserMapManager.is_editable(self.session, "username", "org", "repo"))

    def test_org_member_cannot_editor_private_repo(self):
        user = User(
            username="username",
            password="password",
            display_name="Display Name",
            email="username@company.com")
        org = Organization(
            name="org",
            display_name="Organization")
        OrganizationUserMap(
            organization=org,
            user=user,
            role_shortname='M')
        Repository(
            organization=org,
            name="repo",
            display_name="A Repository",
            is_public=False
        )

        self.session.add(user)
        self.session.commit()

        self.assertFalse(
            RepositoryUserMapManager.is_editable(self.session, "username", "org", "repo"))

    def test_nonmember_cannot_editor_public_repo(self):
        user = User(
            username="username",
            password="password",
            display_name="Display Name",
            email="username@company.com")
        org = Organization(
            name="org",
            display_name="Organization")
        Repository(
            organization=org,
            name="repo",
            display_name="A Repository",
            is_public=True
        )

        self.session.add(user)
        self.session.commit()

        self.assertFalse(
            RepositoryUserMapManager.is_editable(self.session, "username", "org", "repo"))

    def test_nonmember_cannot_editor_private_repo(self):
        user = User(
            username="username",
            password="password",
            display_name="Display Name",
            email="username@company.com")
        org = Organization(
            name="org",
            display_name="Organization")
        Repository(
            organization=org,
            name="repo",
            display_name="A Repository",
            is_public=False
        )

        self.session.add(user)
        self.session.commit()

        self.assertFalse(
            RepositoryUserMapManager.is_editable(self.session, "username", "org", "repo"))


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
