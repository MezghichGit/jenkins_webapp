pipeline {
    agent any

	tools {
        maven 'Maven310' // doit correspondre au nom donné dans Global Tool Configuration
        jdk 'Java17'
    }
    environment {
        APP_PORT = '6701'
    }

    stages {

        stage('1. Checkout') {
            steps {
                git branch: 'master', // ou 'master', adaptez à votre branche
                    url: 'https://github.com/MezghichGit/jenkins_webapp.git'
                    
            }
        }

        stage('2. Build JAR') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('3. Run JAR') {
            steps {
                script {
                 

                    // Lance le jar en arrière-plan, écoute sur toutes les interfaces
                    sh 'java -jar /target/ibcgos_web-0.0.1-SNAPSHOT.jar'
                }
            }
        }
    }

    post {
        success {
            echo "Déploiement réussi. Accessible sur http://jenkins.sip-academy.com:${APP_PORT}"
        }
        failure {
            echo "Le build ou le déploiement a échoué."
        }
    }
}