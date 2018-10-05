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
                setVersion()
            }
            post {
                failure {
                    notifyFailure()
                }
            }
        }
        stage('Run Unit Tests') {
            steps {
                runUnitTests()
            }
            post {
                always {
                    archiveUnitTestResults()
                }
                failure {
                    notifyFailure()
                }
            }
        }
        stage('Build Application') {
            steps {
                buildApplication()
            }
            post {
                failure {
                    notifyFailure()
                }
            }
        }
        stage('Run Integration Tests') {
            steps {
                runIntegrationTests()
            }
            post {
                always {
                    archiveIntegrationTestResults()
                }
                failure {
                    notifyFailure()
                }
            }
        }
        stage('Install Contract Stubs') {
            steps {
                installContractStubs()
            }
            post {
                failure {
                    notifyFailure()
                }
            }
        }
        stage('Build Image') {
            steps {
                buildImage()
            }
            post {
                failure {
                    notifyFailure()
                }
            }
        }
        stage('Push Image to Registry') {
            steps {
                pushToDockerHub()
            }
            post {
                failure {
                    notifyFailure()
                }
            }
        }
        stage('Tag Commit') {
            steps {
                tagGitCommit()
            }
            post {
                failure {
                    notifyFailure()
                }
            }
        }
        stage('Deploy to DEV') {
            steps {
                promoteAndUpdateRunningImage("tcp://$DEV_HOST:2376", IMAGE_NAME, "latest", "dev", "sbms-dev_$JOB_NAME")
            }
            post {
                failure {
                    notifyFailure('Failed to deploy properly to the *DEV* environment and may be in an unstable state.')
                }
            }
        }
        stage('Deploy to TEST') {
            steps {
                promoteAndUpdateRunningImage("tcp://$TEST_HOST:2376", IMAGE_NAME, "dev", "test", "sbms-test_$JOB_NAME")
            }
            post {
                failure {
                    notifyFailure('Failed to deploy properly to the *TEST* environment and may be in an unstable state.')
                }
            }
        }
        stage('Wait For Environment') {
            steps {
                script {
                    currentBuild.result = waitForEnvironment(TEST_HOST, TEST_PORT)
                }
            }
            post {
                failure {
                    notifyFailure('The *TEST* environment did not become available in a reasonable time.')
                }
            }
        }
        stage('Run Acceptance Tests') {
            steps {
                runAcceptanceTests()
            }
            post {
                failure {
                    notifyFailure()
                }
            }
        }
        stage ('Tag Tested Image') {
            steps {
                tagImage(IMAGE_NAME, "test", "test-passed")
            }
            post {
                failure {
                    notifyFailure('Tests were completed, but images were not properly marked as test-passed.')
                }
            }
        }
        stage('Deploy to UAT') {
            steps {
                promoteAndUpdateRunningImage("tcp://$UAT_HOST:2376", IMAGE_NAME, "test-passed", "uat", "sbms-uat_$JOB_NAME")
            }
            post {
                failure {
                    notifyFailure('Failed to deploy properly to the *UAT* environment and may be in an unstable state.')
                }
            }
        }
        stage('Prepare Release Candidate') {
            steps {
                tagImage(IMAGE_NAME, "uat", "rc")
            }
            post {
                failure {
                    notifyFailure('Was not able to be accepted as a Release Candidate.')
                }
            }
        }
        stage('Deploy to PROD') {
            steps {
                promoteAndUpdateRunningImage("tcp://$PROD_HOST:2376", IMAGE_NAME, "rc", "prod", "sbms-prod_$JOB_NAME")
            }
            post {
                failure {
                    notifyProdFailure('Failed to deploy to production!')
                }
            }
        }
    }
    post {
        always {
            revertVersion()
        }
        success {
            notifyProdSuccess('Production deployment successfully completed!')
        }
    }
}
