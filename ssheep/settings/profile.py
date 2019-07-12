class Profile(object):
    pass


class LocalProfile(Profile):
    database_url = "localhost"


class DockerProfile(Profile):
    database_url = "postgres"


class QaProfile(Profile):
    database_url = "qa-postgres.cqx7dy9nh94t.us-east-1.rds.amazonaws.com"


class ProductionProfile(Profile):
    database_url = "localhost"


profile = LocalProfile()
