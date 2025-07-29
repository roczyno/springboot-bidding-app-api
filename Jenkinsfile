pipeline {
    agent any

    tools {
        maven 'Maven 3.9.11'
    }

    stages {
        stage('Package') {
            steps {
                echo 'Packaging application...'
                sh 'mvn clean package'
            }
        }
    }
}
