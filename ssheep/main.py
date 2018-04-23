from sqlalchemy.orm import sessionmaker

from settings import *
from model import *


if __name__ == "__main__":

    Session = sessionmaker(bind=postgres_engine())
    session = Session()

    for instance in session.query(Member):
        print(instance.username)

    for instance in session.query(Organization):
        for repository in instance.repositories:
            print(repository.name)

