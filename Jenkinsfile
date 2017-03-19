#!groovy

pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                if(isUnix()){
					sh 'gradlew build'
				}
				else{
					bat 'gradlew build'
				}
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
    }
}