from fastapi.testclient import TestClient
from main import app

client = TestClient(app)

def test_test_endpoint():
    response = client.get("/test")
    assert response.status_code == 200
    assert response.json() == "Python engineer test ok!"