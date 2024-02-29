'''import pytest
from app import app  
import requests

@pytest.fixture
def client():
    app.config['TESTING'] = True
    with app.test_client() as client:
        yield client

def test_hello_endpoint(client):
    response = client.get('/')
    assert response.status_code == 200
    assert response.get_json()['message'] == 'Hello, world!'

def test_metrics_endpoint(client):
    response = client.get('/metrics')
    assert response.status_code == 200
    # Add more assertions based on the expected metrics response
    # For example, check if certain metrics or labels are present in the response
'''

import pytest
import requests

@pytest.fixture
def base_url():
    return "http://52.14.227.124:8000"

def test_hello_endpoint(base_url):
    response = requests.get(f"{base_url}/")
    assert response.status_code == 200
    assert response.json()['message'] == 'Hello, world!'
