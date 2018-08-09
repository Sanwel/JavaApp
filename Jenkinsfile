node {
def mvnHome
def Response
mvnHome = tool 'maven'
String body = "${env.BUILD_STATUS} " + "${env.shortCommit}";
String to="Maksym_Husak@epam.com"
//         try{
            stage('Git-Checkout') {
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '6da246df-c194-4f83-bdfa-9edee7ca39a2', url: 'https://github.com/Sanwel/JavaApp']]])
            }
            stage ('Build') {
                sh "'${mvnHome}/bin/mvn' -Dmaven.test.failure.ignore clean package"
            }
            stage ('SonarQube testing') {
                def sonarHome = tool name: 'sonarscanner', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
                withSonarQubeEnv('SonarQube') {
                    sh 'echo $PWD'
                    sh "${sonarHome}/bin/sonar-scanner -Dsonar.projectKey=Simple-App -Dsonar.projectName=Simple-App -Dsonar.java.binaries=target/classes/  -Dsonar.sources=src/"
                }
            }
}
/*            stage ('Dockerize') {
                sh '''docker build . -t myapp:1
                docker run -d --name Olen -it -p 8181:8080 myapp:1 '''   
            } 
            stage('Docker Check') {
                long start_time = System.currentTimeMillis();
                long wait_time = 15000;
                long end_time = start_time + wait_time
                sleep 10
                sh '''docker ps -a 
                netstat -tnlp'''
                while(Response!="HTTP/1.1 200" && (System.currentTimeMillis() < end_time)){
                    def Curl = "curl -I http://10.28.12.209:8181/health".execute().text
                    Response = Curl[0..11]
                    println Response
                }
                
            }
            stage('Mail'){
                if(Response.equals("HTTP/1.1 200")) {
                    println Response
                    println env.shortCommit
                    env.BUILD_STATUS = "SUCCESS"
                    env.shortCommit = sh(returnStdout: true, script: "git log -n 1 --pretty=format:\'%h\'").trim()
                    emailext(subject: subject, body: body, to: to, replyTo: '');
                   // mail bcc: '', body: 'Success "${env.shortCommit}', cc: '', from: '', replyTo: '', subject: 'Build status', to: 'Maksym_Husak@epam.com'
                }else {
                      System.exit(1)
                }
            }           
}catch (all) {
env.BUILD_STATUS = "FAILURE"
env.shortCommit = sh(returnStdout: true, script: "git log -n 1 --pretty=format:\'%h\'").trim()
emailext(subject: subject, body: body, to: to, replyTo: '');    
}finally {

    sh 'docker rm -f Olen'
    deleteDir()
}
}*/
