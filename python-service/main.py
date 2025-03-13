# main.py

from sqlalchemy.orm import Session
from fastapi import FastAPI, Depends, HTTPException
import models
from database import engine, get_db
from utils import update_jobs

models.Base.metadata.create_all(engine)

app = FastAPI()

@app.get("/update")
def get_index(db: Session = Depends(get_db)):
    return update_jobs(db)