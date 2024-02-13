pipeline {
    environment {
        registry = "saikiranmerugu74/sai1:10" //To push an image to Docker Hub, you must first name your local image using your Docker Hub username and the repository name that you created through Docker Hub on the web.
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
        stage ('Clean Up'){
            steps{
                sh returnStatus: true, script: 'sudo docker stop $(docker ps -a | grep ${JOB_NAME} | awk \'{print $1}\')'
                sh returnStatus: true, script: 'sudo docker rmi $(docker images | grep ${registry} | awk \'{print $3}\') --force' //this will delete all images
                sh returnStatus: true, script: 'sudo docker rm ${JOB_NAME}'
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
                    docker.withRegistry( '', registryCredential ) {
                    dockerImage.push()
                    }
                }
            }
        }
        
        stage('Deploy') {
           steps {
                sh label: '', script: "sudo docker run -d --name ${JOB_NAME} -p 5000:5000 ${img}"
          }
        }
    }
}
