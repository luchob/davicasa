#!groovy

pipeline {
    agent any

    stages {
        stage('Building on Windows...') {
			agent { 
                label 'windows'
            }
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