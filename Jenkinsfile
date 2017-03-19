#!groovy

pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
				bat 'gradlew build'
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