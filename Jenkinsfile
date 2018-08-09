node {
def mvnHome
def Response
mvnHome = tool 'maven'
def sonarHome = tool name: 'sonarscanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
String to="Maksym_Husak@epam.com"
        try{
            stage('Git-Checkout') {
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '6da246df-c194-4f83-bdfa-9edee7ca39a2', url: 'https://github.com/Sanwel/JavaApp']]])
            }
            stage ('Build') {
                sh "'${mvnHome}/bin/mvn' -Dmaven.test.failure.ignore clean package"
            }
            stage ('SonarQube testing') {
                withSonarQubeEnv('SonarQube') {
                    sh 'echo $PWD'
                    sh "${sonarHome}/bin/sonar-scanner -Dsonar.projectKey=Simple-App -Dsonar.projectName=Simple-App -Dsonar.java.binaries=target/classes/  -Dsonar.sources=src/"
                }
            }
            stage ('Dockerize') {
                sh '''docker build . -t myapp:1
                docker run -d --name Olen -it -p 8181:8080 myapp:1 '''   
            } 
            stage('Docker Check') {
                long start_time = System.currentTimeMillis();
                long wait_time = 15000;
                long end_time = start_time + wait_time
                sleep 5
                while(Response!="HTTP/1.1 200" && (System.currentTimeMillis() < end_time)){
                    def Curl = "curl -I http://10.28.12.209:8181/health".execute().text
                    Response = Curl[0..11]
                }
                
            }
            stage('Mail'){
                if(Response.equals("HTTP/1.1 200")) {
                    env.shortCommit = sh(returnStdout: true, script: "git log -n 1 --pretty=format:\'%h\'").trim()
                    env.BUILD_STATUS = "SUCCESS"
                    String body = "Commit short hash " + "${env.shortCommit}";
                    String subject = "${env.JOB_NAME} was " + "${env.BUILD_STATUS}";
                    emailext(subject: subject, body: body, to: to, replyTo: '');
                }else {
                      System.exit(1)
                }
            }           
}catch (all) {
env.BUILD_STATUS = "FAILURE"
env.shortCommit = sh(returnStdout: true, script: "git log -n 1 --pretty=format:\'%h\'").trim()
String body = "Commit hash " + "${env.shortCommit}";
String subject = "${env.JOB_NAME} was " + "${env.BUILD_STATUS}";
emailext(subject: subject, body: body, to: to, replyTo: '');    
}finally {

    sh 'docker rm -f Olen'
    deleteDir()
}
}
