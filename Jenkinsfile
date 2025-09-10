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
        stage('Setup Branch Helper') {
            steps {
                script {
                    // Function to check if current branch matches a list
                    env.BRANCH_NAME_STRIPPED = env.GIT_BRANCH?.replaceFirst(/^origin\//, '')

                    branchMatches = { allowedBranches ->
                        return allowedBranches.contains(env.BRANCH_NAME_STRIPPED)
                    }
                }
            }
        }

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
                sh 'mvn validate'
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Run Tests') {
            steps {
                echo "Running tests..."
                // sh 'mvn test'
            }
        }

        stage('Code Quality Analysis') {
            when {
                expression { branchMatches(['main','dev']) }
            }
            steps {
                echo "Running code quality checks..."
            }
        }

        stage('Security Scan') {
            when {
                expression { branchMatches(['main','dev']) }
            }
            steps {
                echo "Running security scan..."
            }
        }

        stage('Increment Version') {
            when {
                expression { branchMatches(['main','dev']) }
            }
            steps {
                script {
                    echo "Incrementing version..."
                    def currentVersion = sh(
                        script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout",
                        returnStdout: true
                    ).trim()
                    echo "Current version: ${currentVersion}"

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
                    env.GIT_TAG = "v${newVersion}"
                }
            }
        }

        stage('Commit Version Update') {
            when {
                expression { branchMatches(['main','dev']) }
            }
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: "github-credential", passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                        sh 'git config user.email "jenkins@jenkins.local"'
                        sh 'git config user.name "Jenkins CI"'

                        def branch = env.BRANCH_NAME_STRIPPED ?: "main"
                        sh "git remote set-url origin https://${USER}:${PASS}@github.com/${GITHUB_REPO}.git"

                        def hasChanges = sh(script: 'git diff --name-only', returnStdout: true).trim()
                        if (hasChanges) {
                            sh 'git add pom.xml'
                            sh "git commit -m 'ci: bump version to ${env.NEW_VERSION} [skip ci]'"
                            sh "git tag -a ${env.GIT_TAG} -m 'Release version ${env.NEW_VERSION}'"
                            sh "git push origin HEAD:${branch}"
                            sh "git push origin ${env.GIT_TAG}"
                        }
                    }
                }
            }
        }

        stage('Build Jar') {
            when {
                expression { branchMatches(['main','dev']) }
            }
            steps {
                sh 'mvn clean package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Build and Push Docker Image') {
            when {
                expression { branchMatches(['main','dev']) }
            }
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: "docker-hub-rep-credentials", passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                        def branch = env.BRANCH_NAME_STRIPPED ?: "main"

                        sh "docker build -t ${DOCKER_REGISTRY}/${APP_NAME}:${IMAGE_NAME} ."
                        sh "docker tag ${DOCKER_REGISTRY}/${APP_NAME}:${IMAGE_NAME} ${DOCKER_REGISTRY}/${APP_NAME}:${branch}-latest"

                        if (branch == 'main') sh "docker tag ${DOCKER_REGISTRY}/${APP_NAME}:${IMAGE_NAME} ${DOCKER_REGISTRY}/${APP_NAME}:latest"

                        sh "echo \$PASS | docker login -u ${USER} --password-stdin"
                        sh "docker push ${DOCKER_REGISTRY}/${APP_NAME}:${IMAGE_NAME}"
                        sh "docker push ${DOCKER_REGISTRY}/${APP_NAME}:${branch}-latest"

                        if (branch == 'main') sh "docker push ${DOCKER_REGISTRY}/${APP_NAME}:latest"
                    }
                }
            }
            post {
                always {
                    script {
                        sh "docker rmi ${DOCKER_REGISTRY}/${APP_NAME}:${IMAGE_NAME} || true"
                        sh "docker rmi ${DOCKER_REGISTRY}/${APP_NAME}:${env.BRANCH_NAME_STRIPPED}-latest || true"
                        if (env.BRANCH_NAME_STRIPPED == 'main') sh "docker rmi ${DOCKER_REGISTRY}/${APP_NAME}:latest || true"
                    }
                }
            }
        }

        stage('Deploy to Staging') {
            when {
                expression { branchMatches(['dev']) }
            }
            steps {
                echo "Deploying to staging environment..."
            }
        }

        stage('Deploy to Production') {
            when {
                expression { branchMatches(['main']) }
            }
            steps {
                input message: 'Deploy to Production?', ok: 'Deploy'
                echo "Deploying to production environment..."
            }
        }
    }

    post {
        always {
            echo "Pipeline completed for ${env.GIT_BRANCH}"
            cleanWs()
        }
    }
}
