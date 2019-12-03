from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, ForeignKey
from sqlalchemy.types import Boolean, Integer, String, VARCHAR
from sqlalchemy.orm import relationship

Base = declarative_base()


class User(Base):

    __tablename__ = 'application_user'
    __table_args__ = {'schema': 'auth'}

    id = Column(Integer, primary_key=True)

    username = Column(String)
    password = Column(String)
    display_name = Column(String)
    email = Column(String)

    ssh_keys = relationship("SshKey", back_populates="user")

    repository_user_maps = relationship(
        "RepositoryUserMap",
        back_populates="user")
    organization_user_maps = relationship(
        "OrganizationUserMap",
        back_populates="user")


class Organization(Base):

    __tablename__ = 'organization'
    __table_args__ = {'schema': 'auth'}

    id = Column(Integer, primary_key=True)

    name = Column(String)
    display_name = Column(String)

    repositories = relationship("Repository", back_populates="organization")
    organization_user_maps = relationship(
        "OrganizationUserMap",
        back_populates="organization")


class OrganizationUserMap(Base):

    __tablename__ = 'organization_user_map'
    __table_args__ = {'schema': 'auth'}

    id = Column(Integer, primary_key=True)

    organization_id = Column(Integer, ForeignKey('auth.organization.id'))
    organization = relationship(
        "Organization",
        back_populates="organization_user_maps")

    user_id = Column(Integer, ForeignKey('auth.application_user.id'))
    user = relationship(
        "User",
        back_populates="organization_user_maps")

    role_shortname = Column(VARCHAR(1))


class Repository(Base):

    __tablename__ = 'repository'
    __table_args__ = {'schema': 'auth'}

    id = Column(Integer, primary_key=True)

    organization_id = Column(Integer, ForeignKey('auth.organization.id'))
    organization = relationship("Organization", back_populates="repositories")

    name = Column(String)
    display_name = Column(String)
    description = Column(String)

    is_public = Column(Boolean)

    repository_user_maps = relationship(
        "RepositoryUserMap",
        back_populates="repository")


class RepositoryUserMap(Base):

    __tablename__ = 'repository_user_map'
    __table_args__ = {'schema': 'auth'}

    id = Column(Integer, primary_key=True)

    repository_id = Column(Integer, ForeignKey('auth.repository.id'))
    repository = relationship(
        "Repository",
        back_populates="repository_user_maps")

    user_id = Column(Integer, ForeignKey('auth.application_user.id'))
    user = relationship(
        "User",
        back_populates="repository_user_maps")

    role_shortname = Column(VARCHAR(1))


class SshKey(Base):

    __tablename__ = 'ssh_key'
    __table_args__ = {'schema': 'auth'}

    id = Column(Integer, primary_key=True)

    user_id = Column(Integer, ForeignKey('auth.application_user.id'))
    user = relationship("User", back_populates="ssh_keys")

    key_type = Column(String)
    key_data = Column(String)
    comment = Column(String)

    def get_authorized_keys_line(self, options=[]):
        if not options:
            return "{} {} {}\n".format(
                self.key_type,
                self.key_data,
                self.comment)
        else:
            return "{} {} {} {}\n".format(
                ",".join(options),
                self.key_type,
                self.key_data,
                self.comment)
