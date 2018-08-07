pipeline {
    agent any
def sonarHome = tool name: 'SonarQube', type: 'hudson.plugind.sonar.SonarRunnerInstallation'
def mvnHome = tool name: 'maven'
String Olen = ""
    stages {
        stage('Git-Checkout') {
        checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '6da246df-c194-4f83-bdfa-9edee7ca39a2', url: 'https://github.com/Sanwel/JavaApp']]])
        }
        stage ('Build') {
            sh "'${mvnHome}/bin/mvn' -Dmaven.test.failure.ignore clean package"
        }
        stage ('SonarQube testing') {
            sh "${sonarHome}/bin/sonar-scanner -Dsonar.projectKey=Simple-App -Dsonar.projectName=Simple-App -Dsonar.sources=src/"
        }
        stage ('Dockerize') {
        sh '''docker build . -t myapp:1
docker run -it -p 8080:8080 myapp:1'''   
        } 
        stage('Docker Check') {
            while(Olen!="HTTP/1.1 200 OK" ||(System.rurrentTimeMillis()-startTime)<60000) {
                Olen= $(curl -I http://10.28.12.209:8080/health | grep "HTTP/1.1 200 OK")
            }
        }
    }
}   
