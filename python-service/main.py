# main.py

from sqlalchemy.orm import Session
from fastapi import FastAPI, Depends, HTTPException
import models
from database import engine, get_db
from utils import update_jobs
from schemas import JobUpdateRequestSchema

models.Base.metadata.create_all(engine)

app = FastAPI()

@app.get("/test")
def test():
    return "Python engineer test ok!"

@app.post("/update")
def get_index(request: JobUpdateRequestSchema, db: Session = Depends(get_db)):
    return update_jobs(request, db)