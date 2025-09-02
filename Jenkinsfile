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
                            -DnewVersion=\\${parsed.majorVersion}.\\${parsed.minorVersion}.\\${parsed.nextIncrementalVersion} \
                            versions:commit
                    '''

                    def version = sh(
                        script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout",
                        returnStdout: true
                    ).trim()

                    echo "Resolved version: ${version}"

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
                                        sh 'git commit -m "ci: version bump" || echo "No changes to commit"'
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

    }
}
