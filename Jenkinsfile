pipeline {
    agent any

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
        stage('Build Container Image') {
            steps {
                withMaven(maven: 'M3') {
                    sh "mvn dockerfile:build dockerfile:tag@version -DskipTests"
                }
            }
        }
        stage('Push Image to Registry') {
            steps {
                withMaven(maven: 'M3') {
                    withCredentials([usernamePassword(credentialsId: 'DOCKER_HUB_CREDENTIALS', usernameVariable: 'DOCKER_HUB_USERNAME', passwordVariable: 'DOCKER_HUB_PASSWORD')]) {
                        sh "mvn dockerfile:push@tag-latest dockerfile:push@version -DskipTests -Ddockerfile.username=$DOCKER_HUB_USERNAME -Ddockerfile.password=$DOCKER_HUB_PASSWORD"
                    }
                }
            }
        }
    }
}
