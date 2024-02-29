import pytest
import requests

@pytest.fixture
def base_url():
    return "http://52.14.227.124:8000"

def test_hello_endpoint(base_url):
    response = requests.get(f"{base_url}/")
    assert response.status_code == 200
    assert response.json()['message'] == 'Hello, world!'
