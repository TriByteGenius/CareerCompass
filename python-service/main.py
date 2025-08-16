# main.py

from fastapi import FastAPI, HTTPException
from utils import search_and_publish_jobs
from schemas import JobUpdateRequestSchema

app = FastAPI()

@app.get("/test")
def test():
    return "Python engine test ok!"

@app.post("/update")
def update_jobs(request: JobUpdateRequestSchema):
    try:
        result = search_and_publish_jobs(request)
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))