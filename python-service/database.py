import os
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from dotenv import load_dotenv

load_dotenv()
SQLALCHEMY_DATABASE_URL = os.getenv("SQLALCHEMY_DATABASE_URL")

engine = create_engine(SQLALCHEMY_DATABASE_URL, echo=True)

User_Session = sessionmaker(autocommit=False, autoflush=False, bind=engine)

def get_db():

    db = User_Session()
    try:
        yield db
    finally:
        db.close()