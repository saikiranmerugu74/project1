import pytest

def test_flask_app_text():
    expected_text = "Hello, World!\nThis is my Flask web application."
    actual_text = home()

    assert actual_text == expected_text, f"Expected: {expected_text}\nActual: {actual_text}"

