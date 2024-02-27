pipeline {
    environment {
        registry = "saikiranmerugu74/mypythonapp" //To push an image to Docker Hub, you must first name your local image using your Docker Hub username and the repository name that you created through Docker Hub on the web.
        registryCredential = 'dockerhub_id'
        dockerImage = ''
        PATH = " /home/ubuntu/.local/lib/python3.10/site-packages:$PATH"
        EC2_HOST = 'ec2-3-12-146-98.us-east-2.compute.amazonaws.com'
        DEPLOY_PATH = '/home'
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
        stage('Deploy to EC2') {  
            steps {
                script {
                    sshagent(['deployserver']) {
                        sh "ssh -o StrictHostKeyChecking=no ubuntu@${EC2_HOST} 'docker stop $(docker ps -a | grep ${JOB_NAME} | awk \'{print $1}\')'"
                        sh "ssh -o StrictHostKeyChecking=no ubuntu@${EC2_HOST} 'docker rmi $(docker images | grep ${registry} | awk \'{print $3}\') --force'"
                        sh "ssh -o StrictHostKeyChecking=no ubuntu@${EC2_HOST} 'docker run -d -p 8000:8000 --name newpythonwebapp ${img}'"
                        sh "ssh -o StrictHostKeyChecking=no ubuntu@${EC2_HOST} 'docker rm ${JOB_NAME}'"
                    }

                }
            }
        }
        //stage('Run Prometheus Container') {
            //steps {
                //script {
                    // Run Prometheus Docker container
                    //sh 'docker run -d -p 9090:9090 --name prometheus -v /path/to/prometheus:/etc/prometheus prom/prometheus'
                //}
            //}
        //}
    }
}
