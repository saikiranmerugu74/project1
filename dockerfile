#FROM python:3.7
#WORKDIR /app
#COPY . /app
#RUN pip install --upgrade pip
#RUN pip install --trusted-host pypi.python.org -r requirements.txt
#EXPOSE 8000
#CMD ["python3", "app.py"]


FROM python:3.9-slim

WORKDIR /app

COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

COPY app.py .

CMD [ "python", "app.py" ]
