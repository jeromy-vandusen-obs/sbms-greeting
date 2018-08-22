pipeline {
    agent any

    triggers {
        pollSCM('H/5 8-16 * * 1-5')
    }

    stages {
        stage('Run Unit Tests') {
            steps {
                withMaven(maven: 'M3') {
                    sh "mvn clean test"
                }
            }
            post {
                always {
                    junit "target/surefire-reports/*.xml"
                }
            }
        }
        stage('Build Application') {
            steps {
                withMaven(maven: 'M3') {
                    sh "mvn package -DskipTests"
                }
            }
        }
        stage('Build Image') {
            steps {
                withMaven(maven: 'M3') {
                    sh "mvn dockerfile:build@version dockerfile:tag@latest -DskipTests"
                }
            }
        }
        stage('Push Image to Registry') {
            steps {
                withMaven(maven: 'M3') {
                    withCredentials([usernamePassword(credentialsId: 'DOCKER_HUB_CREDENTIALS', usernameVariable: 'DOCKER_HUB_USERNAME', passwordVariable: 'DOCKER_HUB_PASSWORD')]) {
                        sh "mvn dockerfile:push@version dockerfile:push@latest -DskipTests -Ddockerfile.username=$DOCKER_HUB_USERNAME -Ddockerfile.password=$DOCKER_HUB_PASSWORD"
                    }
                }
            }
        }
    }
}
