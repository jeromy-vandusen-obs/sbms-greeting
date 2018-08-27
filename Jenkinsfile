pipeline {
    agent any

    triggers {
        pollSCM('H/5 8-16 * * 1-5')
    }

    tools {
        maven 'M3'
    }

    stages {
        stage('Run Unit Tests') {
            steps {
                sh "mvn clean test"
            }
            post {
                always {
                    junit "target/surefire-reports/*.xml"
                }
            }
        }
        stage('Build Application') {
            steps {
                sh "mvn package -DskipTests"
            }
        }
        stage('Build Image') {
            steps {
                sh "mvn dockerfile:build@version dockerfile:tag@latest -DskipTests"
            }
        }
        stage('Push Image to Registry') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'DOCKER_HUB_CREDENTIALS', usernameVariable: 'DOCKER_HUB_USERNAME', passwordVariable: 'DOCKER_HUB_PASSWORD')]) {
                    sh "mvn dockerfile:push@version dockerfile:push@latest -DskipTests -Ddockerfile.username=$DOCKER_HUB_USERNAME -Ddockerfile.password=$DOCKER_HUB_PASSWORD"
                }
            }
        }
    }
}
