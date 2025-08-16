import requests
import os
from dotenv import load_dotenv
import re
import json
from datetime import datetime, timedelta
from schemas import JobUpdateRequestSchema
import pika

WEBSITE_MAPPING = {
    "indeed": "site:ie.indeed.com/viewjob",
    "linkedin": "site:linkedin.com/jobs/view",
    "irishjobs": "site:www.irishjobs.ie/job/",
    "jobs": "site:www.jobs.ie/job",
}

def generate_keywords(schema: JobUpdateRequestSchema) -> str:
    # Map selected websites to their URLs (case insensitive)
    website_lower = schema.website.lower()
    site_filter = WEBSITE_MAPPING.get(website_lower, f"site:{website_lower}.com")

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
    return datetime(1970, 1, 1).strftime("%Y-%m-%d %H:%M:%S")

def get_jobs(schema: JobUpdateRequestSchema):
    load_dotenv()
    API_KEY = os.getenv("API_KEY")
    CX_ID = os.getenv("CX_ID")

    jobs = []
    
    keywords = generate_keywords(schema)
    print(f"Searching with keywords: {keywords}")
    
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
        if start_index > 50 or not data.get("items", []):
            break
        else:
            start_index += 10
            items += data.get("items", [])

    for item in items:
        job = {
            "name": item.get("title", ""),
            "url": item.get("link", ""),
            "time": extract_time(item),
            "status": "new",
            "website": schema.website
        }
        
        title = item['htmlTitle']

        match = re.match(r"^(.*) hiring (.*?)(?: in (.*?))?(?:\s*\| LinkedIn)?$", title)
        
        if match:
            job["company"] = re.sub(r"</?b>", "", match.group(1)).strip() if match.group(1) else "Unknown"
            job["type"] = re.sub(r"</?b>", "", match.group(2)).strip() if match.group(2) else "Unknown"
            job["location"] = re.sub(r"</?b>", "", match.group(3)).strip() if match.group(3) else "Unknown"
        else:
            job["company"] = "Unknown"
            job["type"] = "Unknown"
            job["location"] = "Unknown"

        jobs.append(job)
    
    return jobs

def publish_job_event(job_data):
    """Publish a single job event to RabbitMQ"""
    try:
        # RabbitMQ connection
        connection = pika.BlockingConnection(
            pika.ConnectionParameters(
                host=os.getenv('RABBITMQ_HOST', 'rabbitmq'),
                port=int(os.getenv('RABBITMQ_PORT', '5672')),
                credentials=pika.PlainCredentials(
                    os.getenv('RABBITMQ_USERNAME', 'guest'),
                    os.getenv('RABBITMQ_PASSWORD', 'guest')
                )
            )
        )
        channel = connection.channel()
        
        # Declare exchange
        channel.exchange_declare(exchange='job.events', exchange_type='topic', durable=True)
        
        # Create event payload
        event = {
            "name": job_data["name"],
            "company": job_data["company"],
            "type": job_data["type"],
            "location": job_data["location"],
            "website": job_data["website"],
            "url": job_data["url"],
            "time": job_data["time"],
            "status": job_data["status"],
            "eventType": "CREATED",
            "timestamp": datetime.now().isoformat()
        }
        
        # Publish event
        channel.basic_publish(
            exchange='job.events',
            routing_key='job.created',
            body=json.dumps(event),
            properties=pika.BasicProperties(
                delivery_mode=2,  # make message persistent
                content_type='application/json'
            )
        )
        
        print(f"Published job event: {job_data['name']} at {job_data['company']}")
        connection.close()
        
    except Exception as e:
        print(f"Failed to publish job event: {e}")

def search_and_publish_jobs(schema: JobUpdateRequestSchema):
    """Search for jobs and publish events directly"""
    jobs = get_jobs(schema)
    
    published_count = 0
    for job in jobs:
        try:
            publish_job_event(job)
            published_count += 1
        except Exception as e:
            print(f"Failed to publish job {job['name']}: {e}")
    
    return {"message": f"Published {published_count} job events", "total_found": len(jobs)}
