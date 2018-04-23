from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, Integer, String, ForeignKey
from sqlalchemy.orm import relationship

Base = declarative_base()


class Member(Base):

    __tablename__ = 'member'
    __table_args__ = {'schema': 'config'}

    id = Column(Integer, primary_key=True)
    username = Column(String)
    password = Column(String)
    display_name = Column(String)
    email = Column(String)


class Organization(Base):

    __tablename__ = 'organization'
    __table_args__ = {'schema': 'config'}

    id = Column(Integer, primary_key=True)
    name = Column(String)
    display_name = Column(String)

    repositories = relationship("Repository", back_populates="organization")


class Repository(Base):

    __tablename__ = 'repository'
    __table_args__ = {'schema': 'config'}

    id = Column(Integer, primary_key=True)
    organization_id = Column(Integer, ForeignKey('config.organization.id'))
    organization = relationship("Organization", back_populates="repositories")
    name = Column(String)
    display_name = Column(String)
    description = Column(String)
