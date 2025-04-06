from fastapi.testclient import TestClient
from main import app
from unittest.mock import patch, MagicMock
from schemas import JobUpdateRequestSchema

client = TestClient(app)

def test_test_endpoint():
    response = client.get("/test")
    assert response.status_code == 200
    assert response.json() == "Python engineer test ok!"

@patch("main.get_db")
@patch("main.get_jobs")
def test_update_endpoint(mock_get_jobs, mock_get_db):
    # Mock DB session
    mock_db = MagicMock()
    mock_get_db.return_value = mock_db

    # Create dummy job
    mock_job = MagicMock()
    mock_job.url = "http://fake-job-url.com"
    mock_get_jobs.return_value = [mock_job]

    # Simulate existing jobs (with different URL)
    mock_db.query.return_value.all.return_value = []

    request_payload = {
        "website": "LINKEDIN",
        "type": ["Software Engineer", "Backend Developer"],
        "location": "Ireland",
        "time": 1
    }

    response = client.post("/update", json=request_payload)
    assert response.status_code == 200
    assert "jobs updated" in response.json()["message"]
