from models import *


class RepositoryManager():

    @staticmethod
    def get_by_name_and_organization_name(session, name, org_name):
        return session.query(Repository).join(Organization).\
            filter(Repository.name == name).\
            filter(Organization.name == org_name).\
            one()


class RepositoryMemberMapManager():

    @staticmethod
    def __filter_user_and_org_and_repo_name(
            session, username, org_name, repo_name):
        return session.query(RepositoryMemberMap).\
            join(Repository).join(Organization).join(Member).\
            filter(Member.username == username).\
            filter(Organization.name == org_name).\
            filter(Repository.name == repo_name)

    @staticmethod
    def is_readable(session, username, org_name, repo_name):

        maps = RepositoryMemberMapManager.__filter_user_and_org_and_repo_name(
            session, username, org_name, repo_name).all()

        if len(maps) > 0:
            return True
        else:
            return False

    @staticmethod
    def is_editable(session, username, org_name, repo_name):

        maps = RepositoryMemberMapManager.__filter_user_and_org_and_repo_name(
            session, username, org_name, repo_name).\
            filter(_or(
                RepositoryMemberMap.role == 'E',
                RepositoryMemberMap.role == 'L')).all()

        if len(maps) > 0:
            return True
        else:
            return False
