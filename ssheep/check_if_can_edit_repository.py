"""
Check if a particular user can edit a repository belongs to some organization.

usage:
python3 check_if_can_edit_repository.py [username] [repo_path]

`repo_path` can have form of either `/home/git/org/repo.git` or `org/repo.git`
"""
from sqlalchemy.orm import sessionmaker
import re
import sys

from settings.postgres import postgres_engine
from managers import RepositoryMemberMapManager


def parse_repo_path(repo_path):
    match = re.search('/home/git/(.+)/(.+).git', repo_path)
    if match:
        org_name = match.group(1)
        repo_name = match.group(2)
        return (org_name, repo_name)

    match = re.search('(.+)/(.+).git', repo_path)
    if match:
        org_name = match.group(1)
        repo_name = match.group(2)
        return (org_name, repo_name)


if __name__ == "__main__":
    username = sys.argv[1]
    repo_path = sys.argv[2]

    org_repo_pair = parse_repo_path(repo_path)
    if not org_repo_pair:
        print(False)
    (org_name, repo_name) = org_repo_pair

    connection = postgres_engine().connect()
    Session = sessionmaker(bind=connection)
    session = Session()

    print(RepositoryMemberMapManager.is_editable(session, username, org_name, repo_name))

    session.close()
    connection.close()
