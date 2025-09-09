pipeline {
    agent any

    tools {
        maven 'Maven 3.9.9'
    }

    stages {
        stage ("increment version") {
            steps {
                script {
                    echo "Incrementing version..."
                    sh '''
                        mvn build-helper:parse-version versions:set \
                          -DnewVersion=\\${parsed.majorVersion}.\\${parsed.minorVersion}.\\${parsed.incrementalVersion} \
                          versions:commit
                    '''
                    def pomContent = readFile("pom.xml")
                    def matcher = pomContent =~ '(?m)<version>([^<]+)</version>'
                    def version = matcher[0][1].trim()
                    env.IMAGE_NAME = "${version}-${BUILD_NUMBER}"
                }
            }
        }

        stage('Commit Version update') {
            steps {
                script {

                    withCredentials([usernamePassword(credentialsId: "github-credential", passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                        sh 'git config user.email "jenkins@gmail.com"'
                        sh 'git config user.name "jenkins"'
                        sh 'git status'
                        sh 'git branch'
                        sh "git add ."
                        sh 'git commit -m "ci: version bump" || echo "No changes to commit"'
                        sh "git push origin main"
                    }
                }
            }
        }

        stage("Build Jar") {
            steps {
                script {
                    echo "Building JAR file"
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage("Build Docker Image and Push") {
            steps {
                script {
                    echo 'Building docker image...'
                    withCredentials([
                        usernamePassword(
                            credentialsId: "dockerhub-credentials",
                            passwordVariable: 'PASS',
                            usernameVariable: 'USER'
                        )
                    ]) {
                        sh "docker build -t roczyno/java-project-management-api:${IMAGE_NAME} ."
                        sh "echo $PASS | docker login -u ${USER} --password-stdin"
                        sh "docker push roczyno/java-project-management-api:${IMAGE_NAME}"
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
