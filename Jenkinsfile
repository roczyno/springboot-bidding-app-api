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
        stage('Package') {
            steps {
                echo 'Packaging application...'
                sh 'mvn clean package'
            }
        }
    }
}
