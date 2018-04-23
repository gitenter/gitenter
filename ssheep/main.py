from sqlalchemy.orm import sessionmaker

from settings import *
from managers import *


if __name__ == "__main__":

    Session = sessionmaker(bind=postgres_engine())
    session = Session()

    print(RepositoryManager.get_by_name_and_organization_name(session, "fff", "aka").name)

"""
    for instance in session.query(Member):
        print(instance.username)

    for instance in session.query(Organization):
        for repository in instance.repositories:
            print(repository.name)
"""
