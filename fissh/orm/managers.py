from django.db import models
from django.db.models import Q


class RepositoryManager(models.Manager):

    def get_by_name_and_organization_name(self, name, org_name):
        return super().get(name=name, organization__name=org_name)


class RepositoryMemberMap(models.Manager):

    def __get_by_user_and_organization_and_repository_name(
            self, username, org_name, repo_name):
        return super().get(
            member__username=username,
            repository__name=repo_name,
            repository__organization__name=org_name
        )

    def is_readable(self, username, org_name, repo_name):

        maps = self.__get_by_user_and_organization_and_repository_name(
            username,
            org_name,
            repo_name)

        if len(maps) > 0:
            return True
        else:
            return False

    def is_editable(self, username, org_name, repo_name):

        maps = self.__get_by_user_and_organization_and_repository_name(
            username,
            org_name,
            repo_name).filter(Q(role='E') | Q(role='L'))

        if len(maps) > 0:
            return True
        else:
            return False
