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

## Datebase
Only one datebase, containing all the jobs information from all websites.