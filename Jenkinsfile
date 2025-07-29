pipeline {
    agent any

    tools {
        maven 'maven 3.9.11'
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
