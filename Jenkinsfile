@Library('sbms-pipeline-shared@master') _

pipeline {
    agent any

    triggers {
        pollSCM('H/5 * * * 1-5')
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
        disableConcurrentBuilds()
        timeout(time: 15, unit: 'MINUTES')
    }

    stages {
        stage('Set Version') {
            steps {
                mvn "versions:set -DnewVersion=\$(./mvnw help:evaluate -Dexpression=project.version | grep -e '^[^\\[\\/]')-$BUILD_NUMBER"
            }
        }
        stage('Run Unit Tests') {
            steps {
                mvn "clean test"
            }
            post {
                always {
                    junit "target/surefire-reports/*.xml"
                }
            }
        }
        stage('Build Application') {
            steps {
                mvn "package -DskipTests"
            }
        }
        stage('Run Integration Tests') {
            steps {
                mvn "verify -DskipUnitTests"
            }
            post {
                always {
                    junit "target/failsafe-reports/*.xml"
                }
            }
        }
        stage('Install Contract Stubs') {
            steps {
                mvn "install -DskipTests"
            }
        }
        stage('Build Image') {
            steps {
                mvn "dockerfile:build@version dockerfile:tag@latest -DskipTests"
            }
        }
        stage('Push Image to Registry') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'DOCKER_HUB_CREDENTIALS', usernameVariable: 'DOCKER_HUB_USERNAME', passwordVariable: 'DOCKER_HUB_PASSWORD')]) {
                    mvn "dockerfile:push@version dockerfile:push@latest -DskipTests -Ddockerfile.username=$DOCKER_HUB_USERNAME -Ddockerfile.password=$DOCKER_HUB_PASSWORD"
                }
            }
        }
    }
    post {
        always {
            mvn "versions:revert"
        }
        success {
            notify('', 'Succeeded')
        }
        failure {
            notify('FAIL', 'Failed')
        }
    }
}
