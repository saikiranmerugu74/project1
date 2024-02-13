pipeline {
    environment {
        registry = "saikiranmerugu74/mypythonapp" //To push an image to Docker Hub, you must first name your local image using your Docker Hub username and the repository name that you created through Docker Hub on the web.
        registryCredential = 'dockerhub_id'
        //githubCredential = 'GITHUB'
        dockerImage = ''
    }
    agent any
    stages {
        stage('Checkout') {
            steps {
                checkout scmGit(branches: [[name: '*/main']],
                extensions: [],
                userRemoteConfigs: [[url: 'https://github.com/saikiranmerugu74/project1.git']]) 
                
            }
        }
        stage('Build Image') {
            steps {
                script {
                    img = registry + ":${env.BUILD_ID}"
                    println ("${img}")
                    dockerImage = docker.build("${img}")
                }
            }
        }
        stage('Push To DockerHub') {
            steps {
                script {
                    docker.withRegistry('', registryCredential) {
                    dockerImage.push()
                    }
                }
            }
        }
        stage('Deploy') {
            steps {
                sh label: '', script: "docker run -d --name ${JOB_NAME} -p 8000:5000 ${img}"
            }
        }
    }
}
