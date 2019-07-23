from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, ForeignKey
from sqlalchemy.types import Boolean, Integer, String, VARCHAR
from sqlalchemy.orm import relationship

Base = declarative_base()


class Member(Base):

    __tablename__ = 'member'
    __table_args__ = {'schema': 'auth'}

    id = Column(Integer, primary_key=True)

    username = Column(String)
    password = Column(String)
    display_name = Column(String)
    email = Column(String)

    ssh_keys = relationship("SshKey", back_populates="member")

    repository_member_maps = relationship(
        "RepositoryMemberMap",
        back_populates="member")
    organization_member_maps = relationship(
        "OrganizationMemberMap",
        back_populates="member")


class Organization(Base):

    __tablename__ = 'organization'
    __table_args__ = {'schema': 'auth'}

    id = Column(Integer, primary_key=True)

    name = Column(String)
    display_name = Column(String)

    repositories = relationship("Repository", back_populates="organization")
    organization_member_maps = relationship(
        "OrganizationMemberMap",
        back_populates="organization")


class OrganizationMemberMap(Base):

    __tablename__ = 'organization_member_map'
    __table_args__ = {'schema': 'auth'}

    id = Column(Integer, primary_key=True)

    organization_id = Column(Integer, ForeignKey('auth.organization.id'))
    organization = relationship(
        "Organization",
        back_populates="organization_member_maps")

    member_id = Column(Integer, ForeignKey('auth.member.id'))
    member = relationship(
        "Member",
        back_populates="organization_member_maps")

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

    repository_member_maps = relationship(
        "RepositoryMemberMap",
        back_populates="repository")


class RepositoryMemberMap(Base):

    __tablename__ = 'repository_member_map'
    __table_args__ = {'schema': 'auth'}

    id = Column(Integer, primary_key=True)

    repository_id = Column(Integer, ForeignKey('auth.repository.id'))
    repository = relationship(
        "Repository",
        back_populates="repository_member_maps")

    member_id = Column(Integer, ForeignKey('auth.member.id'))
    member = relationship(
        "Member",
        back_populates="repository_member_maps")

    role_shortname = Column(VARCHAR(1))


class SshKey(Base):

    __tablename__ = 'ssh_key'
    __table_args__ = {'schema': 'auth'}

    id = Column(Integer, primary_key=True)

    member_id = Column(Integer, ForeignKey('auth.member.id'))
    member = relationship("Member", back_populates="ssh_keys")

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
