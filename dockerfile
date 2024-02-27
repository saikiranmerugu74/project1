FROM python:3.7
WORKDIR /app
COPY . /app
RUN pip install --upgrade pip
RUN pip install --trusted-host pypi.python.org -r requirements.txt
RUN pip install prometheus_client
EXPOSE 8000
CMD ["python3", "app.py"]
