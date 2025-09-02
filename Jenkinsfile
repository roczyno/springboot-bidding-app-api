pipeline {
    agent any

    tools {
        maven 'Maven 3.9.9'
    }

    stages {
        stage("Increment version") {
                    steps {
                        script {
                            echo "Incrementing Application Version"

                            sh '''
                                mvn build-helper:parse-version versions:set \
                                    -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.nextIncrementalVersion}
                                mvn versions:commit
                            '''

                            def version = sh(
                                script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout",
                                returnStdout: true
                            ).trim()

                            echo "Resolved version: ${version}"

                            env.PROJECT_VERSION = version
                            env.IMAGE_NAME = "${version}-${BUILD_NUMBER}"
                        }
                    }
        }

        stage("Commit Version Update") {
            steps {
                script {
                    withCredentials([
                        usernamePassword(
                            credentialsId: 'github-credential',
                            usernameVariable: 'USER',
                            passwordVariable: 'PASS'
                        )
                    ]) {
                        echo "Committing version change to GitHub"
                        sh 'git config user.email "jenkins@gmail.com"'
                        sh 'git config user.name "jenkins"'
                        sh 'git remote set-url origin https://$USER:$PASS@github.com/roczyno/springboot-bidding-app-api.git'
                        sh 'git add pom.xml'
                        sh 'git diff --quiet || git commit -m "ci: version bump"'
                        sh 'git push origin HEAD:refs/heads/main'
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

        stage("Archive Artifact") {
            steps {
                archiveArtifacts artifacts: "target/*.jar", fingerprint: true
            }
        }

        stage("Build Docker Image and Push") {
            steps {
                script {
                    echo "Building and pushing Docker image..."
                    
                    // Get the image name safely
                    def imageName = env.IMAGE_NAME
                    echo "Using image name: ${imageName}"
                    
                    withCredentials([
                        usernamePassword(
                            credentialsId: "docker-hub-rep-credentials",
                            passwordVariable: "PASS",
                            usernameVariable: "USER"
                        )
                    ]) {
                        // Use triple-quoted strings to avoid substitution issues
                        sh """
                            docker build -t roczyno/java-bidding-api:${imageName} .
                        """
                        
                        sh '''
                            echo $PASS | docker login -u $USER --password-stdin
                        '''
                        
                        sh """
                            docker push roczyno/java-bidding-api:${imageName}
                        """
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