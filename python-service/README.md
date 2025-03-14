# Python-service

## Setup & Deployment
1. Setup the virtual enviroment and install all dependences:
   ```sh
   make setup
   ```
2. Run service
   ```sh
   make run
   ```

## API Endpoint
#### POST /update
#### Request Body:
```json
{
    "websites": "LINKEDIN",
    "type": ["Software Engineer", "Backend Developer"],
    "location": "Ireland",
    "time": 1
}
```

#### equest Parameters
1. **websites**: A string specifying the job listing website. The valid options are:
INDEED: "site:ie.indeed.com/viewjob"
LINKEDIN: "site:linkedin.com/jobs/view"
IRISHJOBS: "site:www.irishjobs.ie/job/"
JOBS: "site:www.jobs.ie/job"

2. **type**: A list of job titles or keywords to filter the search results. This can contain any job titles or keywords.
location: The location of the job.
If the websites field is set to LINKEDIN, the location can be set to any city or country.
For all other websites, the location must either be set to Ireland or a specific city within Ireland.

3. **time**: An integer indicating the number of days since the job was posted. This helps filter jobs based on how recent the listing is.

4. **type** could be any words


## Datebase
Only one datebase, containing all the jobs information from all websites.

```sql
CREATE TABLE jobs (
    id INTEGER PRIMARY KEY AUTOINCREMENT, -- Auto-incremented ID
    name TEXT NOT NULL,
    company TEXT NOT NULL,
    type TEXT NOT NULL,
    location TEXT NOT NULL,
    time DATETIME NOT NULL,
    status TEXT NOT NULL,
    url TEXT NOT NULL,
    website TEXT NOT NULL
);
```