node {
def BranchName = '*/master'
def GitRepository = 'https://github.com/Sanwel/JavaApp'
def CredentialsId = '6da246df-c194-4f83-bdfa-9edee7ca39a2'
def mvnHome = tool 'maven'
def Response
def sonarHome = tool name: 'sonarscanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
String Recipient ="Maksym_Husak@epam.com"
        try{
            stage('Git-Checkout') {
                echo 'Git Checkout'
                checkout([$class: 'GitSCM', branches: [[name: BranchName]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: CredentialsId , url: GitRepository ]]])
                def shortCommit = sh(returnStdout: true, script: "git log -n 1 --pretty=format:\'%h\'").trim()
            }
            stage ('Build') {
                echo 'Maven Build'
                sh "'${mvnHome}/bin/mvn' -Dmaven.test.failure.ignore clean package"
                BUILD_STATUS = "SUCCESS"
            }
            stage ('SonarQube testing') {
                echo 'SonarQube Test'
                withSonarQubeEnv('SonarQube') {
                    sh "${sonarHome}/bin/sonar-scanner -Dsonar.projectKey=Simple-App -Dsonar.projectName=Simple-App -Dsonar.java.binaries=target/classes/  -Dsonar.sources=src/"
                }
            }
            stage ('Dockerize') {
/*                agent {
                    docker { image 'java:8-alpine'}
                }
                steps {
                    sh 'java -jar target/rd-1.0-SNAPSHOT.jar'
                }*/
                echo 'Run Application in Docker'
                sh '''docker build . -t myapp:1
                docker run -d --name Olen -it -p 8181:8080 myapp:1 '''   
            } 
            stage('Docker Check') {
                echo 'Check Successful docker container Up'
                sleep 5
                timeout (time: 15, unit:'SECONDS') {
                    while(Response!="HTTP/1.1 200") {
                        def Curl = "curl -I http://10.28.12.209:8181/health".execute().text
                        Response = Curl[0..11]
                    }
                }
                
            }
            stage('Mail'){
                echo 'Send email notification'
                if(Response.equals("HTTP/1.1 200")) {
                    emailext(subject: "${env.JOB_NAME} was " + "${env.BUILD_STATUS}", body: "Commit short hash " + "${shortCommit}", to: Recipient, replyTo: '');
                }else {
                      System.exit(1)
                }
            }           
} catch (all) {
    echo 'Catch Errors'
    env.BUILD_STATUS = "FAILURE"
    emailext(subject: "${env.JOB_NAME} was + ${env.BUILD_STATUS}", body: "Commit short hash " + "${shortCommit}", to: Recipient, replyTo: '');    
} /*finally {
    sh 'docker rm -f Olen'
    deleteDir()
}*/
}
