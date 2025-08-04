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
                sh 'mvn test'
            }
        }

        stage("Increment version") {
            steps {
                script {
                    echo "Incrementing Application Version"

                    sh '''mvn build-helper:parse-version versions:set \
                        -DnewVersion=${parsed.majorVersion}.${parsed.minorVersion}.${parsed.incrementalVersion} \
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
    }
}
