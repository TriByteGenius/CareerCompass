# models.py

from sqlalchemy import Column, Integer, String, DateTime
from sqlalchemy.ext.declarative import declarative_base
from datetime import datetime

Base = declarative_base()

class Job(Base):
    __tablename__ = "jobs"
    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, nullable=False)
    company = Column(String, nullable=False)
    type = Column(String, nullable=False)
    location = Column(String, nullable=False)
    time = Column(DateTime, nullable=False)
    status = Column(String, nullable=False)
    url = Column(String, nullable=False)