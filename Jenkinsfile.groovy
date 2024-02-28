pipeline {
    environment {
        registry = "saikiranmerugu74/mypythonapp" //To push an image to Docker Hub, you must first name your local image using your Docker Hub username and the repository name that you created through Docker Hub on the web.
        registryCredential = 'dockerhub_id'
        dockerImage = ''
        PATH = " /home/ubuntu/.local/lib/python3.10/site-packages:$PATH"
        EC2_HOST = 'ec2-13-59-37-88.us-east-2.compute.amazonaws.com'
        //DEPLOY_PATH = '/home'
        EC2_INSTANCE_SSH_KEY_CREDENTIALS = credentials('deployserver')
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
                sh "python3 -m pytest testapp.py"
            }
        }
        stage('Deploy on Ec2') {  
            steps {
                script {
                    sshagent(['deployserver']) {
                        //sh "ssh -o StrictHostKeyChecking=no ubuntu@${EC2_HOST} 'docker run -d -p 8000:8000 --name newpythonwebapp ${img}'"
                        //sh "ssh -o StrictHostKeyChecking=no ubuntu@${EC2_HOST} 'checkout([\$class: \'scmGit\', branches: [[name: '*/main']],extensions: [],userRemoteConfigs: [[url: 'https://github.com/saikiranmerugu74/project1.git']]])'"
                        sh "ssh -o StrictHostKeyChecking=no ubuntu@${EC2_HOST} 'git clone https://github.com/saikiranmerugu74/project1.git -b main'"
                        sh "ssh -o StrictHostKeyChecking=no ubuntu@${EC2_HOST} 'cp -r project1/* /home/ubuntu'"
                        //sh "ssh -o StrictHostKeyChecking=no ubuntu@${EC2_HOST} 'cd project1/'"
                        sh "ssh -o StrictHostKeyChecking=no ubuntu@${EC2_HOST} 'ls'"
                        sh "ssh -o StrictHostKeyChecking=no ubuntu@${EC2_HOST} 'pwd'"
                        sh "ssh -o StrictHostKeyChecking=no ubuntu@${EC2_HOST} 'docker-compose up -d --build'"

                    }

                }
            }
        }
    }
}
