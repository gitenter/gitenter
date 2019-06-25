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
#
# TODO:
# Right now in docker (should be a Java/python decode mismatch):
# > Traceback (most recent call last):
# >   File "get_authorized_keys_content.py", line 24, in <module>
# >     print(SshKeyManager.get_plain_authorized_keys_file_content(session))
# >   File "/ssheep/managers.py", line 73, in get_plain_authorized_keys_file_content
# >     output += ssh_key.get_authorized_keys_line()
# >   File "/ssheep/models.py", line 126, in get_authorized_keys_line
# >     self.key_data.decode('ascii'),
# > UnicodeDecodeError: 'ascii' codec can't decode byte 0xd2 in position 23: ordinal not in range(128)
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
