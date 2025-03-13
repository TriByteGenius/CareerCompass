import requests
import os
from dotenv import load_dotenv
import re
import json
from models import Job
from datetime import datetime, timedelta


def extract_time(item) -> str:
    time_pattern = re.compile(r"\b(\d+)\s*(hours|days|weeks|months|years) ago\b", re.IGNORECASE)
    match = time_pattern.search(item.get("htmlSnippet", ""))

    if match:
        value = int(match.group(1))  # Extract the numeric value
        unit = match.group(2).lower()  # Extract the time unit

        # Map the unit to a corresponding timedelta parameter
        time_deltas = {
            "hours": timedelta(hours=value),
            "days": timedelta(days=value),
            "weeks": timedelta(weeks=value),
            "months": timedelta(days=value * 30),  # Approximate 1 month = 30 days
            "years": timedelta(days=value * 365)   # Approximate 1 year = 365 days
        }

        # Calculate the final time
        calculated_time = datetime.now() - time_deltas[unit]
        return calculated_time.strftime("%Y-%m-%d %H:%M:%S")
    return datetime(1970, 1, 1)

def get_jobs():
    load_dotenv()
    API_KEY = os.getenv("API_KEY")
    CX_ID = os.getenv("CX_ID")

    jobs = []
    
    keywords = 'site:linkedin.com/jobs/view ("Software Engineer" | "Backend Developer") "Ireland"'
    
    params = {
        "q": keywords,
        "key": API_KEY,
        "cx": CX_ID,
        "dateRestrict": "d1",
    }

    items = []
    start_index = 1
    while True:
        params["start"] = start_index
        response = requests.get("https://www.googleapis.com/customsearch/v1", params=params)
        data = response.json()
        if not data.get("items", []):
            break
        else:
            start_index += 10
            items += data.get("items", [])

    for item in items:
        job = Job()
        job.name = item.get("title", "")
        job.url = item.get("link", "")
        job.time = extract_time(item)
        job.status = "new"
        
        title = item['pagemap']['metatags'][0]['twitter:title']
        match = re.match(r"^(.*) hiring (.*?) in (.*?)(?:\s*\| LinkedIn)?$", title)
        if match:
            job.company = match.group(1).strip()
            job.type = match.group(2).strip()
            job.location = match.group(3).strip()

        jobs.append(job)
    
    return jobs

# def get_jobs():
#     return [
#         Job(
#             name="Software Engineer",
#             url="https://www.linkedin.com/jobs/view/123456",
#             keywords="site:linkedin.com/jobs/view ('Software Engineer' | 'Backend Developer') 'Ireland'",
#             time="2 days ago",
#             status="new"
#         ),
#         Job(
#             name="Backend Developer",
#             url="https://www.linkedin.com/jobs/view/789012",
#             keywords="site:linkedin.com/jobs/view ('Software Engineer' | 'Backend Developer') 'Ireland'",
#             time="5 hours ago",
#             status="new"
#         )
#     ]

def update_jobs(db):

    jobs = get_jobs()

    for job in jobs:
        db.add(job)

    db.commit()
    return {"message": "{} jobs updated".format(len(jobs))}
