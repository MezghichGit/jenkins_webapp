pipeline {
    agent any

    tools {
        maven 'Maven310' // doit correspondre au nom donné dans Global Tool Configuration
        jdk 'Java17'
    }

    stages {

        stage('1. Checkout') {
            steps {
                echo "Récupération du code depuis repo github privé"
            }
        }

        stage('2. Build JAR') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }
    }

    post {
        success {
            echo "Build réussi, jar généré dans target/"
        }
        failure {
            echo "Le build a échoué."
        }
    }
}