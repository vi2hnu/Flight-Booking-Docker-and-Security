pipeline {
    agent any
    stages {
        stage('Packaging Services') {
            steps {
                git url: 'https://github.com/vi2hnu/Flight-Booking-Docker-and-Security.git', branch: 'main'
                
                sh 'mvn package'
            }
            
            post {
                success {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/TEST-*.xml'
                    archiveArtifacts '**/target/*.jar'
                }
            }
        }
        stage('Building Docker images'){
            steps{
                sh 'docker compose build'
            }
            post{
                success{
                    echo 'Docker images built successfully'
                }
            }
        }
        stage('Docker Compose'){
            steps{
                sh 'docker compose up -d'
            }
            post{
                success{
                    sh 'docker ps'
                }
            }
        }
    }
}
