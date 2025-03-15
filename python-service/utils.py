import requests
import os
from dotenv import load_dotenv
import re
import json
from models import Job
from datetime import datetime, timedelta
from schemas import JobUpdateRequestSchema

WEBSITE_MAPPING = {
    "INDEED": "site:ie.indeed.com/viewjob",
    "LINKEDIN": "site:linkedin.com/jobs/view",
    "IRISHJOBS": "site:www.irishjobs.ie/job/",
    "JOBS": "site:www.jobs.ie/job",
}

def generate_keywords(schema: JobUpdateRequestSchema) -> str:
    # Map selected websites to their URLs
    site_filter = WEBSITE_MAPPING[schema.website]

    # Create job type filter (e.g., ("Software Engineer" | "Backend Developer"))
    newtype = [f'"{job}"' for job in schema.type]
    type_filter = f'({" | ".join(newtype)})' if newtype else ""

    # Combine components into the search query
    keywords = f'{site_filter} {type_filter} "{schema.location}"'
    
    return keywords.strip()

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

def get_jobs(schema: JobUpdateRequestSchema):
    load_dotenv()
    API_KEY = os.getenv("API_KEY")
    CX_ID = os.getenv("CX_ID")

    jobs = []
    
    # keywords = 'site:linkedin.com/jobs/view ("Software Engineer" | "Backend Developer") "Ireland"'

    keywords = generate_keywords(schema)

    print(keywords)
    
    params = {
        "q": keywords,
        "key": API_KEY,
        "cx": CX_ID,
        "dateRestrict": f"d{schema.time}",
    }

    items = []
    start_index = 1
    while True:
        params["start"] = start_index
        response = requests.get("https://www.googleapis.com/customsearch/v1", params=params)
        data = response.json()
        if start_index>50 or not data.get("items", []):
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
        job.website = schema.website
        
        title = item['htmlTitle']

        match = re.match(r"^(.*) hiring (.*?)(?: in (.*?))?(?:\s*\| LinkedIn)?$", title)
        
        if match:
            job.company = re.sub(r"</?b>", "", match.group(1)).strip() if match.group(1) else "Unknown"
            job.type = re.sub(r"</?b>", "", match.group(2)).strip() if match.group(2) else "Unknown"
            job.location = re.sub(r"</?b>", "", match.group(3)).strip() if match.group(3) else "Unknown"
        else:
            job.company = "Unknown"
            job.type = "Unknown"
            job.location = "Unknown"

        # print(job.company, job.type, job.location)

        jobs.append(job)
    
    return jobs

def update_jobs(schema: JobUpdateRequestSchema, db):

    jobs = get_jobs(schema)

    added_jobs_count = 0
    existing_urls = {job.url for job in db.query(Job).all()}

    for job in jobs:
        if job.url not in existing_urls:
            db.add(job)
            added_jobs_count+=1

    db.commit()
    return {"message": "{} jobs updated".format(added_jobs_count)}
