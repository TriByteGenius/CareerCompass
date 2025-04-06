from fastapi.testclient import TestClient
from main import app
from unittest.mock import patch, MagicMock
from schemas import JobUpdateRequestSchema

client = TestClient(app)

def test_test_endpoint():
    response = client.get("/test")
    assert response.status_code == 200
    assert response.json() == "Python engineer test ok!"

@patch("main.get_db")  # Mocking the DB session
@patch("main.get_jobs")  # Mocking the get_jobs function
def test_update_endpoint(mock_get_jobs, mock_get_db):
    # Mock DB session
    mock_db = MagicMock()
    mock_get_db.return_value = mock_db

    # Mock the result of get_jobs, simulate the return value of get_jobs
    mock_job = MagicMock()
    mock_job.url = "http://fake-job-url.com"
    mock_get_jobs.return_value = [mock_job]

    # Simulate the condition that no job exists in DB yet (simulate an empty DB)
    mock_db.query.return_value.all.return_value = []  # No jobs exist in DB

    # Prepare your payload for the update endpoint
    request_payload = {
        "website": "LINKEDIN",
        "type": ["Software Engineer", "Backend Developer"],
        "location": "Ireland",
        "time": 1
    }

    # Call the update endpoint
    response = client.post("/update", json=request_payload)

    # Check the response
    assert response.status_code == 200
    assert "jobs updated" in response.json()["message"]

    # Check if the job was added to the database (mocked interaction)
    mock_db.add.assert_called_once_with(mock_job)  # Ensure add was called with mock_job
    mock_db.commit.assert_called_once()  # Ensure commit was called
