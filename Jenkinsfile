pipeline {
    agent any

    tools {
        maven 'Maven 3.9.9'
    }

    stages {
        stage("Init") {
            steps {
                echo "Initializing..."
            }
        }

        stage("Test") {
            steps {
                echo "Running tests..."
            }
        }

        stage("Increment version") {
            steps {
                script {
                    echo "Incrementing Application Version"

                    sh '''mvn build-helper:parse-version versions:set \
                        -DnewVersion=\\${parsed.majorVersion}.\\${parsed.minorVersion}.\\${parsed.incrementalVersion} \
                        versions:commit'''

                    def pom = readFile("pom.xml")
                    def matcher = pom =~ '<version>(.+)</version>'
                    def version = matcher ? matcher[0][1] : "0.0.0"

                    echo "Resolved version: ${version}"

                    env.IMAGE_NAME = "${version}-${BUILD_NUMBER}"
                }
            }
        }


        stage('Package') {
            steps {
                echo 'Packaging application without running tests...'
                sh 'mvn clean package -DskipTests'
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
                                sh 'git commit -m "ci: version bump" || echo "No changes to commit"'
                                sh 'git push origin HEAD:refs/heads/main'
                            }
                        }
                    }
                }

        stage("Build Image"){
            steps{
              script {
                 echo "Building docker image..."
                 withCredentials([
                      usernamePassword(
                          credentialsId: "docker-hub-rep-credentials",
                          passwordVariable:"PASS",
                          usernameVariable: "USER"
                      )
                 ]){

                    sh "docker build -t roczyno/java-bidding-api:${IMAGE_NAME} ."
                    sh 'echo $PASS | docker login -u $USER --password-stdin'
                    sh "docker push roczyno/java-bidding-api:${IMAGE_NAME}"
                 }
              }


            }
        }
    }
}
