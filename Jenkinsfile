pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }
        stage('Deploy') {
            steps {
                sh "mkdir -p /mnt/jenkins/SuperJump"
                sh "rm ./target/original*"
                sh "cp ./target/*.jar /mnt/jenkins/SuperJump/"
            }
        }
    }

}
