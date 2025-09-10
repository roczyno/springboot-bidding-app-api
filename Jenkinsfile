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
        stage('Checkout') {
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

        stage('Validate') {
            steps {
                script {
                    echo "Validating Maven project..."
                    sh 'mvn validate'
                }
            }
        }

        stage('Compile') {
            steps {
                script {
                    echo "Compiling project..."
                    sh 'mvn clean compile'
                }
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    echo "Running tests..."
                    sh 'mvn test || echo "Tests failed but continuing pipeline"'

                    // Publish test results
                    publishTestResults testResultsPattern: 'target/surefire-reports/*.xml'

                    // Archive test reports
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/surefire-reports',
                        reportFiles: '*.html',
                        reportName: 'Test Report'
                    ])
                }
            }
            post {
                always {
                    // Archive test artifacts even if tests fail
                    archiveArtifacts artifacts: 'target/surefire-reports/**', allowEmptyArchive: true
                }
            }
        }

        stage('Code Quality Analysis') {
            when {
                anyOf {
                    branch 'main'
                    branch 'dev'
                    changeRequest()
                }
            }
            steps {
                script {
                    echo "Running code quality checks..."
                    // Uncomment if you have SonarQube configured
                    // withSonarQubeEnv('SonarQube') {
                    //     sh 'mvn sonar:sonar'
                    // }

                    // Alternative: SpotBugs for static analysis
                    sh 'mvn compile spotbugs:check'
                }
            }
        }

        stage('Security Scan') {
            when {
                anyOf {
                    branch 'main'
                    branch 'dev'
                }
            }
            steps {
                script {
                    echo "Running security scan..."
                    sh 'mvn org.owasp:dependency-check-maven:check'
                }
            }
            post {
                always {
                    // Archive OWASP dependency check report if it exists
                    archiveArtifacts artifacts: 'target/dependency-check-report.html', allowEmptyArchive: true
                }
            }
        }

        stage ("Increment Version") {
            when {
                anyOf {
                    branch 'main'
                    branch 'dev'
                }
            }
            steps {
                script {
                    echo "Incrementing version..."

                    // Get current version first
                    def currentVersion = sh(
                        script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout",
                        returnStdout: true
                    ).trim()
                    echo "Current version: ${currentVersion}"

                    // Increment version
                    sh '''
                        mvn build-helper:parse-version versions:set \
                          -DnewVersion=\\${parsed.majorVersion}.\\${parsed.minorVersion}.\\${parsed.nextIncrementalVersion} \
                          versions:commit
                    '''

                    def newVersion = sh(
                        script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout",
                        returnStdout: true
                    ).trim()

                    echo "New version: ${newVersion}"
                    env.NEW_VERSION = newVersion
                    env.IMAGE_NAME = "${newVersion}-${BUILD_NUMBER}"

                    // Create git tag for the version
                    env.GIT_TAG = "v${newVersion}"
                }
            }
        }

        stage('Commit Version Update') {
            when {
                anyOf {
                    branch 'main'
                    branch 'dev'
                }
            }
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
            when {
                anyOf {
                    branch 'main'
                    branch 'dev'
                }
            }
            steps {
                script {
                    echo "Building JAR file..."
                    sh 'mvn clean package -DskipTests'

                    // Archive the built JAR
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        stage("Build and Push Docker Image") {
            when {
                anyOf {
                    branch 'main'
                    branch 'dev'
                }
            }
            steps {
                script {
                    echo "Building Docker image: ${DOCKER_REGISTRY}/${APP_NAME}:${IMAGE_NAME}"

                    withCredentials([usernamePassword(credentialsId: "docker-hub-rep-credentials", passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                        def branch = env.GIT_BRANCH?.replaceFirst(/^origin\//, '') ?: "main"

                        // Build image with multiple tags
                        sh "docker build -t ${DOCKER_REGISTRY}/${APP_NAME}:${IMAGE_NAME} ."
                        sh "docker tag ${DOCKER_REGISTRY}/${APP_NAME}:${IMAGE_NAME} ${DOCKER_REGISTRY}/${APP_NAME}:${branch}-latest"

                        if (branch == 'main') {
                            sh "docker tag ${DOCKER_REGISTRY}/${APP_NAME}:${IMAGE_NAME} ${DOCKER_REGISTRY}/${APP_NAME}:latest"
                        }

                        // Login and push
                        sh "echo \$PASS | docker login -u ${USER} --password-stdin"
                        sh "docker push ${DOCKER_REGISTRY}/${APP_NAME}:${IMAGE_NAME}"
                        sh "docker push ${DOCKER_REGISTRY}/${APP_NAME}:${branch}-latest"

                        if (branch == 'main') {
                            sh "docker push ${DOCKER_REGISTRY}/${APP_NAME}:latest"
                        }

                        echo "Docker images pushed successfully"
                    }
                }
            }
            post {
                always {
                    // Clean up local images to save disk space
                    script {
                        sh """
                            docker rmi ${DOCKER_REGISTRY}/${APP_NAME}:${IMAGE_NAME} || true
                            docker rmi ${DOCKER_REGISTRY}/${APP_NAME}:${env.GIT_BRANCH?.replaceFirst(/^origin\//, '') ?: 'main'}-latest || true
                        """
                        if (env.GIT_BRANCH?.replaceFirst(/^origin\//, '') == 'main') {
                            sh "docker rmi ${DOCKER_REGISTRY}/${APP_NAME}:latest || true"
                        }
                    }
                }
            }
        }

        stage('Deploy to Staging') {
            when {
                branch 'dev'
            }
            steps {
                script {
                    echo "Deploying to staging environment..."
                    // Add your staging deployment logic here
                    // Example: kubectl, docker-compose, or API calls to your deployment system
                }
            }
        }

        stage('Deploy to Production') {
            when {
                branch 'main'
            }
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
                if (env.IMAGE_NAME) {
                    echo "Docker image: ${DOCKER_REGISTRY}/${APP_NAME}:${IMAGE_NAME}"
                }

                // Send success notification
                // slackSend (
                //     color: 'good',
                //     message: ":white_check_mark: Build successful: ${env.JOB_NAME} - ${env.BUILD_NUMBER}"
                // )
            }
        }
        unstable {
            echo "Pipeline completed with warnings"
        }
    }
}
