pipeline {
    agent any

    triggers {
        pollSCM('H/5 * * * 1-5')
    }

    tools {
        maven 'M3'
    }

    stages {
        stage('Set Version') {
            steps {
                sh "mvn versions:set -DnewVersion=\$(mvn help:evaluate -Dexpression=project.version | grep -e '^[^\\[]')-$BUILD_NUMBER"
            }
        }
        stage('Run Unit Tests') {
            steps {
                sh "mvn clean test -DbuildNumber=$BUILD_NUMBER"
            }
            post {
                always {
                    junit "target/surefire-reports/*.xml"
                }
            }
        }
        stage('Build Application') {
            steps {
                sh "mvn package -DskipTests -DbuildNumber=$BUILD_NUMBER"
            }
        }
        stage('Run Integration Tests') {
            steps {
                sh "mvn verify -DskipUnitTests -DbuildNumber=$BUILD_NUMBER"
            }
            post {
                always {
                    junit "target/failsafe-reports/*.xml"
                }
            }
        }
        stage('Install Contract Stubs') {
            steps {
                sh "mvn install -DskipTests -DbuildNumber=$BUILD_NUMBER"
            }
        }
        stage('Build Image') {
            steps {
                sh "mvn dockerfile:build@version dockerfile:tag@latest -DskipTests -DbuildNumber=$BUILD_NUMBER"
            }
        }
        stage('Push Image to Registry') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'DOCKER_HUB_CREDENTIALS', usernameVariable: 'DOCKER_HUB_USERNAME', passwordVariable: 'DOCKER_HUB_PASSWORD')]) {
                    sh "mvn dockerfile:push@version dockerfile:push@latest -DskipTests -DbuildNumber=$BUILD_NUMBER -Ddockerfile.username=$DOCKER_HUB_USERNAME -Ddockerfile.password=$DOCKER_HUB_PASSWORD"
                }
            }
        }
    }
    post {
        always {
            sh "mvn versions:revert"
        }
    }
}
