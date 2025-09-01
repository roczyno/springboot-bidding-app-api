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
    }
}
