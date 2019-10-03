class Profile(object):
    pass


class LocalProfile(Profile):
    database_url = "localhost"


class DockerProfile(Profile):
    database_url = "postgres"


class StagingProfile(Profile):
    database_url = "staging-postgres.cqx7dy9nh94t.us-east-1.rds.amazonaws.com"


class ProductionProfile(Profile):
    database_url = "localhost"


profile = LocalProfile()
