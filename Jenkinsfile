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
            sh """
                PID=\$(lsof -t -i:${APP_PORT} || true)
                if [ ! -z "\$PID" ]; then
                    echo "Arrêt de l'ancien processus (PID \$PID)"
                    kill -9 \$PID || true
                    sleep 2
                fi

                setsid nohup java -jar ./target/ibcgos_web-0.0.1-SNAPSHOT.jar > app.log 2>&1 < /dev/null &

                sleep 5
                echo "Application démarrée sur le port ${APP_PORT}"
            """
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