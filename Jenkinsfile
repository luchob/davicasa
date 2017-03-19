#!groovy

pipeline {
    agent any

    stages {
        stage('Building...') {
            steps {
				bat 'gradlew build'
	        }
        }
        stage('Executing test cases...') {
            steps {
                bat 'gradlew test'
            }
        }
        stage('Deploying...') {
			when {
              expression {
                currentBuild.result == null || currentBuild.result == 'SUCCESS' 
              }
            }
            steps {
                bat 'gradlew distZip'
            }
        }
    }
}