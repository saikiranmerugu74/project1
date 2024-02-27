'''import pytest

def test_flask_app_text():
    expected_text = "Hello, World!\nThis is my Flask web application."
    actual_text = home()

    assert actual_text == expected_text, f"Expected: {expected_text}\nActual: {actual_text}"'''


import pytest
from app.py import app, REQUEST_COUNT, REQUEST_LATENCY

@pytest.fixture
def client():
    with app.test_client() as client:
        yield client

def test_hello_endpoint(client):
    response = client.get('/')
    assert response.status_code == 200
    assert response.json == {'message': 'Hello, world!'}

def test_metrics_endpoint(client):
    response = client.get('/metrics')
    assert response.status_code == 200
    # Add more assertions based on your specific implementation of metrics endpoint

def test_request_metrics():
    with app.test_request_context('/'):
        with app.test_client() as client:
            response = client.get('/')
    
    assert response.status_code == 200

    # Add more assertions based on your specific implementation of metrics
    assert REQUEST_COUNT.labels('GET', '/', 200).get() == 1
    # You may want to check REQUEST_LATENCY metrics as well


