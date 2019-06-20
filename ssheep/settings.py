from sqlalchemy import create_engine


DATABASE = {
    'username': 'gitenter_app',
    'password': 'zooo',
    'dbname': 'gitenter'
}


def postgres_engine():

    engine = create_engine(
        'postgresql://{username}:{password}@localhost:5432/{dbname}'
        .format(
            username=DATABASE['username'],
            password=DATABASE['password'],
            dbname=DATABASE['dbname']), echo=False)

    return engine
