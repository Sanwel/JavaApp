pipeline {
    agent any
node {
def sonarHome = tool name: 'SonarQube', type: 'hudson.plugind.sonar.SonarRunnerInstallation'
mvnHome = tool 'maven'
String Olen = " "
    stages {     
        try{
            stage('Git-Checkout') {
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '6da246df-c194-4f83-bdfa-9edee7ca39a2', url: 'https://github.com/Sanwel/JavaApp']]])
            }
            stage ('Build') {
                sh "'${mvnHome}/bin/mvn' -Dmaven.test.failure.ignore clean package"
            }
            stage ('SonarQube testing') {
                withSonarQubeEnv('sonarqube') {
                    sh "${sonarHome}/bin/sonar-scanner -Dsonar.projectKey=Simple-App -Dsonar.projectName=Simple-App -Dsonar.sources=src/main/java/rd/pingable/"
                }
            }
            stage ('Dockerize') {
                sh '''docker build . -t myapp:1
                docker run -d -it -p 8080:8080 myapp:1'''   
            } 
            stage('Docker Check') {
                while(Olen!="HTTP/1.1 200 OK" ||(System.currentTimeMillis()-startTime)<60000) {
                    def Curl = "curl -I http://10.28.12.209:8080/health".execute().text
                    Olen = Curl[0..15]
                    println Olen
                }
            }
            stage('Mail'){
                if(Olen) {
                    println Olen
                    mail bcc: '', body: '''"Success" 
                    shortCommit = sh(returnStdout: true, script: "git log -n 1 --pretty=format:\'%h\'").trim()''', cc: '', from: '', replyTo: '', subject: 'Build status', to: 'Maksym_Husak@epam.com'
                }else {
                      System.exit(0)
                }
            }           
}catch (all) {
mail bcc: '', body: '''"Error" 
shortCommit = sh(returnStdout: true, script: "git log -n 1 --pretty=format:\'%h\'").trim()''', cc: '', from: '', replyTo: '', subject: 'Build status', to: 'Maksym_Husak@epam.com'
currentBuild.result = 'FAILURE'
} finally {

    sh 'docker rm -f myapp:1'
    deleteDir()
}
}
}
}
