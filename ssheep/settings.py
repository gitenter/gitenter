from sqlalchemy import create_engine


class Database():
    username = "gitenter_app"
    password = "zooo"
    dbname = "gitenter"


class LocalDatabase(Database):
    url = "localhost"


class DockerDatabase(Database):
    url = "database"


class QaDatabase(Database):
    url = "qa-postgres.cqx7dy9nh94t.us-east-1.rds.amazonaws.com"


class ProductionDatabase(Database):
    url = "localhost"


database = LocalDatabase()


def postgres_engine():

    engine = create_engine(
        'postgresql://{}:{}@{}:5432/{}'
        .format(
            database.username,
            database.password,
            database.url,
            database.dbname), echo=False)

    return engine
