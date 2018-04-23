from sqlalchemy import create_engine


DATABASE = {
    'username': 'enterovirus',
    'password': 'zooo',
    'dbname': 'enterovirus'
}


def postgres_engine():

    engine = create_engine(
        'postgresql://{username}:{password}@localhost:5432/{dbname}'
        .format(
            username=DATABASE['username'],
            password=DATABASE['password'],
            dbname=DATABASE['dbname']))

    return engine
