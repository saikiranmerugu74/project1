import pytest
import requests

@pytest.fixture
def base_url():
    return "http://18.191.54.139:8000"

def test_hello_endpoint(base_url):
    response = requests.get(f"{base_url}/")
    assert response.status_code == 200
    assert response.json()['message'] == 'Hello, world!'
