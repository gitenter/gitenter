"""
Check if a particular user can edit a repository belongs to some organization.

usage:
python3 check_if_can_edit_repository.py [username] [org_name] [repo_name]
"""
from sqlalchemy.orm import sessionmaker
import sys

from settings.postgres import postgres_engine
from managers import RepositoryMemberMapManager


if __name__ == "__main__":
    username = sys.argv[1]
    org_name = sys.argv[2]
    repo_name = sys.argv[3]

    connection = postgres_engine().connect()
    Session = sessionmaker(bind=connection)
    session = Session()

    print(RepositoryMemberMapManager.is_editable(session, username, org_name, repo_name))

    session.close()
    connection.close()
