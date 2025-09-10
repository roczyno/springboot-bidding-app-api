pipeline {
    agent any

    tools {
        maven 'Maven 3.9.9'
    }

    environment {
        DOCKER_REGISTRY = 'roczyno'
        APP_NAME = 'java-bid-app'
        GITHUB_REPO = 'roczyno/springboot-bidding-app-api'
    }

    stages {
        stage('Checkouts') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '**']],
                    doGenerateSubmoduleConfigurations: false,
                    extensions: [[$class: 'LocalBranch', localBranch: '**']],
                    userRemoteConfigs: [[
                        url: "https://github.com/${GITHUB_REPO}.git",
                        credentialsId: 'github-credential'
                    ]]
                ])
            }
        }



        stage('Run Tests') {
            steps {
                script {
                    echo "Running tests..."

                }
            }

        }




stage ("Increment Version") {
    steps {
        script {
            echo "Incrementing version..."

            // Get current version first
            def currentVersion = sh(
                script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout",
                returnStdout: true
            ).trim()
            echo "Current version: ${currentVersion}"
            def versionParts = currentVersion.tokenize('.')
            def major = versionParts[0] as Integer
            def minor = versionParts[1] as Integer
            def patch = versionParts[2] as Integer
            def newPatch = patch + 1
            def newVersion = "${major}.${minor}.${newPatch}"


            sh "mvn versions:set -DnewVersion=${newVersion} versions:commit"

            echo "New version: ${newVersion}"
            env.NEW_VERSION = newVersion
            env.IMAGE_NAME = "${newVersion}-${BUILD_NUMBER}"
            env.GIT_TAG = "v${newVersion}"
        }
    }
}
        stage('Commit Version Update') {

            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: "github-credential", passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                        sh 'git config user.email "jenkins@jenkins.local"'
                        sh 'git config user.name "Jenkins CI"'

                        def branch = env.GIT_BRANCH?.replaceFirst(/^origin\//, '') ?: "main"
                        echo "Working on branch: ${branch}"

                        sh "git remote set-url origin https://${USER}:${PASS}@github.com/${GITHUB_REPO}.git"

                        // Check if there are changes to commit
                        def hasChanges = sh(
                            script: 'git diff --name-only',
                            returnStdout: true
                        ).trim()

                        if (hasChanges) {
                            sh 'git add pom.xml'
                            sh "git commit -m 'ci: bump version to ${env.NEW_VERSION} [skip ci]'"
                            sh "git tag -a ${env.GIT_TAG} -m 'Release version ${env.NEW_VERSION}'"
                            sh "git push origin HEAD:${branch}"
                            sh "git push origin ${env.GIT_TAG}"
                            echo "Version bumped and tagged: ${env.GIT_TAG}"
                        } else {
                            echo "No version changes to commit"
                        }
                    }
                }
            }
        }

        stage("Build Jar") {

            steps {
                script {
                    echo "Building JAR file..."
                    sh 'mvn clean package -DskipTests'


                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        stage("Build and Push Docker Image") {
            steps {
                script {
                    def imageTag = "${env.NEW_VERSION}-${BUILD_NUMBER}"
                    def fullImageName = "${DOCKER_REGISTRY}/${APP_NAME}:${imageTag}"

                    // Store for cleanup and reporting
                    env.IMAGE_TAG = imageTag
                    env.DOCKER_IMAGE = fullImageName

                    echo "Building Docker image: ${fullImageName}"

                    withCredentials([usernamePassword(credentialsId: "docker-hub-rep-credentials", passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                        sh "docker build -t ${fullImageName} ."
                        sh "echo \$PASS | docker login -u ${USER} --password-stdin"
                        sh "docker push ${fullImageName}"
                        echo "Docker image pushed successfully: ${fullImageName}"
                    }
                }
            }
            post {
                always {
                    script {
                        // Now we can use the environment variable
                        sh "docker rmi ${DOCKER_REGISTRY}/${APP_NAME}:${env.IMAGE_TAG} || true"
                    }
                }
            }
        }

        stage('Deploy to Staging') {
//             when {
//                 branch 'dev'
//             }
            steps {
                script {
                    echo "Deploying to staging environment..."
                    // Add your staging deployment logic here
                    // Example: kubectl, docker-compose, or API calls to your deployment system
                }
            }
        }

        stage('Deploy to Production') {
//             when {
//                 branch 'main'
//             }
            steps {
                input message: 'Deploy to Production?', ok: 'Deploy'
                script {
                    echo "Deploying to production environment..."
                    // Add your production deployment logic here
                }
            }
        }
    }

    post {
        always {
            echo "Pipeline completed for ${env.GIT_BRANCH}"

            // Clean workspace conditionally
            script {
                if (currentBuild.result != 'SUCCESS') {
                    echo "Build failed, preserving workspace for debugging"
                } else {
                    cleanWs()
                }
            }
        }
        failure {
            script {
                echo "Pipeline failed - check logs for details"

                // Send notification (uncomment and configure as needed)
                // emailext (
                //     subject: "Build Failed: ${env.JOB_NAME} - ${env.BUILD_NUMBER}",
                //     body: "Build failed. Check console output at ${env.BUILD_URL}",
                //     to: "${env.CHANGE_AUTHOR_EMAIL}"
                // )
            }
        }
        success {
            script {
                    echo "Pipeline succeeded"
                    if (env.DOCKER_IMAGE) {
                        echo "Docker image: ${env.DOCKER_IMAGE}"
                    }
                }

    }
}
