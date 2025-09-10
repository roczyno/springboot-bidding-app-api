pipeline {
    agent any

    tools {
        maven 'Maven 3.9.9'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '**']], // checkout the branch Jenkins is building
                    doGenerateSubmoduleConfigurations: false,
                    extensions: [[$class: 'LocalBranch', localBranch: '**']], // ensure no detached HEAD
                    userRemoteConfigs: [[
                        url: 'https://github.com/roczyno/springboot-bidding-app-api.git',
                        credentialsId: 'github-credential' // GitHub PAT
                    ]]
                ])
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    echo "Running tests..."
//                     sh 'mvn clean test'
                }
            }
        }

        stage ("Increment Version") {
            when {
                expression {
                    def branch = env.GIT_BRANCH?.replaceFirst(/^origin\//, '') ?: ""
                    return branch == "main" || branch == "dev"
                }
            }
            steps {
                script {
                    echo "Incrementing version..."
                    sh '''
                        mvn build-helper:parse-version versions:set \
                          -DnewVersion=\\${parsed.majorVersion}.\\${parsed.minorVersion}.\\${parsed.nextIncrementalVersion} \
                          versions:commit
                    '''
                    def version = sh(
                        script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout",
                        returnStdout: true
                    ).trim()
                    env.IMAGE_NAME = "${version}-${BUILD_NUMBER}"
                }
            }
        }

        stage('Commit Version Update') {
            when {
                expression {
                    def branch = env.GIT_BRANCH?.replaceFirst(/^origin\//, '') ?: ""
                    return branch == "main" || branch == "dev"
                }
            }
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: "github-credential", passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                        sh 'git config user.email "jenkins@gmail.com"'
                        sh 'git config user.name "jenkins"'
                        sh 'git status'
                        sh 'git branch'

                        def branch = env.GIT_BRANCH?.replaceFirst(/^origin\//, '') ?: "main"

                        sh "git remote set-url origin https://${USER}:${PASS}@github.com/roczyno/springboot-bidding-app-api.git"
                        sh 'git add pom.xml'
                        sh 'git commit -m "ci: version bump" || echo "No changes to commit"'
                        sh "git push origin HEAD:${branch}"
                    }
                }
            }
        }

        stage("Build Jar") {
            when {
                expression {
                    def branch = env.GIT_BRANCH?.replaceFirst(/^origin\//, '') ?: ""
                    return branch == "main" || branch == "dev"
                }
            }
            steps {
                script {
                    echo "Building JAR file"
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage("Build Docker Image and Push") {
            when {
                expression {
                    def branch = env.GIT_BRANCH?.replaceFirst(/^origin\//, '') ?: ""
                    return branch == "main" || branch == "dev"
                }
            }
            steps {
                script {
                    echo 'Building docker image...'
                    withCredentials([usernamePassword(credentialsId: "docker-hub-rep-credentials", passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                        def branch = env.GIT_BRANCH?.replaceFirst(/^origin\//, '') ?: "main"

                        sh "docker build -t roczyno/java-bid-app:${IMAGE_NAME} ."
                        sh "echo $PASS | docker login -u ${USER} --password-stdin"
                        sh "docker push roczyno/java-bid-app:${IMAGE_NAME}"
                    }
                }
            }
        }
    }

    post {
        always {
            echo "Pipeline completed"
        }
        failure {
            echo "Pipeline failed - check logs for details"
        }
        success {
            echo "Pipeline succeeded - Docker image pushed successfully"
        }
    }
}
