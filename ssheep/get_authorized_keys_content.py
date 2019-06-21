"""
Generating `.ssh/authorized_keys` content to fill in  `AuthorizedKeysCommand`
setting up in `sshd_config`.
"""
from sqlalchemy.orm import sessionmaker

from settings import postgres_engine
from managers import SshKeyManager


# TODO:
# It actually have an input of a username, so we can only generate
# authorized_keys of that particular user. However, that user need to already
# exist in the OS. If we can get rid of it, we can accept that argument in
# here so it is possible for us to generate a small set of tests.
if __name__ == "__main__":
    Session = sessionmaker(bind=postgres_engine())
    session = Session()

    # TODO:
    # After finished debugging SSH connection/git docker container, we should add force
    # command to the file.
    print(SshKeyManager.get_plain_authorized_keys_file_content(session))
    # print(SshKeyManager.get_force_command_authorized_keys_file_content(session))

    session.close()
