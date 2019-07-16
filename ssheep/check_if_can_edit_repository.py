"""
Check if a particular user can edit a repository belongs to some organization.

usage:
python3 check_if_can_edit_repository.py [username] [org-name] [repo-name]
"""
from sqlalchemy.orm import sessionmaker

from settings.postgres import postgres_engine
from managers import SshKeyManager


# TODO:
# It actually have an input of a username, so we can only generate
# authorized_keys of that particular user. However, that user need to already
# exist in the OS. If we can get rid of it, we can accept that argument in
# here so it is possible for us to generate a small set of tests.
if __name__ == "__main__":
    connection = postgres_engine().connect()
    Session = sessionmaker(bind=connection)
    session = Session()

    # TODO:
    # After finished debugging SSH connection/git docker container, we should add force
    # command to the file.
    print(SshKeyManager.get_plain_authorized_keys_file_content(session))
    # print(SshKeyManager.get_force_command_authorized_keys_file_content(session))

    session.close()
    connection.close()
