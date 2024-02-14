import pytest
'''
#from selenium import webdriver

#@pytest.fixture
def browser():
    # This fixture sets up and tears down the Selenium WebDriver
    driver = webdriver.Chrome()  # You can choose a different driver (Firefox, etc.) based on your preference
    yield driver
    driver.quit()

def test_validate_text_on_web_page(browser):
    # Open the web page
    browser.get("http://127.0.0.1:5000/")  # Replace with the actual URL of your web page

    # Find the element containing the text you want to validate
    text_element = browser.find_element_by_xpath("/html/body/h1")

    # Get the text from the element
    actual_text = text_element.text

    # Define the expected text
    expected_text = "Hello, World!"  # Replace with the expected text

    # Validate that the actual text matches the expected text
    assert actual_text == expected_text, f"Expected text: {expected_text}, Actual text: {actual_text}"


    # test_app.py'''

def test_flask_app_text():
    expected_text = "Hello, World!\nThis is my Flask web application."
    actual_text = home()

    assert actual_text == expected_text, f"Expected: {expected_text}\nActual: {actual_text}"

