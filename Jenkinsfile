pipeline {
    agent any

    tools {
        maven 'Maven310'
        jdk 'Java17'
    }

    environment {
        // Identifiant utilisateur (sert aux credentials Sonar/Nexus et à la clé projet)
        USER_ID = 'user5'

        // GitHub public repository
        GITHUB_REPO   = 'https://github.com/MezghichGit/jenkins_webapp.git'
        GITHUB_BRANCH = 'master'

        // Docker Hub
        DOCKERHUB_USERNAME    = 'aminesip'
        DOCKERHUB_CREDENTIALS = 'dockerhub-credentials-mezghich'
        IMAGE_NAME            = 'ibcgos-app-springboot-mezghich'

        // Docker container
        CONTAINER_NAME = 'ibcgos-app-springboot-mezghich-container'
        HOST_PORT      = '8065'
        CONTAINER_PORT = '8080'   // port par défaut de Spring Boot

        // Gmail SMTP
        EMAIL_CREDENTIALS = 'Cred-Gmail-Mezghich'
        EMAIL_TO          = 'ma.mezghich@smart-it-partner.com'

        // URLs publiques des serveurs
        NEXUS_URL = 'http://57.131.31.88:8097'
        SONAR_URL = 'http://57.131.31.88:9000'

        // Calculé automatiquement à partir de USER_ID
        NEXUS_REPO  = "${NEXUS_URL}/repository/maven-snapshots/"
        PROJECT_KEY = "${USER_ID}-Lab_Spring_ibcgos"
    }

    stages {

        stage('1. Checkout') {
            steps {
                echo 'Recuperation du code depuis GitHub...'
                git branch: "${GITHUB_BRANCH}", url: "${GITHUB_REPO}"
            }
        }

        stage('2. Build & Tests') {
            steps {
                echo 'Compilation, tests et rapport de couverture JaCoCo...'
                sh 'mvn -B clean verify'
                echo 'JAR genere avec succes.'
            }
        }

        stage('3. Analyse SonarQube') {
            steps {
                withCredentials([string(credentialsId: "sonar-token-${USER_ID}", variable: 'SONAR_TOKEN')]) {
                    sh '''mvn -B org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
                          -Dsonar.host.url=$SONAR_URL \
                          -Dsonar.token=$SONAR_TOKEN \
                          -Dsonar.projectKey=$PROJECT_KEY \
                          -Dsonar.projectName=$PROJECT_KEY'''
                }
            }
        }

        stage('4. Build Docker Image') {
            steps {
                echo 'Construction de l image Docker...'
                sh '''
                    docker build \
                      -t ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${BUILD_NUMBER} \
                      -t ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:latest \
                      .
                '''
            }
        }

        stage('5. DockerHub Login & Push') {
            steps {
                echo 'Connexion et publication sur Docker Hub...'
                withCredentials([usernamePassword(credentialsId: "${DOCKERHUB_CREDENTIALS}",
                                                  usernameVariable: 'DH_USER',
                                                  passwordVariable: 'DH_TOKEN')]) {
                    sh '''
                        set +x
                        trap 'docker logout >/dev/null 2>&1 || true' EXIT
                        printf '%s' "$DH_TOKEN" | docker login -u "$DH_USER" --password-stdin
                        docker push ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${BUILD_NUMBER}
                        docker push ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:latest
                    '''
                }
            }
        }

        stage('6. Stop Old Container') {
            steps {
                echo 'Arret de l ancienne instance...'
                sh '''
                    docker stop ${CONTAINER_NAME} || true
                    docker rm ${CONTAINER_NAME} || true
                '''
            }
        }

        stage('7. Deploy Container') {
            steps {
                echo 'Demarrage du nouveau conteneur Spring Boot...'
                sh '''
                    docker run -d \
                      --name ${CONTAINER_NAME} \
                      --restart unless-stopped \
                      -p ${HOST_PORT}:${CONTAINER_PORT} \
                      ${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${BUILD_NUMBER}
                '''
            }
        }

        stage('8. Verify Deployment') {
            steps {
                echo 'Verification du deploiement...'
                sh '''
                    sleep 15
                    docker ps --filter "name=${CONTAINER_NAME}"
                    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:${HOST_PORT}/ || true)
                    echo "HTTP Status: ${HTTP_CODE}"
                    if [ "$HTTP_CODE" = "000" ] || [ "$HTTP_CODE" -ge 500 ]; then
                        echo "Application non accessible !"
                        docker logs --tail 50 ${CONTAINER_NAME}
                        exit 1
                    fi
                '''
            }
        }
    }

    post {
        success {
            echo 'DEPLOIEMENT SPRING BOOT REUSSI !'
            emailext(
                subject: "SUCCESS: ${JOB_NAME} - Build #${BUILD_NUMBER}",
                mimeType: 'text/html',
                to: "${EMAIL_TO}",
                body: """
                    <h2 style="color:green;">Pipeline CI/CD réussie !</h2>
                    <p>Bonjour,</p>
                    <p>Le déploiement de votre application Spring Boot a été effectué avec succès.</p>
                    <table border="1" cellpadding="8" cellspacing="0">
                        <tr><td><b>Projet</b></td><td>${JOB_NAME}</td></tr>
                        <tr><td><b>Build</b></td><td>#${BUILD_NUMBER}</td></tr>
                        <tr><td><b>Statut</b></td><td>SUCCESS</td></tr>
                        <tr><td><b>Image Docker</b></td><td>${DOCKERHUB_USERNAME}/${IMAGE_NAME}:${BUILD_NUMBER}</td></tr>
                        <tr><td><b>Container</b></td><td>${CONTAINER_NAME}</td></tr>
                        <tr><td><b>Application</b></td><td>http://jenkins.sip-academy.com:${HOST_PORT}</td></tr>
                        <tr><td><b>SonarQube</b></td><td>${SONAR_URL}/dashboard?id=${PROJECT_KEY}</td></tr>
                    </table>
                    <p><a href="${BUILD_URL}">Consulter le build Jenkins</a></p>
                    <p>Cordialement,<br>Jenkins CI/CD</p>
                """
            )
        }

        failure {
            echo 'ECHEC DE LA PIPELINE CI/CD !'
            emailext(
                subject: "FAILURE: ${JOB_NAME} - Build #${BUILD_NUMBER}",
                mimeType: 'text/html',
                to: "${EMAIL_TO}",
                body: """
                    <h2 style="color:red;">Échec de la pipeline CI/CD !</h2>
                    <p>Bonjour,</p>
                    <p>Une erreur est survenue pendant l'exécution de votre pipeline Spring Boot.</p>
                    <table border="1" cellpadding="8" cellspacing="0">
                        <tr><td><b>Projet</b></td><td>${JOB_NAME}</td></tr>
                        <tr><td><b>Build</b></td><td>#${BUILD_NUMBER}</td></tr>
                        <tr><td><b>Statut</b></td><td>FAILURE</td></tr>
                        <tr><td><b>Image Docker</b></td><td>${DOCKERHUB_USERNAME}/${IMAGE_NAME}</td></tr>
                    </table>
                    <p><a href="${BUILD_URL}console">Consulter les logs Jenkins</a></p>
                    <p>Cordialement,<br>Jenkins CI/CD</p>
                """
            )
        }

        always {
            echo 'Fin de la pipeline Jenkins.'
        }
    }
}