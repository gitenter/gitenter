from sqlalchemy import create_engine
from settings.profile import profile


def postgres_engine():

    username = "gitenter_app"
    password = "zooo"
    dbname = "gitenter"

    engine = create_engine(
        'postgresql://{}:{}@{}:5432/{}'
        .format(
            username,
            password,
            profile.database_url,
            dbname), echo=False)

    return engine
