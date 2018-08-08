node {
def mvnHome
def Response
def sonarHome = tool name: 'sonarqube', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
mvnHome = tool 'maven'
         try{
            stage('Git-Checkout') {
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '6da246df-c194-4f83-bdfa-9edee7ca39a2', url: 'https://github.com/Sanwel/JavaApp']]])
            }
            stage ('Build') {
                sh "'${mvnHome}/bin/mvn' -Dmaven.test.failure.ignore clean package"
            }
/*            stage ('SonarQube testing') {
                withSonarQubeEnv('sonarqube') {
                    sh "${sonarHome}/bin/sonar-scanner -Dsonar.projectKey=Simple-App -Dsonar.projectName=Simple-App -Dsonar.projectVersion=$PROJECT_VERSION -Dsonar.sources=src/main/java/rd/pingable/rest/"
                }
            }*/
            stage ('Dockerize') {
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
                    println Olen
                    shortCommit = sh(returnStdout: true, script: "git log -n 1 --pretty=format:\'%h\'").trim()
                    println shortCommit
                    mail bcc: '', body: 'Success', cc: '', from: '', replyTo: '', subject: 'Build status', to: 'Maksym_Husak@epam.com'
                }else {
                      System.exit(1)
                }
            }           
}catch (all) {
shortCommit = sh(returnStdout: true, script: "git log -n 1 --pretty=format:\'%h\'").trim()
mail bcc: '', body: 'Error', cc: '', from: '', replyTo: '', subject: 'Build status', to: 'Maksym_Husak@epam.com'
currentBuild.result = 'FAILURE'
}finally {

    sh 'docker rm -f Olen'
    deleteDir()
}
}
