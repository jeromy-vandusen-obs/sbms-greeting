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

    environment {
        DEV_HOST = "dev"
        TEST_HOST = "test"
        TEST_PORT = "28080"
        UAT_HOST = "uat"
        PROD_HOST = "prod"

        IMAGE_NAME = "$DOCKER_USERNAME/$JOB_NAME"
    }

    stages {
        stage('Set Version') {
            steps {
                mvn "versions:set -DnewVersion=\$(./mvnw help:evaluate -Dexpression=project.version | grep -e '^[^\\[\\/]')-$BUILD_NUMBER"
            }
            post {
                failure {
                    notify('FAIL', 'Failed')
                }
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
        stage('Tag Commit') {
            steps {
                echo "TODO: Tag commit: $GIT_COMMIT"
            }
        }
        stage('Deploy to DEV') {
            environment {
                DOCKER_HOST = "tcp://$DEV_HOST:2376"
                DOCKER_TLS_VERIFY = "1"
                DOCKER_CERT_PATH = "/root/.tls"

                SOURCE_IMAGE_TAG = "latest"
                IMAGE_TAG = "dev"
                SERVICE_NAME = "sbms-dev-app_$JOB_NAME"
            }
            steps {
                sh "docker pull $IMAGE_NAME:$SOURCE_IMAGE_TAG"
                sh "docker tag $IMAGE_NAME:$SOURCE_IMAGE_TAG $IMAGE_NAME:$IMAGE_TAG"
                withDockerRegistry([url: '', credentialsId: 'DOCKER_HUB_CREDENTIALS']) {
                    sh "docker push $IMAGE_NAME:$IMAGE_TAG"
                }
                sh "docker service update --image $IMAGE_NAME:$IMAGE_TAG $SERVICE_NAME"
            }
            post {
                failure {
                    notify('WARN', 'Failed to deploy properly to the *DEV* environment and may be in an unstable state.')
                }
            }
        }
        stage('Deploy to TEST') {
            environment {
                DOCKER_HOST = "tcp://$TEST_HOST:2376"
                DOCKER_TLS_VERIFY = "1"
                DOCKER_CERT_PATH = "/root/.tls"

                SOURCE_IMAGE_TAG = "dev"
                IMAGE_TAG = "test"
                SERVICE_NAME = "sbms-test-app_$JOB_NAME"
            }
            steps {
                sh "docker pull $IMAGE_NAME:$SOURCE_IMAGE_TAG"
                sh "docker tag $IMAGE_NAME:$SOURCE_IMAGE_TAG $IMAGE_NAME:$IMAGE_TAG"
                withDockerRegistry([url: '', credentialsId: 'DOCKER_HUB_CREDENTIALS']) {
                    sh "docker push $IMAGE_NAME:$IMAGE_TAG"
                }
                sh "docker service update --image $IMAGE_NAME:$IMAGE_TAG $SERVICE_NAME"
            }
            post {
                failure {
                    notify('WARN', 'Failed to deploy properly to the *TEST* environment and may be in an unstable state.')
                }
            }
        }
        stage('Wait For Environment') {
            steps {
                echo "Temporarily skipping this and the next step..."
                //script {
                //    if (! waitUntilActive("$TEST_HOST", "$TEST_PORT", 10, 30)) {
                //        currentBuild.result = 'FAILED'
                //    }
                //}
            }
            post {
                failure {
                    notify('WARN', 'The *TEST* environment did not become available in a reasonable time.')
                }
            }
        }
        stage('Run Application Tests') {
            steps {
                echo "Temporarily skipping this and the previous step..."
                //build('sbms-test')
            }
        }
        stage ('Tag Tested Image') {
            environment {
                SOURCE_IMAGE_TAG = "test"
                IMAGE_TAG = "test-passed"
            }
            steps {
                sh "docker tag $IMAGE_NAME:$SOURCE_IMAGE_TAG $IMAGE_NAME:$IMAGE_TAG"
                withDockerRegistry([url: '', credentialsId: 'DOCKER_HUB_CREDENTIALS']) {
                    sh "docker push $IMAGE_NAME:$IMAGE_TAG"
                }
            }
            post {
                failure {
                    notify('WARN', 'Tests were completed, but images were not properly marked as test-passed.')
                }
            }
        }
        stage('Deploy to UAT') {
            environment {
                DOCKER_HOST = "tcp://$UAT_HOST:2376"
                DOCKER_TLS_VERIFY = "1"
                DOCKER_CERT_PATH = "/root/.tls"

                SOURCE_IMAGE_TAG = "test-passed"
                IMAGE_TAG = "uat"
                SERVICE_NAME = "sbms-uat-app_$JOB_NAME"
            }
            steps {
                sh "docker pull $IMAGE_NAME:$SOURCE_IMAGE_TAG"
                sh "docker tag $IMAGE_NAME:$SOURCE_IMAGE_TAG $IMAGE_NAME:$IMAGE_TAG"
                withDockerRegistry([url: '', credentialsId: 'DOCKER_HUB_CREDENTIALS']) {
                    sh "docker push $IMAGE_NAME:$IMAGE_TAG"
                }
                sh "docker service update --image $IMAGE_NAME:$IMAGE_TAG $SERVICE_NAME"
            }
            post {
                failure {
                    notify('WARN', 'Failed to deploy properly to the *UAT* environment and may be in an unstable state.')
                }
            }
        }
        stage('Prepare Release Candidate') {
            environment {
                SOURCE_IMAGE_TAG = 'uat'
                IMAGE_TAG = 'rc'
            }
            steps {
                sh "docker tag $IMAGE_NAME:$SOURCE_IMAGE_TAG $IMAGE_NAME:$IMAGE_TAG"
                withDockerRegistry([url: '', credentialsId: 'DOCKER_HUB_CREDENTIALS']) {
                    sh "docker push $IMAGE_NAME:$IMAGE_TAG"
                }
            }
            post {
                failure {
                    notify('WARN', 'Was not able to be accepted as a Release Candidate.')
                }
            }
        }
        stage('Deploy to PROD') {
            environment {
                DOCKER_HOST = "tcp://$PROD_HOST:2376"
                DOCKER_TLS_VERIFY = "1"
                DOCKER_CERT_PATH = "/root/.tls"

                SOURCE_IMAGE_TAG = "rc"
                IMAGE_TAG = "prod"
                SERVICE_NAME = "sbms-prod-app_$JOB_NAME"
            }
            steps {
                sh "docker pull $IMAGE_NAME:$SOURCE_IMAGE_TAG"
                sh "docker tag $IMAGE_NAME:$SOURCE_IMAGE_TAG $IMAGE_NAME:$IMAGE_TAG"
                withDockerRegistry([url: '', credentialsId: 'DOCKER_HUB_CREDENTIALS']) {
                    sh "docker push $IMAGE_NAME:$IMAGE_TAG"
                }
                sh "docker service update --image $IMAGE_NAME:$IMAGE_TAG $SERVICE_NAME"
            }
            post {
                failure {
                    notify('PROD_FAIL', '*Failed to deploy to production!*')
                }
            }
        }
    }
    post {
        always {
            mvn "versions:revert"
        }
        success {
            notify('INFO', 'Succeeded')
        }
    }
}
