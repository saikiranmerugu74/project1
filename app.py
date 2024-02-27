# app.py
from flask import Flask
from prometheus_client import start_http_server, Counter

request_counter = Counter('webapp_requests_total', 'Total number of requests')

app = Flask(__name__)

@app.route('/')
def hello():
    return 'Hello, World!'

if __name__ == '__main__':
    #app.run(debug=True)
    start_http_server(8001)
    app.run(host='0.0.0.0', port=8000)

