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


# `ssheep` code is mostly used for `get_authorized_keys_content.sh` in SSH `AuthorizedKeysCommand`
# hook. Very unfortunate, it is technically very hard to move the `ssheep` choices of profile
# into env variable in docker container.
#
# There are multiple ways we can get a list of `env` variables:
# (1) `docker-compose exec git env`
# (2) SSH into the container and `env`
# (3) `env` in `AuthorizedKeysCommand`.
# All have different results.
#
# (2) is not the same as (1) becuase ssh deamon overrides the environment variables.
# https://docs.docker.com/engine/examples/running_ssh_service/#environment-variables
# The way to bypass it we can either
# (a) Edit `PermitUserEnvironment yes` in `/etc/ssh/sshd_config` and define in
# `.ssh/environment` (this file can be edit by `ENTRYPOINT` in docker). It is available
# for both `ssh <user>@<host> env` and `ssh <user>@<host>` then `env`
# (b) Edit `AcceptEnv` in `/etc/ssh/sshd_config` and `SendEnv` in `/etc/ssh/ssh_config`
# in `/etc/profile`(or define it `.ssh/config`). Available only in `ssh <user>@<host>`
# then `env`, but not in `ssh <user>@<host> env`:
#
# Above tricks will not affect `AuthorizedKeysCommand` env and right now I don't know a way
# to change it.
profile = LocalProfile()
