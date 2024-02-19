pipeline {
    environment {
        registry = "saikiranmerugu74/mypythonapp" //To push an image to Docker Hub, you must first name your local image using your Docker Hub username and the repository name that you created through Docker Hub on the web.
        registryCredential = 'dockerhub_id'
        //githubCredential = 'GITHUB'
        dockerImage = ''
        PATH = " /home/ubuntu/.local/lib/python3.10/site-packages:$PATH"
        SSH_KEY = '511897ab-b3f6-4d2e-a5f0-b43bc9a0a586'
        EC2_HOST = '172.31.10.16.us-east-2.compute.amazonaws.com'
        DEPLOY_PATH = '/home/pyapp'
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
                sh returnStatus: true, script: 'docker stop $(docker ps -a | grep ${JOB_NAME} | awk \'{print $1}\')'
                sh returnStatus: true, script: 'docker rmi $(docker images | grep ${registry} | awk \'{print $3}\') --force' //this will delete all images
                sh returnStatus: true, script: 'docker rm ${JOB_NAME}'
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
                sh label: '', script: "docker run -d --name ${JOB_NAME} -p 5000:5000 ${img}"
            }
        }
        stage ('Test'){
            steps {
                sh "ls"
                sh "python3 -m pytest testapp.py"
            }
        }
        stage('Deployment Checkout') {
            steps {
                checkout scmGit(branches: [[name: '*/main']],
                extensions: [],
                userRemoteConfigs: [[url: 'https://github.com/saikiranmerugu74/project1.git']]) 
                
            }
        }
        stage('Deploy to EC2') {
            steps {
                script {
                    // Use SSH agent to connect to the EC2 instance
                    sshagent(credentials: ['511897ab-b3f6-4d2e-a5f0-b43bc9a0a586']) {
                        // Copy your artifact to the EC2 instance
                        sh "scp -i ${SSH_KEY} app.py ec2-user@${EC2_HOST}:${DEPLOY_PATH}"
                    }
                }
            }
        }
    }
}
