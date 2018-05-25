from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Table, Column, ForeignKey
from sqlalchemy.types import Boolean, Integer, String, VARCHAR
from sqlalchemy.orm import relationship

Base = declarative_base()


organization_manager_map = Table(
    'settings.organization_manager_map',
    Base.metadata,
    Column('member_id', Integer, ForeignKey('settings.member.id')),
    Column('organization_id', Integer, ForeignKey('settings.organization.id'))
)


class Member(Base):

    __tablename__ = 'member'
    __table_args__ = {'schema': 'settings'}

    id = Column(Integer, primary_key=True)

    username = Column(String)
    password = Column(String)
    display_name = Column(String)
    email = Column(String)

    repository_member_maps = relationship(
        "RepositoryMemberMap",
        back_populates="member")

    managed_organizations = relationship(
        "Organization",
        secondary=organization_manager_map,
        back_populates="managers")


class Organization(Base):

    __tablename__ = 'organization'
    __table_args__ = {'schema': 'settings'}

    id = Column(Integer, primary_key=True)

    name = Column(String)
    display_name = Column(String)

    repositories = relationship("Repository", back_populates="organization")

    managers = relationship(
        "Member",
        secondary=organization_manager_map,
        back_populates="managed_organizations")


class Repository(Base):

    __tablename__ = 'repository'
    __table_args__ = {'schema': 'settings'}

    id = Column(Integer, primary_key=True)

    organization_id = Column(Integer, ForeignKey('settings.organization.id'))
    organization = relationship("Organization", back_populates="repositories")

    name = Column(String)
    display_name = Column(String)
    description = Column(String)

    is_public = Column(Boolean)

    repository_member_maps = relationship(
        "RepositoryMemberMap",
        back_populates="repository")


class RepositoryMemberMap(Base):

    __tablename__ = 'repository_member_map'
    __table_args__ = {'schema': 'settings'}

    id = Column(Integer, primary_key=True)

    repository_id = Column(Integer, ForeignKey('settings.repository.id'))
    repository = relationship(
        "Repository",
        back_populates="repository_member_maps")

    member_id = Column(Integer, ForeignKey('settings.member.id'))
    member = relationship(
        "Member",
        back_populates="repository_member_maps")

    role = Column(VARCHAR(1))
